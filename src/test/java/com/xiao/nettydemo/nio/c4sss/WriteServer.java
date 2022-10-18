package com.xiao.nettydemo.nio.c4sss;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    // 向客户端发送数据
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true){
            selector.select();
            Iterator<SelectionKey> itera = selector.selectedKeys().iterator();
            while (itera.hasNext()){
                SelectionKey key = itera.next();
                itera.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    // 注意要先设置 非阻塞 在注册如selector , 不然 exc 警告
                    sc.configureBlocking(false);
                    SelectionKey sckey = sc.register(selector, 0, null);
                    sckey.interestOps(SelectionKey.OP_READ);
                    StringBuilder sb = new StringBuilder();
                    // 1. 向客户端发送数据
                    for (int i = 0; i <5000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

                    // 2. 返回值代表实际写入的字节数
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 判读是否有剩余内容
                    if (buffer.hasRemaining()){
                        // 4. 关注可写事件
                        sckey.interestOps(sckey.interestOps() + SelectionKey.OP_WRITE);
                        // 5. 把未写完的数据挂到 sckey 上
                        sckey.attach(buffer);
                    }

                }else if (key.isWritable()){
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();


                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 6. 清理操作
                    if (!buffer.hasRemaining()){
                        key.attach(null); // 需要清理buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }

}
