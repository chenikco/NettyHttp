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


        if (nettyConnectionLog.getStartTime().getTime()>0){
            try {
                double timing = nettyConnectionLog.getEndTime().getTime()-nettyConnectionLog.getStartTime().getTime() ;
                double seconds = timing /1000;
                double speed =  (Math.round((((int)nettyConnectionLog.getReceivedBytes() + (int)nettyConnectionLog.getSentBytes()) / seconds) * 1000.0) / 1000.0);
                nettyConnectionLog.setSpeed( speed);

                //   double speedNew = this.trafficCounter.lastWrittenBytes() * 1000 / (this.trafficCounter.checkInterval());
            }
            catch ( ArithmeticException ae){
                System.out.println( " ArithmeticException " + ae.getMessage());
                System.out.println( "Setting speed to 0 ");
                nettyConnectionLog.setSpeed( 0);
            }




        }
        addNettyConnectionLogInStatistics(nettyConnectionLog);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
