package io.netty.example.idleStateHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.echo.EchoServerHandler;
import io.netty.example.util.ServerUtil;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * IdleStateHandler 学习
 **/
public class IdleStateHandlerStuServer {
    public static void main(String[] args) throws InterruptedException, CertificateException, SSLException {
        final SslContext sslCtx = ServerUtil.buildSslContext();


        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        EchoServerHandler echoServerHandler = new EchoServerHandler();

        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup,childGroup)
            .handler(new LoggingHandler())
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    if (sslCtx != null) {
                        p.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    //p.addLast(new LoggingHandler(LogLevel.INFO));
                    p.addLast(echoServerHandler);
                    p.addLast(new IdleStateHandler(10,15,15));
                    p.addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            if (evt instanceof IdleStateEvent) {
                                IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
                                if (idleStateEvent.state() == IdleState.READER_IDLE) {
                                    System.out.println("读空闲超时");
                                } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                                    System.out.println("写空闲超时");
                                } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                                    System.out.println("读写空闲超时");
                                }
                            } else {
                                super.userEventTriggered(ctx, evt);
                            }
                        }
                    });
                }
            });
        ChannelFuture startFuture = server.bind(8080).sync();
        startFuture.channel().closeFuture().sync();

    }
}
