package com.xiao.nettydemo.server.handler;

import com.xiao.nettydemo.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RPCResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                      序号       用来接受结果的 promise 对象
    public static final Map<Integer, Promise<Object>> PROMISE = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}",msg);
        // 拿到空的 promise
        Promise<Object> promise = PROMISE.get(msg.getSequenceId());
        if (promise!=null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue!=null){
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(returnValue);
            }
        }

    }
}
