package com.netty.task.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author <a href="mailto:norman.maurer@googlemail.com">Norman Maurer</a>
 */

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.content().writeBytes(getContent().getBytes(CharsetUtil.UTF_8));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ChannelFuture future = ctx.writeAndFlush(response);

            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
    protected String getContent() {
        return "This content is transmitted via HTTP\r\n";
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
