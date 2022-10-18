package com.xiao.nettydemo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {

    // channel 数据传输
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to =new FileOutputStream("to.txt").getChannel();
        ) {
            // 效率高， 底层会利用操作系统的零拷贝进行优化, 2g 数据
            long size = from.size();
            for (long left = size; left>0;){
                // left 变量代表还剩余多少字节
                left -= from.transferTo((size - left), size,to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
