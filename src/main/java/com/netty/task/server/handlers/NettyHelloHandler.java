package com.netty.task.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class NettyHelloHandler extends ChannelInboundHandlerAdapter {


    private static final StringBuilder content = new StringBuilder();
    private static final String HELLO = "/Hello";
    private static final String STATUS = "/Status";
    private static final String REDIRECT_REGEXP = "/redirect[?]url=.*";

    HttpRequest req = null;


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

//TODO Try to do different handlers for Uri

        String requestUrl;

        if (msg instanceof HttpRequest) {
           req = (HttpRequest) msg;

            requestUrl = req.getUri().trim();
            content.delete(0, content.length());
            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }


            switch (requestUrl) {

                case HELLO:

                    content.append("Hello World!");
                    ctx.executor().schedule(
                            new Runnable() {
                                @Override
                                public void run() {

                                    writeResponse(req, ctx);
                                }
                            }, 10, TimeUnit.SECONDS);
                    return;
                case STATUS:
                    content.append(getStatusText());

                    writeResponse(req, ctx);
                    return;
                default:
                    if (requestUrl.matches(REDIRECT_REGEXP)){
                         Map<String, List<String>> params = new QueryStringDecoder(req.getUri()).parameters();
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                        response.headers().set(HttpHeaders.Names.LOCATION, "http://" + params.get("url").get(0));
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

                    }

            }

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();        ctx.close();
    }
    //TODO try to create method with using Timer or newSingleThreadScheduledExecutor()



    private void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(req);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, currentObj.getDecoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(content.toString(), CharsetUtil.UTF_8));

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);

        if (!keepAlive) {
            // If keep-alive is off, close the connection once the content is fully written.
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.flush();
        }
    }


private String getStatusText() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><head>");

    stringBuilder.append( "<html>");

    stringBuilder.append( "<title></title>");
    stringBuilder.append( "</head>");
    stringBuilder.append( "<body>");
    stringBuilder.append( "<h1>Server statistics </h1>");

    stringBuilder.append( "<p>Total count connections: <p>");
    stringBuilder.append( "<p>Unique requests by IP: <p>");
    stringBuilder.append( "<p>Connections active now: <p>");
    stringBuilder.append( "<table>");

    stringBuilder.append("<tr>");
    stringBuilder.append("<th>");
    stringBuilder.append("IP address");
    stringBuilder.append("</th>");
    stringBuilder.append("<th>");
    stringBuilder.append("Count unique requests");
    stringBuilder.append("</th>");


    stringBuilder.append("</tr>");
    stringBuilder.append("<tr>");
        //    stringBuilder.append("<tr align="center" >
    stringBuilder.append("<td> 192.168.0.1 </td>");
    stringBuilder.append("<td> 4 </td>");
    stringBuilder.append("</tr>");
      //      stringBuilder.append("<tr align=")+center">
    stringBuilder.append("<tr>");
    stringBuilder.append("<td> localhost </td>");
    stringBuilder.append("<td> 6 </td>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    stringBuilder.append("<p>Counter of requests:  <p>");
    stringBuilder.append("<table>");
    stringBuilder.append("<tr>");
    stringBuilder.append("<th>");
    stringBuilder.append("IP address");
    stringBuilder.append("</th>");
    stringBuilder.append("<th>");
    stringBuilder.append("Count requests");
    stringBuilder.append("</th>");
    stringBuilder.append("<th>");
    stringBuilder.append("Last request's time");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("<tr>") ;
          //  "align="center" >
    stringBuilder.append("<td> 192.168.0.1 </td>");
    stringBuilder.append("<td> 4 </td>");
    stringBuilder.append("<td> 12:28 </td>");
    stringBuilder.append("</tr>");
    stringBuilder.append("<tr>");
    //stringBuilder.append("<tr align="center">
    stringBuilder.append("<td> localhost </td>");
    stringBuilder.append("<td> 6 </td>");
    stringBuilder.append("<td> 23:00 </td>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    stringBuilder.append("<p>Counter of Url's redirections:  <p>");
    stringBuilder.append("        <table>");
    stringBuilder.append("<tr>");
    stringBuilder.append("<th>");
    stringBuilder.append("        URL");
    stringBuilder.append("        </th>");
    stringBuilder.append("<th>");
    stringBuilder.append("        Count");
    stringBuilder.append("        </th>");
    stringBuilder.append("</tr>");

    //stringBuilder.append("<tr align="center" >");
    stringBuilder.append("<tr>");
    stringBuilder.append("<td> www.google.com </td>");
    stringBuilder.append("<td> 4 </td>");

    stringBuilder.append("</tr>");
    //stringBuilder.append("<tr align="center">");
    stringBuilder.append("<tr>");
    stringBuilder.append("<td> www.microsoft.com </td>");
    stringBuilder.append("<td> 6 </td>");

    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    stringBuilder.append("<p>Log of active connections:  <p>");
    stringBuilder.append("<table>");
    stringBuilder.append("<tr>");
    stringBuilder.append("<th>");
    stringBuilder.append("        IP");
    stringBuilder.append("        </th>");
    stringBuilder.append("<th>");
    stringBuilder.append("        URI");
    stringBuilder.append("        </th>");
    stringBuilder.append("<th>");
    stringBuilder.append("        Timestamp");
    stringBuilder.append("        </th>");
    stringBuilder.append("<th>");
    stringBuilder.append("        sent_bytes");
    stringBuilder.append("</th>");
    stringBuilder.append("<th>");
    stringBuilder.append("        received_bytes");
    stringBuilder.append("        </th>");
    stringBuilder.append("<th>");
    stringBuilder.append("speed (bytes/sec)");
    stringBuilder.append("        </th>");
    stringBuilder.append("</tr>");
    //stringBuilder.append("<tr align="center" >");
    stringBuilder.append("<tr>");
    stringBuilder.append("<td> 192.168.0.1 </td>");
    stringBuilder.append("<td> www.google.com </td>");
    stringBuilder.append("<td> 10:12:01 25.03.2015 </td>");
    stringBuilder.append("<td> 1444 </td>");
    stringBuilder.append("<td> 3000 </td>");
    stringBuilder.append("<td> 25000 </td>");
    stringBuilder.append("</tr>");
    //stringBuilder.append("<tr align="center">");
    stringBuilder.append("<tr>");
    stringBuilder.append("<td> 192.168.0.1 </td>");
    stringBuilder.append("<td> www.microsoft.com </td>");
    stringBuilder.append("<td> 20:12:01 25.03.2015 </td>");
    stringBuilder.append("<td> 3444 </td>");
    stringBuilder.append("<td> 5000 </td>");
    stringBuilder.append("<td> 15000 </td>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");

    stringBuilder.append("</body>");
    stringBuilder.append("</html>");
    return  stringBuilder.toString();
}

}