package com.netty.task.server;

import com.netty.task.server.beans.NettyStatistics;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


/**
 * Created by nikolay on 29.03.2015.
 */
public class StatusPageBuilder {
    private NettyStatistics nettyStatistics = NettyStatistics.getInstance();
    public String getStatusText() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><head>");
        stringBuilder.append( "<html>");
        stringBuilder.append( "<title></title>");
        stringBuilder.append( "</head>");
        stringBuilder.append( "<body>");
        stringBuilder.append( "<h1>Server statistics </h1>");
        stringBuilder.append( "<p>Total count connections: " );
        stringBuilder.append( nettyStatistics.getTotalCountConnections());
        stringBuilder.append(  " <p>");
        stringBuilder.append( "<p>Unique requests by IP: " );
        stringBuilder.append( nettyStatistics.getNumberOfIpRequests());
        stringBuilder.append(" <p>");
        stringBuilder.append("<p>Connections active now: ");
        stringBuilder.append( nettyStatistics.getAllChannels().size());
        stringBuilder.append(" <p>");
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
        stringBuilder.append(nettyStatistics.getIpRequests());
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
        stringBuilder.append(nettyStatistics.getRedirects());
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
        stringBuilder.append("        Start date");
        stringBuilder.append("        </th>");
        stringBuilder.append("<th>");
        stringBuilder.append("        sent_bytes");
        stringBuilder.append("</th>");
        stringBuilder.append("<th>");
        stringBuilder.append("        received_bytes");
        stringBuilder.append("        </th>");
        stringBuilder.append("<th>");
        stringBuilder.append("speed (bytes/sec)  ");
        stringBuilder.append("        </th>");
        stringBuilder.append("</tr>");
        stringBuilder.append(nettyStatistics.getConnections());
        stringBuilder.append("</table>");
        stringBuilder.append("</body>");
        stringBuilder.append("<style> table, th, td {  border: 1px solid black; border-collapse: collapse; text-align: center;  }  </style> ");
        stringBuilder.append("</html>");
        return  stringBuilder.toString();
    }

    public void pageNotFound(ChannelHandlerContext ctx, String requestUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The requested URL ");
        stringBuilder.append(requestUrl);
        stringBuilder.append( " not found.");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer(stringBuilder.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=utf-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
