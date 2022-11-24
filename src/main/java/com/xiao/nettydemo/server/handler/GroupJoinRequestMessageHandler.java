package com.xiao.nettydemo.server.handler;

import com.xiao.nettydemo.message.GroupJoinRequestMessage;
import com.xiao.nettydemo.message.GroupJoinResponseMessage;
import com.xiao.nettydemo.server.session.Group;
import com.xiao.nettydemo.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        if (group!=null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true,msg.getUsername()+"加入"+msg.getGroupName()));
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,"加入失败"));
        }
    }
}
