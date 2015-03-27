package com.netty.task.server.response;

import com.sun.deploy.net.HttpRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;

/**
 * Created by nikolay on 27.03.2015.
 */
abstract class AbstractResponse {

    private StringBuilder responseHeader;
    private StringBuilder responseBody;
    private ChannelHandlerContext ctx;
    private HttpRequest httpRequest;
    private FullHttpResponse fullHttpResponse;
    private boolean keepAlive;

    AbstractResponse(){

    }



    public FullHttpResponse getFullHttpResponse() {
        return fullHttpResponse;
    }

    public void setFullHttpResponse(FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }

    public StringBuilder getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(StringBuilder responseHeader) {
        this.responseHeader = responseHeader;
    }

    public void addResponseHeader(String stringAddTo) {
        getResponseHeader().append(stringAddTo);
    }

    public StringBuilder getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(StringBuilder responseBody) {
        this.responseBody = responseBody;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

   abstract void processResponse(HttpObject currentObj, ChannelHandlerContext ctx);


}
