package com.xiao.nettydemo.server;

import com.xiao.nettydemo.server.initializer.ChannelInitializerMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        // 1.客户端通过 .option() 方法配置参数 给 SocketChannel 配置参数

        // new ServerBootstrap().option() // 是给 ServerSocketChannel 配置参数
        // new ServerBootstrap().childOption() // 给 SocketChannel 配置参数


        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_RCVBUF,2);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializerMessage());
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
