package pl.edu.pk.student.tomaszkisiel.jmb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class JmbServer {
    private static final int PORT = 3000;

    public static void main(String[] args) throws Exception {
        new JmbServer().run(PORT);
    }

    public void run(final int port) throws Exception {
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(master, slave)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new JmbServerHandler());
                        }
                    });

            ChannelFuture future = server.bind(port).sync();
            System.out.printf("Server listening on port %d...%n", port);
            future.channel().closeFuture().sync();
        } finally {
            master.shutdownGracefully();
            slave.shutdownGracefully();
        }
    }
}
