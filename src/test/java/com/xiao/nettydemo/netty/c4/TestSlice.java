package com.xiao.nettydemo.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.StringUtil;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

public class TestSlice {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);
        // 在切片过程中, 没有发生数据复制
        ByteBuf s1 = buf.slice(0, 5);
        // 'a','b','c','d','e','f','g','h','i','j'
        ByteBuf s2 = buf.slice(5, 5);
        log(s1);
        log(s2);
        System.out.println("=========================");
        s1.setByte(0,'b');
        log(s1);
        log(buf);

    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(StringUtil.NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }

}
