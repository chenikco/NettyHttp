package com.netty.task.server.beans.response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by nikolay on 31.03.2015.
 */
public class BaseResponse {



    private ChannelHandlerContext channelHandlerContext;
    boolean keepAlive;


    private HttpRequest httpRequest;

    private FullHttpResponse fullHttpResponse;



    private StringBuilder stringWrite;

    public BaseResponse(ChannelHandlerContext channelHandlerContext,HttpRequest httpRequest){
        this.channelHandlerContext = channelHandlerContext;
        this.httpRequest = httpRequest;
        this.keepAlive = HttpHeaders.isKeepAlive(getHttpRequest());
        setStringWrite(new StringBuilder());

    }

    public ChannelHandlerContext getChannelHandlerContext() {
    return channelHandlerContext;
}

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
    public FullHttpResponse getFullHttpResponse() {
        return fullHttpResponse;
    }

    public void setFullHttpResponse(FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }
    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    public StringBuilder getStringWrite() {
        return stringWrite;
    }

    public void setStringWrite(StringBuilder stringWrite) {
        this.stringWrite = stringWrite;
    }

    public void addString (String string){
        getStringWrite().append(string);
    }

    public void doResponse() {
        setFullHttpResponse(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,   HttpResponseStatus.NOT_IMPLEMENTED));
        getChannelHandlerContext().writeAndFlush(getFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);
    }


     public void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx,StringBuilder contentWrite) {

         //   boolean keepAlive = HttpHeaders.isKeepAlive(req);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, currentObj.getDecoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                    Unpooled.copiedBuffer(contentWrite.toString(), CharsetUtil.UTF_8));

            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");

            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            ctx.write(response);

            if (!keepAlive) {

                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.flush();
            }
        }

}
