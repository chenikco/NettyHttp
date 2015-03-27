package com.netty.task.server;


import com.netty.task.server.handlers.HttpRequestHandler;
import com.netty.task.server.handlers.NettyHelloHandler;
import com.netty.task.server.handlers.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;


/**
 * Created by nikolay on 25.03.2015.
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {



    public NettyServerInitializer() {

    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new NettyHelloHandler());
        p.addLast(new NettyServerHandler());
        p.addLast(new HttpRequestHandler());
    }
}



