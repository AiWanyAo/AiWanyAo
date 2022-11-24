package com.xiao.nettydemo.server.handler;

import com.xiao.nettydemo.message.GroupChatRequestMessage;
import com.xiao.nettydemo.message.GroupChatResponseMessage;
import com.xiao.nettydemo.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());
        if (channels!=null){
            channels.forEach(channel -> channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent())));
        }

    }
}
