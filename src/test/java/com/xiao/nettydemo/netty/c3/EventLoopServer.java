package com.xiao.nettydemo.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 添加一个独立的 EventLoopGroup 任务,
 * .addLast(group,"handle2",new ChannelInboundHandlerAdapter(){} , 使用一个新的 group
 */
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) {
        // 细分2: 创建一个独立的 EventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // boos and worker

                // 细分1: boss 只负责 accept 事件     worker 只负责 socketChannel 上的读写
                //
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast("handle1",new ChannelInboundHandlerAdapter(){
                            @Override           // ByteBuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                                // 交给下一个handler
                                ctx.fireChannelRead(msg); // 将消息传给下一个 handler
                            }
                        }).addLast(group,"handle2",new ChannelInboundHandlerAdapter(){
                            @Override           // ByteBuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);
    }

}
