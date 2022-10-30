package com.xiao.nettydemo.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * 客户端
 */
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        // 2. 带有 Future . Promise 的类型都是和 异步方法配套使用, 用来处理结果
        // 1. 启动器类
        Channel channel = new Bootstrap()
                // 2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择客户端 channel 的实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5. 连接到服务器
                // 异步非阻塞, main 发起了调用, 真正执行 connect 是 nio 线程
                .connect(new InetSocketAddress("localhost", 8080))  // 1s 后
                // 2.1 使用 sync 方法同步处理结果
                .sync() // 阻塞住当前线程, 直到nio线程连接建立完毕
                // 2.2 使用 addListener(回调对象) 方法异步处理结果
//                .addListener(new ChannelFutureListener() {
//                    @Override
//                    // 在 nio 线程连接建立好之后, 会调用 operationComplete
//                    public void operationComplete(ChannelFuture future) throws Exception {
//                            future.channel();
//                    }
//                })
                // 无阻塞向下执行获取 channel 因为 connect 是异步建立的 channel 是主线程 所以 channel 没有建立连接 sync 同步
                .channel();
        System.out.println(channel);
        System.out.println("");



    }

}
