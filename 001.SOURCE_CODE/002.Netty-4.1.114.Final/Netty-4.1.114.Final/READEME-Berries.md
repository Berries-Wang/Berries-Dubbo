# Netty 中一些概念
## Server创建流程
1. 套接字创建，端口监听: socket套接字 ， 端口绑定，套接字监听: sun.nio.ch.ServerSocketChannelImpl#bind



提交任务：启动EventLoop线程 
  ->  EventLoop 执行task（即在Event内部执行）
     -> Channel 注册
         -> 调用PendingHandlerCallback回调函数，

io.netty.channel.ChannelPipeline.addLast(io.netty.channel.ChannelHandler...) 添加 pendingHandlerCallbackHead
```log
callHandlerCallbackLater(AbstractChannelHandlerContext, boolean):1081, DefaultChannelPipeline (io.netty.channel), DefaultChannelPipeline.java
internalAdd(EventExecutorGroup, String, ChannelHandler, String, DefaultChannelPipeline$AddStrategy):195, DefaultChannelPipeline (io.netty.channel), DefaultChannelPipeline.java
addLast(EventExecutorGroup, String, ChannelHandler):229, DefaultChannelPipeline (io.netty.channel), DefaultChannelPipeline.java
addLast(EventExecutorGroup, ChannelHandler[]):332, DefaultChannelPipeline (io.netty.channel), DefaultChannelPipeline.java
addLast(ChannelHandler[]):321, DefaultChannelPipeline (io.netty.channel), DefaultChannelPipeline.java
init(Channel):135, ServerBootstrap (io.netty.bootstrap), ServerBootstrap.java
initAndRegister():341, AbstractBootstrap (io.netty.bootstrap), AbstractBootstrap.java
doBind(SocketAddress):290, AbstractBootstrap (io.netty.bootstrap), AbstractBootstrap.java
bind(SocketAddress):284, AbstractBootstrap (io.netty.bootstrap), AbstractBootstrap.java
bind(int):262, AbstractBootstrap (io.netty.bootstrap), AbstractBootstrap.java
main(String[]):66, EchoServer (io.netty.example.echo), EchoServer.java
```
往ChannelPipeline添加的任务(pipeline.addLast)何时执行