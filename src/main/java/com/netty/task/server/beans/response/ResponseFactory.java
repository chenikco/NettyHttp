package com.netty.task.server.beans.response;

import com.netty.task.server.Consts;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by nikolay on 31.03.2015.
 */
public class ResponseFactory  {

   private ChannelHandlerContext channelHandlerContext;
   private HttpRequest httpRequest;

    public ResponseFactory(ChannelHandlerContext channelHandlerContext,HttpRequest httpRequest) {
      this.channelHandlerContext = channelHandlerContext;
      this.httpRequest = httpRequest;
   }
   public BaseResponse getResponse(String uri){

       if (uri.equals(Consts.HELLO)) {
           return new HelloResponse(channelHandlerContext,httpRequest);
       }
       if (uri.matches(Consts.REDIRECT_REGEXP)) {
           return new RedirectResponse(channelHandlerContext,httpRequest);
       }
       if (uri.equals(Consts.STATUS)) {
           return new StatusResponse(channelHandlerContext,httpRequest);
       }

       return new NotFoundResponse(channelHandlerContext,httpRequest);
   }





}
