package com.netty.task.server.handlers;

import com.netty.task.server.beans.NettyConnectionLog;
import com.netty.task.server.beans.NettyStatistics;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Date;


/**
 * Created by nikolay on 29.03.2015.
 */
public class NettyTrafficStatistics extends ChannelTrafficShapingHandler {

    private NettyConnectionLog nettyConnectionLog = new NettyConnectionLog();

    public NettyTrafficStatistics(long checkInterval ) {
        super(checkInterval);

    }

    public NettyConnectionLog getNettyConnectionLog() {
        return nettyConnectionLog;
    }

    public void setNettyConnectionLog(NettyConnectionLog nettyConnectionLog) {
        this.nettyConnectionLog = nettyConnectionLog;
    }

    public void addNettyConnectionLogInStatistics (NettyConnectionLog nettyConnectionLog) {
        NettyStatistics.netty_statistics.addNettyConnectionLog(nettyConnectionLog);

    }

//    @Override
//    public void channelRead( ChannelHandlerContext ctx, Object msg) throws Exception{
//
////TODO Try to do different handlers for Uri
//        if (msg instanceof HttpRequest) {
//            HttpRequest req =(HttpRequest) msg;
//            nettyConnectionLog.setURI(req.getUri().trim());
//
//
//        }
//        super.channelRead(ctx, msg);
//    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg)
//            throws Exception {
//
//        if (msg instanceof HttpRequest) {
//            HttpRequest req = (HttpRequest) msg;
//
//            String requestUrl = req.getUri().trim();
//            nettyConnectionLog.setURI(requestUrl);
//        }
//  //      UnpooledHeapByteBuf byteMsg = (UnpooledHeapByteBuf) msg;
//      //  nettyConnectionLog.setReceivedBytes((nettyConnectionLog.getReceivedBytes()+ byteMsg.readableBytes()));
//
//       // super.channelRead(ctx, msg);
//
//    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//
//        ctx.flush();
//        super.channelReadComplete(ctx);
//       /* Writing response to request is over. Record request in statistics */
//      //  nettyStatistics.addIpRequests(NettyStatistics.getIpFromChannel(ctx.channel()));
////        if (req != null) {
////            nettyConnectionLog.setURI(req.getUri());
////        }
//    }
//
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg,
//                      ChannelPromise promise) throws Exception {
//        ByteBuf byteMsg = (ByteBuf) msg;
//        nettyConnectionLog.setSentBytes((nettyConnectionLog.getSentBytes() + byteMsg.readableBytes()));
//
//        super.write(ctx, msg, promise);
//    }

//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//     //   nettyConnectionLog.setSrcIp(((InetSocketAddress) ctx.channel().remoteAddress()).getHostString());
//        nettyConnectionLog.setStartTime(new Timestamp(new Date().getTime()));
//        super.channelRegistered(ctx);
//    }

//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        nettyConnectionLog.setSrcIp(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
//        nettyConnectionLog.setEndTime(new Timestamp(new Date().getTime()));
//    //    this.request.setWhen(new Date());
//      //  this.request.setTimestamp(System.currentTimeMillis() - this.startTime);
//        nettyConnectionLog.setURI(NettyStatistics.netty_statistics.getUri());
//
//        super.channelUnregistered(ctx);
//    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        nettyConnectionLog.setStartTime(new Timestamp(new Date().getTime()));
        nettyConnectionLog.setSrcIp(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
        this.trafficCounter().start();
    }


    @Override
    public synchronized void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.trafficCounter().stop();

        nettyConnectionLog.setURI(NettyStatistics.netty_statistics.getUri());
        nettyConnectionLog.setEndTime(new Timestamp(new Date().getTime()));
        nettyConnectionLog.setReceivedBytes( this.trafficCounter().cumulativeReadBytes());
        nettyConnectionLog.setSentBytes( this.trafficCounter().cumulativeWrittenBytes());
     //   TimeUnit.SECONDS.convert(nettyConnectionLog.getEndTime()-nettyConnectionLog.getStartTime(), TimeUnit.NANOSECONDS);

        if (nettyConnectionLog.getStartTime().getTime()>0){
      //      double timing = nettyConnectionLog.getEndTime().getTime()-nettyConnectionLog.getStartTime().getTime() ;
      //      double seconds = timing /1000;
       //     double speed =  (Math.round((((int)nettyConnectionLog.getReceivedBytes() + (int)nettyConnectionLog.getSentBytes()) / seconds) * 1000.0) / 1000.0);
            double speedNew = this.trafficCounter.lastWrittenBytes() * 1000 / (this.trafficCounter.checkInterval());

            nettyConnectionLog.setSpeed( speedNew);

        }
        addNettyConnectionLogInStatistics(nettyConnectionLog);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
