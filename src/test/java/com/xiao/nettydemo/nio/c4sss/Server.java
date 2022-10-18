package com.xiao.nettydemo.nio.c4sss;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.xiao.nettydemo.nio.ByteBufferUtil.debugAll;

@Slf4j
public class Server {
    // 处理消息边界
    public static void main(String[] args) throws IOException {
        // 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后， 通过它可以知道事件和那个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}",sscKey);

        ssc.bind(new InetSocketAddress(8080));

        while (true){
            // 3. select 方法, 没有事件发生， 线程阻塞， 有事件， 线程才会回复运行
            // select 在事件未处理时， 它不会阻塞, 事件发生后，要么处理， 要么取消， 不能置之不理
            selector.select();
            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                log.debug("key:{}",key);
                // 5. 区分事件类型
                if (key.isAcceptable()){    // 如果是 accept
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16); // attachment
                    // 将一个 ByteBuffer 作为附件关联到 selectionKey 上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}:",sc);
                }else if (key.isReadable()){    // 如果是 read
                    try {
                        SocketChannel channel = (SocketChannel)key.channel(); // 拿到触发事件的channel\
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer); // 如果是正常断开， read 的方法的返回值是 -1
                        if (read == -1){
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer); // 01234567890abcdef
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 因为客户端断开了， 因此需要将 key 取消（从selector 的key集合中真正删除）
                    }
                }
                // 处理key 时， 要从selectedKeys 集合中删除， 否则下次处理就会有问题
                iter.remove();
                // 取消
//                key.cancel();
            }
        }

    }


    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i <source.limit(); i++) {
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读， 向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact(); // 01234567890abcdef
    }

}
