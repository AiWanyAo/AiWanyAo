package com.xiao.nettydemo.client;

import com.xiao.nettydemo.message.RpcRequestMessage;
import com.xiao.nettydemo.protocol.MessageCoderSharable;
import com.xiao.nettydemo.protocol.ProcotolFrameDecoder;
import com.xiao.nettydemo.protocol.SequenceIdGenerator;
import com.xiao.nettydemo.server.handler.RPCResponseMessageHandler;
import com.xiao.nettydemo.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RPCClientManager {

    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);
        System.out.println(service.sayHello("zhangsan"));
        System.out.println(service.sayHello("lisi"));

    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serviceClass){
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nexId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            // 3.准备一个空 Promise 对象， 来接收结果            指定 promise 对象接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RPCResponseMessageHandler.PROMISE.put(sequenceId,promise);

            // 2. 将消息对象发送出去
            getChannel().writeAndFlush(msg);

            // 4. 等待 promise 结果
            promise.await();
            if (promise.isSuccess()){
                // 调用正常
                return promise.getNow();
            }else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }


    private static Channel channel = null;
    private static final Object LOCK = new Object();

    // 获取唯一的 channel 对象
    public static Channel getChannel(){
        if (channel != null){
            return channel;
        }
        synchronized (LOCK){
            if (channel != null){
                return channel;
            }
            initChannel();
            return channel;
        }
    };

    // 初始化 Channel 方法
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCoderSharable MESSAGE_CODEC = new MessageCoderSharable();

        // rpc 响应消息处理器，待实现
        RPCResponseMessageHandler RPC_HANDLER = new RPCResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }

}
