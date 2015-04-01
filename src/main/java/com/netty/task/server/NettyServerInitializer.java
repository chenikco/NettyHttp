package com.netty.task.server;



import com.netty.task.server.handlers.NettyServerHandler;
import com.netty.task.server.handlers.NettyTrafficStatistics;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
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
        p.addLast(new NettyTrafficStatistics(0));
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new NettyServerHandler());

    }
}



