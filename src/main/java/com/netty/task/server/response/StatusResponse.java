package com.netty.task.server.response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by nikolay on 27.03.2015.
 */
public class StatusResponse extends AbstractResponse {

    private static final String STATUS = "/Status";
    private final String responseBodyText = "Status";

    public StatusResponse(HttpRequest httpRequest,ChannelHandlerContext ctx){
        setCtx(ctx);
        setKeepAlive(HttpHeaders.isKeepAlive(httpRequest));
        addResponseHeader(responseBodyText);
    }
    @Override
    void processResponse(HttpObject currentObj, ChannelHandlerContext ctx) {

    }
}
