package com.netty.task.server.response;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by nikolay on 27.03.2015.
 */
public class HelloResponse extends AbstractResponse {

    private static final String HELLO = "/hello";
    private final String responseBodyText = "Hello World!!!";

    public HelloResponse(HttpRequest httpRequest,ChannelHandlerContext ctx){
        setCtx(ctx);
        setKeepAlive(HttpHeaders.isKeepAlive(httpRequest));
        addResponseHeader(responseBodyText);
    }
    @Override
    void processResponse(HttpObject currentObj, ChannelHandlerContext ctx) {

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, currentObj.getDecoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(getResponseHeader().toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");
    }






//    if (keepAlive) {
//        // Add 'Content-Length' header only for a keep-alive connection.
//        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
//        // Add keep alive header as per:
//        // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
//        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//    }
//
//    // Write the response.
//    ctx.write(response);
//
//    if (!keepAlive) {
//        // If keep-alive is off, close the connection once the content is fully written.
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//    } else {
//        ctx.flush();
//    }
}
