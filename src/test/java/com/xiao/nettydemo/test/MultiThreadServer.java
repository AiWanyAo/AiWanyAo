package com.xiao.nettydemo.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xiao.nettydemo.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {

        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 1. 创建固定数量的worker 并初始化
        //Runtime.getRuntime().availableProcessors() 获取当前系统的核心数
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i <workers.length; i++) {
            workers[i] = new Worker("worker-"+i);
        }
//        Worker worker = new Worker("worker-0");

        AtomicInteger index = new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}",sc.getRemoteAddress());
                    // 2. 关联 selector
                    log.debug("before register...{}",sc.getRemoteAddress());
                    // round robin 负载均衡算法
                    workers[index.getAndIncrement() % workers.length].register(sc); // boos 调用 初始化 selector, 启动 worker-0
                    log.debug("after register...{}",sc.getRemoteAddress());
                }
            }
        }
    }


    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;

        private boolean start = false; // 还未初始化

        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程, 和Selector
        public synchronized void register(SocketChannel socketChannel) throws IOException {
            if (!start){
                thread = new Thread(this,name);
                selector = Selector.open();
                thread.start();
                start = true;
            }
            // 向队列添加任务, 但这个任务并没有立刻执行
            queue.add(() ->{
                try {
                    socketChannel.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });

            selector.wakeup();
//            socketChannel.register(selector,SelectionKey.OP_READ,null);
        }

        @Override
        public void run() {
            while (true){
                try {
                    selector.select(); // worker-0 阻塞, wakeup
                    Runnable task = queue.poll();
                    if (task != null){
                        // 这个 run 并不是创建线程, 而是执行队列中的task
                        task.run(); // 执行了 socketChannel.register(selector,SelectionKey.OP_READ,null);
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);

                            SocketChannel channel = (SocketChannel)key.channel();
                            log.debug("read...{}",channel.getRemoteAddress());
                            try {
                                int read = channel.read(buffer);
                                if (read == -1){
                                    key.cancel();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                            }
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
