package com.xiao.nettydemo.server.initializer;

import com.xiao.nettydemo.protocol.MessageCoderSharable;
import com.xiao.nettydemo.protocol.ProcotolFrameDecoder;
import com.xiao.nettydemo.server.handler.*;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelInitializerMessage extends ChannelInitializer<SocketChannel> {

    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    MessageCoderSharable MESSAGE_CODEC = new MessageCoderSharable();

    LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
    ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
    GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
    GroupChatRequestMessageHandler GROUP_CHAT_HANDLE = new GroupChatRequestMessageHandler();
    GroupJoinRequestMessageHandler GROUP_JOIN_HANDLE = new GroupJoinRequestMessageHandler();
    GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLE = new GroupMembersRequestMessageHandler();
    GroupQuitRequestMessageHandler GROUP_QUIT_HANDLE = new GroupQuitRequestMessageHandler();
    QuitHandler QUIT_HANDLER = new QuitHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ProcotolFrameDecoder());
        ch.pipeline().addLast(LOGGING_HANDLER);
        ch.pipeline().addLast(MESSAGE_CODEC);

        // 用来判断是不是读空闲时间过长, 或 写时间过长
        // 5s 内如果没有收到 channel 的数据, 会触发一个事件
        ch.pipeline().addLast(new IdleStateHandler(5,0,0));
        ch.pipeline().addLast(new ChannelDuplexHandler(){
            // 用来触发特殊事件
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                IdleStateEvent event = (IdleStateEvent)evt;
                if (event.state() == IdleState.READER_IDLE){
                    log.debug("已经 5s 没有读到数据了");
                    ctx.channel().close();
                }
            }
        });
        ch.pipeline().addLast(LOGIN_HANDLER);
        ch.pipeline().addLast(CHAT_HANDLER);
        ch.pipeline().addLast(GROUP_CREATE_HANDLER);
        ch.pipeline().addLast(GROUP_CHAT_HANDLE);
        ch.pipeline().addLast(GROUP_JOIN_HANDLE);
        ch.pipeline().addLast(GROUP_MEMBERS_HANDLE);
        ch.pipeline().addLast(GROUP_QUIT_HANDLE);
        ch.pipeline().addLast(QUIT_HANDLER);
    }
}
