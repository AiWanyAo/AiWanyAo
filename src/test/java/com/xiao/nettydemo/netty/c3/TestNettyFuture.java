package com.xiao.nettydemo.netty.c3;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * Netty Future ,
 * eventLoop.submit().addListener() , 回调函数
 * or eventLoop.submit().get();
 *
 * 3.3 Future & Promise
 */

@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(() -> {
            log.debug("执行计算");
            Thread.sleep(1000);
            return 70;
        });
        // 同步等待结果
//        log.debug("等待结果");
//        Integer integer = future.get();
//        log.debug("结果:{}",integer);

        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("接受结果:{}",future.getNow());
            }
        });

    }

}
