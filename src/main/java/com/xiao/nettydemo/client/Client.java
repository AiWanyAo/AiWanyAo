package com.xiao.nettydemo.client;

import com.xiao.nettydemo.message.LoginRequestMessage;
import com.xiao.nettydemo.message.LoginResponseMessage;
import com.xiao.nettydemo.protocol.MessageCoderSharable;
import com.xiao.nettydemo.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Client {
    public static void main(String[] args) throws UnknownHostException {

        String hostName = InetAddress.getLocalHost().getHostName();

        NioEventLoopGroup group = new NioEventLoopGroup();

        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCoderSharable MESSAGE_CODEC = new MessageCoderSharable();

        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);

        AtomicBoolean LOGIN = new AtomicBoolean(false);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){

                        // 在连接建立后 触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责接收用户在控制台的输入, 负责向服务器发送各种各样的消息
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                log.debug("请输入用户名:");
                                String username = scanner.nextLine();
                                log.debug("请输入密码:");
                                String password = scanner.nextLine();
                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                // 发送消息
                                ctx.writeAndFlush(message);

                                log.debug("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                // 如果登录失败
                                if (!LOGIN.get()){
                                    ctx.channel().close();
                                    return;
                                }
                                while (true){
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmemvers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    System.out.print(hostName+":");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":

                                            break;
                                        case "gsend":
                                            break;
                                        case "gcreate":
                                            break;
                                        case "gmemvers":
                                            break;
                                        case "gjoin":
                                            break;
                                        case "gquit":
                                            break;
                                        case "quit":
                                            break;

                                    }
                                }
                            },"system in").start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg:{}",msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()){
                                    // 如果登录成功
                                    LOGIN.set(true);
                                }
                            }
                            // 唤醒 system in 线程
                            WAIT_FOR_LOGIN.countDown();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            // ...
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error",e);
        }finally {
            group.shutdownGracefully();
        }
    }
}
