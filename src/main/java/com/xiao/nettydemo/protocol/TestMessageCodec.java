package com.xiao.nettydemo.protocol;

import com.xiao.nettydemo.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        LoggingHandler loggingHandler = new LoggingHandler();

        EmbeddedChannel channel = new EmbeddedChannel(
                // 最大长度，长度偏移，长度占用字节，长度字节为基准还有几个字节是内容，剥离字节数
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                loggingHandler,
                new MessageCodec());
        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);

        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);

        // 入站
        channel.writeInbound(buf);
    }

}
