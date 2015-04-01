package com.netty.task.server.beans.response;

import com.netty.task.server.StatusPageBuilder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Created by nikolay on 01.04.2015.
 */
public class StatusResponse extends BaseResponse {
    public StatusResponse(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        super(channelHandlerContext, httpRequest);
    }

    @Override
    public void doResponse(){

        StatusPageBuilder statusPageBuilder = new StatusPageBuilder();
        addString(statusPageBuilder.getStatusText());
        setFullHttpResponse( new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.copiedBuffer(getStringWrite().toString(), CharsetUtil.UTF_8)));
        getFullHttpResponse().headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");
        getChannelHandlerContext().writeAndFlush(getFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);

    }
}
