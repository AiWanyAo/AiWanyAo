package com.xiao.nettydemo.protocol;

import com.xiao.nettydemo.config.Config;
import com.xiao.nettydemo.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用, 确保接到的 ByteBuf 消息是完整的
 */

@Slf4j
@ChannelHandler.Sharable
public class MessageCoderSharable extends MessageToMessageCodec<ByteBuf,Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔术
        out.writeBytes("xiao".getBytes());
        // 2. 1 字节的版本
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节指令请求序号
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte(); // 0 ro 1
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);

        // 找到反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerType];
        // 确定具体消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Object deserialize = algorithm.deserialize(messageClass, bytes);
        log.debug("编码格式:{},{},{},{},{},{}",magicNum,version,serializerType,messageType,sequenceId,length);
        log.debug("消息内容:{}",deserialize);
        out.add(deserialize);
    }
}
