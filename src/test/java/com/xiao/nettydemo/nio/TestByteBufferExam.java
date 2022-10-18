package com.xiao.nettydemo.nio;

import java.nio.ByteBuffer;

import static com.xiao.nettydemo.nio.ByteBufferUtil.debugAll;

public class TestByteBufferExam {

    public static void main(String[] args) {
        // 黏包， 半包 处理
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
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
        source.compact();
    }

}
