package com.netty.task.server.beans.response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import java.util.concurrent.TimeUnit;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Created by nikolay on 31.03.2015.
 */
public class HelloResponse extends BaseResponse{

    public HelloResponse(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        super(channelHandlerContext, httpRequest);
    }

    @Override
    public void doResponse(){

        addString("Hello World!!!");
        setFullHttpResponse( new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.copiedBuffer(getStringWrite().toString(), CharsetUtil.UTF_8)));
        getFullHttpResponse().headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");
        getChannelHandlerContext().executor().schedule(
                new Runnable() {
                    @Override
                    public void run() {

                        getChannelHandlerContext().writeAndFlush(getFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);
                    }
                }, 10, TimeUnit.SECONDS);

    }
}
