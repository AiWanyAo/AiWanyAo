package com.xiao.nettydemo.netty.c4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * new ChannelInboundHandlerAdapter() 入站
 * new ChannelOutboundHandlerAdapter() 出站
 * ctx 是向前寻找 出栈处理器
 * ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
 * ch 是从后向前找
 * ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
 */

@Slf4j
public class TestPipeline {

    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        // 1. 通过 channel 拿到 pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        // 2. 添加处理器 hand -> h1 -> h2 -> h3 -> h4 -> h5 -> h6 -> tail
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                String name = msg.toString();
                                super.channelRead(ctx, name);
                            }
                        });
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object name) throws Exception {
                                log.debug("2");
                                Student student = new Student((String) name);
                                super.channelRead(ctx, student); // 将数据传给下一个Handler , 如果不调用, 调用链会断开
                            }
                        });
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3,结果:{},class:{}",msg,msg.getClass());
                                // ctx 是向前寻找 出栈处理器
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                                // ch 是从后向前找
//                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                            }
                        });
                        pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                }).bind(8080);

    }

    @Data
    @AllArgsConstructor
    static class Student{

        private String name;

    }


}
