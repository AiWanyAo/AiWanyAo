package com.xiao.nettydemo.netty.c3;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 事件循环组,
 * 测试 EventLoop
 */

@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        // 1. 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2); // io 事件, 普通任务, 定时任务
//        EventLoopGroup defaultEventLoop = new DefaultEventLoop(); //普通任务, 定时任务
//        System.out.println(NettyRuntime.availableProcessors());
        // 2. 获取下一个事件循环对象
        log.debug("当前任务对象:{}",group.next());
        log.debug("当前任务对象:{}",group.next());
        log.debug("当前任务对象:{}",group.next());
        log.debug("当前任务对象:{}",group.next());


        // 3. 执行普通任务
        // execute
        /*group.next().submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("ok");
        });*/

        // 4. 执行定时任务
        group.next().scheduleAtFixedRate(()->{
          log.debug("ok");
        },0,1, TimeUnit.SECONDS);


        log.debug("main");

    }

}
