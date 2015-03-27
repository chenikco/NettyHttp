package com.netty.task.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by nikolay on 25.03.2015.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // called for each incoming message
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        //A random and sequential accessible sequence of zero or more bytes (octets).
        // This interface provides an abstract view for one or more primitive byte arrays (byte[]) and NIO buffers.
        ByteBuf in = (ByteBuf) msg;
        //Log the message to the console.
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        //Writes the received message back to the sender. Note that this does not yet “flush” the outbound
        //messages.
        ctx.write(in);
    }
    // called to notify the handler that the last call made to
    // channelRead() was the last message in the current batch
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //Flushes all pending messages to the remote peer. Closes the channel after the operation is complete.
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    //exceptionCaught()- called if an exception is thrown during the read operation
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //Prints the exception stack trace.
        cause.printStackTrace();
        //Closes the channel.
        ctx.close();
    }
}
