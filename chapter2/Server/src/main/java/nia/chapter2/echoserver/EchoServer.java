package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        int port = 25;
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler(); //消息处理器
        EventLoopGroup group = new NioEventLoopGroup();  // 用于事件处理
        try {
            ServerBootstrap b = new ServerBootstrap();  // 引导绑定服务器
            b.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port)) //指定端口设置套接字地址
                .childHandler(new ChannelInitializer<SocketChannel>() { // 添加一个EchoServerHandler 到子Channel 的 ChannelPipeline
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(serverHandler);  //ChannelPipeline 是一个用于注册handler的容器
                    }
                });

            ChannelFuture f = b.bind().sync(); // 异步绑定服务器
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync(); // 关闭关闭 EventLoopGroup，释放资源
        }
    }
}
