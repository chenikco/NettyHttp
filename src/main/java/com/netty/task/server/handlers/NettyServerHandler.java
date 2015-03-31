package com.netty.task.server.handlers;

import com.netty.task.server.StatusPageBuilder;
import com.netty.task.server.beans.NettyStatistics;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;


/**
 * Created by nikolay on 25.03.2015.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {



    private static final String HELLO = "/Hello";
    private static final String STATUS = "/Status";
    private static final String REDIRECT_REGEXP = "/redirect[?]url=.*";
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
      //  super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {

        String requestUrl;
        StatusPageBuilder statusPageBuilder = new StatusPageBuilder();
        if (msg instanceof HttpRequest) {
            req = (HttpRequest) msg;


            requestUrl = req.getUri().trim();
            // Allow only GET methods.
            if (req.getMethod() != HttpMethod.GET) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_IMPLEMENTED);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            if (req.getUri().equals("/favicon.ico")) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, NOT_FOUND);
                StringBuilder contentNotFound = new StringBuilder();
                contentNotFound.append("The requested URL " + requestUrl + "not found.");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }


            nettyStatistics.setUri(requestUrl);

            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }


            switch (requestUrl) {

                case HELLO:
                    final StringBuilder contentHello = new StringBuilder();
                    contentHello.append("Hello World!");
                    ctx.executor().schedule(
                            new Runnable() {
                                @Override
                                public void run() {

                                    writeResponse(req, ctx,contentHello);
                                }
                            }, 10, TimeUnit.SECONDS);
                    return;
                case STATUS:
                    StringBuilder contentStatus = new StringBuilder();
                    contentStatus.append(statusPageBuilder.getStatusText());

                    writeResponse(req, ctx,contentStatus);
                    return;
                default:
                    if (requestUrl.matches(REDIRECT_REGEXP)){
                        Map<String, List<String>> params = new QueryStringDecoder(req.getUri()).parameters();
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                        response.headers().set(HttpHeaders.Names.LOCATION, "http://" + params.get("url").get(0));
                        nettyStatistics.addRedirect( response.headers().get(HttpHeaders.Names.LOCATION));
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

                    }
                    else {
                        statusPageBuilder.pageNotFound(ctx,requestUrl);
                    }
            }
       }
    }



    private void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx,StringBuilder contentWrite) {

        boolean keepAlive = HttpHeaders.isKeepAlive(req);

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