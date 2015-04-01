package com.netty.task.server.beans.response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * Created by nikolay on 31.03.2015.
 */
public class NotFoundResponse extends BaseResponse {


   public NotFoundResponse(ChannelHandlerContext channelHandlerContext,HttpRequest httpRequest){
       super(channelHandlerContext, httpRequest);

   }

    @Override
    public void doResponse(){

        addString("The requested URL " + getHttpRequest().getUri() + "not found.");
        setFullHttpResponse( new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, NOT_FOUND, Unpooled.copiedBuffer(getStringWrite().toString(), CharsetUtil.UTF_8)));
        getChannelHandlerContext().writeAndFlush(getFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);
    }
}
