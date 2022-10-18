package com.xiao.nettydemo.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1. 输入输出流, 2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 从channel读取数据， 向buffer写入
            while (true){
                int len = channel.read(buffer);
                log.debug("读取到的字节数{}",len);
                if (len == -1){ // 没有内容了
                    break;
                }
                // 打印buffer的内容
                buffer.flip(); // 切换到读模式
                while (buffer.hasRemaining()){ // 是否还有剩余的数据
                    byte b = buffer.get();
                    log.debug("读取到的字节{}",(char)b);
                }
                // 清空缓冲区
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
