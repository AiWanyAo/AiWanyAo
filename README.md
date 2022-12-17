# 黑马学习Netty

## Test-基础

### nio

```java
package com.xiao.nettydemo.nio.c4;

/**
 * 单线程 ServerSocketChannel
 */
```

```java
package com.xiao.nettydemo.nio.c4s;

/**
 * 单线程,非阻塞模式 ServerSocketChannel
 */
```

```java
package com.xiao.nettydemo.nio.c4ss;

/**
 * 单线程,非阻塞模式, selector, ServerSocketChannel
 */
```

```java
package com.xiao.nettydemo.nio.test;

/**
 * 多线程,非阻塞模式, selector , ServerSocketChannel
 */
```

```java
package com.xiao.nettydemo.nio;

/**
 * 处理消息边界, 粘包半包
 */
```

### netty

```java
ChannelHandler

单工，就是两者通信单向进行，只能一个主动发信号一个被动去接受，不能角色互换。
举例:行人只能接受红绿灯的信号但是不能向红绿灯发信号，红绿灯只能发出信号不能接收信号。

半双工，两个事物都可以发信号，但是不能同时进行。
举例:类似于踢足球，只能一个传给另一个人，两个人不能同时传球，球只有一个，信道只有一个。

全双工，两个事物可以同时发送和接受信息。
举例:两个人互相打电话，你可以说也可以听电话。在Java里套接字socket就是全双工的。

 

单工、半双工和全双工

根据通信双方的分工和信号传输方向可将通信分为三种方式：单工、半双工与全双工。在计算机网络中主要采用双工方式，其中：局域网采用半双工方式，城域网和广域网采用全双年方式。

 1. 单工(Simplex)方式：通信双方设备中发送器与接收器分工明确，只能在由发送器向接收器的单一固定方向上传送数据。采用单工通信的典型发送设备如早期计算机的读卡器，典型的接收设备如打印机。 

2. 半双工(Half Duplex)方式：通信双方设备既是发送器，也是接收器，两台设备可以相互传送数据，但某一时刻则只能向一个方向传送数据。例如，步话机是半双工设备，因为在一个时刻只能有一方说话。 

3. 全双工(Full Duplex)方式：通信双方设备既是发送器，也是接收器，两台设备可以同时在两个方向上传送数据。例如，电话是全双工设备，因为双方可同时说话。
```

```java
package com.xiao.nettydemo.netty.c1;

/**
 * Netty基本模型
 */
```

```java
package com.xiao.nettydemo.netty.c3;

/**
 * 关闭Netty, EventLoop, Future, NettyFuture, Promise
 */
```

```java
package com.xiao.nettydemo.netty.c4;

/**
 * 堆内存,直接内存,buf池化
 *
 * CompositeByteBuf: 浅拷贝
 *
 * EmbeddedChannel;
 * 用于测试, 预览通道 入站 出站
 *
 * new ChannelInboundHandlerAdapter() 入站
 * new ChannelOutboundHandlerAdapter() 出站
 * ctx 是向前寻找 出栈处理器
 * ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
 * ch 是从后向前找
 * ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
 *
 * 分片, 使用同一块内存
 */
```

```java
package com.xiao.nettydemo.netty.c5;

/**
 * 解决 消息边界, 粘包, 半包
 * 短链接: 可以解决半包问题, 但是无法解决粘包问题
 * 固定长度: 可以解决粘包,半包问题, 但是额外的网络资源消耗
 *  		ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
 * 固定特殊符号
 * 			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
 * 预设长度
 * 			ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,4,1,4));
 */
```



## Java-项目

### 聊天室

### RPC