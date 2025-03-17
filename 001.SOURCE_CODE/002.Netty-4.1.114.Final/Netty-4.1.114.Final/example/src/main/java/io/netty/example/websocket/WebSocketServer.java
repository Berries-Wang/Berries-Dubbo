package io.netty.example.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.util.ServerUtil;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * WebSocket 服务端: 先读源码: {@link io.netty.example.echo.EchoServer}
 **/
public final class WebSocketServer {
    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        // Configure SSL.
        final SslContext sslCtx = ServerUtil.buildSslContext();

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // websocket基于http协议，所以要有http编解码器，服务端用HttpServerCodec
                        pipeline.addLast(new HttpServerCodec());

                        // 对写大数据流的支持
                        pipeline.addLast(new ChunkedWriteHandler());

                        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

                        // websocket 服务路径: 对应客户端写法: let wsClient = new WebSocket("ws://127.0.0.1:30001/ws_stu_01");
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws_stu_01"));

                        // 自定义Handler，处理websocket消息
                        pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
                                throws Exception {
                                // 获取客户端传输过来的消息
                                String msg_content = msg.text();
                                System.out.println("接收到数据: " + msg_content);

                                // 向客户端发送数据, 没有构造为 TextWebSocketFrame ， 则websocket客户端接收不到消息
                                ctx.channel()
                                    .writeAndFlush(new TextWebSocketFrame("你好，我已经接收到消息:「" + msg_content + "」了"));
                            }
                        });

                    }
                });

            // Start the server.
            ChannelFuture f = b.bind(30001).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
