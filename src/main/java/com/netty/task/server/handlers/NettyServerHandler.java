package com.netty.task.server.handlers;


import com.netty.task.server.beans.NettyStatistics;
import com.netty.task.server.beans.response.BaseResponse;
import com.netty.task.server.beans.response.ResponseFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;


/**
 * Created by nikolay on 25.03.2015.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {




    private NettyStatistics nettyStatistics = NettyStatistics.getInstance();

     HttpRequest req = null;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        nettyStatistics.addIpRequests(NettyStatistics.getIpFromChannel(ctx.channel()));
        ctx.flush();

    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        nettyStatistics.addChannel(ctx.channel());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {

        String requestUrl;

        if (msg instanceof HttpRequest) {
            req = (HttpRequest) msg;
            requestUrl = req.getUri().trim();

            ResponseFactory responseFactory = new ResponseFactory(ctx, req);
            BaseResponse baseResponse= responseFactory.getResponse(requestUrl);
            baseResponse.doResponse();
            nettyStatistics.setUri(requestUrl);

            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

       }
    }

}