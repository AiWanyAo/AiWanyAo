package com.xiao.nettydemo.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.StringUtil;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

/**
 * byteBuf 的创建;
 * byteBuf 的动态扩容;
 * PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
 * PooledUnsafeDirectByteBuf(ridx: 0, widx: 300, cap: 512)
 */

public class TestByteBuf {

    public static void main(String[] args) {
//        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();       堆内存
//        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();    直接内存
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();        // 直接内存, 4.1 之后 默认 采用buf 池化

        System.out.println(buf.getClass());
        log(buf);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <32; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
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
