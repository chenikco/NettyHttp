package com.netty.task.server.beans.response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import java.util.List;
import java.util.Map;


/**
 * Created by nikolay on 31.03.2015.
 */
public class RedirectResponse extends BaseResponse {
    public RedirectResponse(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        super(channelHandlerContext, httpRequest);
    }

    @Override
    public void doResponse(){
        Map<String, List<String>> params = new QueryStringDecoder(getHttpRequest().getUri()).parameters();
       setFullHttpResponse(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND));
        getFullHttpResponse().headers().set(HttpHeaders.Names.LOCATION, "http://" + params.get("url").get(0));
        getChannelHandlerContext().writeAndFlush(getFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);
    }
}
