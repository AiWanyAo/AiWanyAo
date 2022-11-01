package com.xiao.nettydemo.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.StringUtil;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

/**
 * CompositeByteBuf: 浅拷贝
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        /*ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(buf1).writeBytes(buf2);*/

        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true,buf1,buf2);
        log(buffer);
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
