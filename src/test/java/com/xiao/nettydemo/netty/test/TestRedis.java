package com.xiao.nettydemo.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 协议篇
 * Redis 协议
 */

@Slf4j
public class TestRedis {

    /**
     * set key value
     * *3 数组的元素
     * $3 set的长度
     * set
     * @param args
     */
    public static void main(String[] args) {
        final byte[] LINE = {13,10};
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf login = ctx.alloc().buffer();
                            login.writeBytes("*2".getBytes());
                            login.writeBytes(LINE);

                            login.writeBytes("$4".getBytes());
                            login.writeBytes(LINE);
                            login.writeBytes("auth".getBytes());
                            login.writeBytes(LINE);
                            login.writeBytes("$11".getBytes());
                            login.writeBytes(LINE);
                            login.writeBytes("zhang..0902".getBytes());
                            login.writeBytes(LINE);
                            ctx.writeAndFlush(login);

                            ByteBuf buf = ctx.alloc().buffer();
                            buf.writeBytes("*3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("set".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$4".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("name".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$8".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("*zhangsan".getBytes());
                            buf.writeBytes(LINE);
                            ctx.writeAndFlush(buf);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            log.debug(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }

            });
            bootstrap.connect("180.76.234.78",6379).sync();
        } catch (InterruptedException e) {
            log.error("client error",e);
        }
    }

}
