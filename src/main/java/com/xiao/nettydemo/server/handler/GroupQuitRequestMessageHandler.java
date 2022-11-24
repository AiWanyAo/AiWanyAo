package com.xiao.nettydemo.server.handler;

import com.xiao.nettydemo.message.GroupQuitRequestMessage;
import com.xiao.nettydemo.message.GroupQuitResponseMessage;
import com.xiao.nettydemo.server.session.Group;
import com.xiao.nettydemo.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().removeMember(msg.getGroupName(), msg.getUsername());
        if (group!=null){
            ctx.writeAndFlush(new GroupQuitResponseMessage(true,"退出成功"));
        }else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false,"退出失败"));
        }

    }
}
