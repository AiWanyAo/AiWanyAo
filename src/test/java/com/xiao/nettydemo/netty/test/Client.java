package com.xiao.nettydemo.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {

        ChannelFuture client = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("读取到server发送数据:{}",msg.toString());
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = client.sync().channel();
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if ("q".equals(line)){
                    channel.close(); // 异步的
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();


    }

}
