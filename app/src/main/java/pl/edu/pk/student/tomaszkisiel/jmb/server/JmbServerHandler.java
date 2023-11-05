package pl.edu.pk.student.tomaszkisiel.jmb.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class JmbServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.printf("Client connected (%s)%n" ,ctx.channel().id());
        ctx.writeAndFlush(Unpooled.copiedBuffer("First message\n", StandardCharsets.UTF_8));
        ctx.writeAndFlush(Unpooled.copiedBuffer("Second message\n", StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}