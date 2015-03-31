package com.netty.task.server.beans;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.commons.lang3.tuple.Pair;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nikolay on 28.03.2015.
 */
public class NettyStatistics  {

    public static  NettyStatistics netty_statistics ;


    //TODO Difference between GlobalEventExecutor and ImmediateEventExecutor ????
    private static DefaultChannelGroup allChannels  = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private ConcurrentHashMap<String,Integer> redirectsCount = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Pair<Integer,Timestamp>>  iprequests = new ConcurrentHashMap<>();
    private List<NettyConnectionLog> connectionLogs = Collections.synchronizedList(new ArrayList<NettyConnectionLog>());

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String uri;



    private NettyStatistics() {

    }

    public static synchronized NettyStatistics getInstance() {
        if ( netty_statistics == null){
            netty_statistics = new NettyStatistics();
        }
        return netty_statistics;
    }

    public ConcurrentHashMap<String, Integer> getRedirectsCount() {
        return redirectsCount;
    }

    public void setRedirectsCount(ConcurrentHashMap<String, Integer> redirectsCount) {
        this.redirectsCount = redirectsCount;
    }
    public ConcurrentHashMap<String, Pair<Integer, Timestamp>> getIprequests() {
        return iprequests;
    }

    public void setIprequests(ConcurrentHashMap<String, Pair<Integer, Timestamp>> iprequests) {
        this.iprequests = iprequests;
    }

    public long getNumberOfIpRequests() {
        return getIprequests().size();
    }

    public synchronized void addIpRequests(String ipAddress) {
        java.util.Date date= new java.util.Date();
         if (iprequests.containsKey(ipAddress)) {
            iprequests.put(ipAddress, Pair.of(iprequests.get(ipAddress).getLeft() + 1, new Timestamp(date.getTime())));
        } else {
            iprequests.put(ipAddress, Pair.of(1, new Timestamp(date.getTime())));
        }
    }

    public synchronized void addRedirect(String redirectUrl) {
        if(redirectsCount.containsKey(redirectUrl)){
            redirectsCount.replace(redirectUrl, redirectsCount.get(redirectUrl)+1);

        }
        else {
            redirectsCount.put(redirectUrl, 1);
        }
    }
    public String getRedirects() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> set = netty_statistics.getRedirectsCount().keySet();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(set);

       for (String key : list) {

            stringBuilder.append( "<tr><td>").append(key).append("</td><td>").
                                append(netty_statistics.getRedirectsCount().get(key)).append("</td></tr>");
        }

        return stringBuilder.toString();
    }

    public String getIpRequests() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> set = netty_statistics.getIprequests().keySet();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(set);

        for (String key : list) {
            stringBuilder.append( "<tr><td>").
                          append(key).
                          append("</td><td>").
                          append(netty_statistics.getIprequests().get(key).getLeft()).
                          append("</td>").
                          append("<td>").
                          append(netty_statistics.getIprequests().get(key).getRight()).
                          append("</td></tr>");
        }

        return stringBuilder.toString();
    }

    public synchronized String getConnections() {
        StringBuilder stringBuilder = new StringBuilder();
        for (NettyConnectionLog key : netty_statistics.connectionLogs) {
              stringBuilder.append( "<tr><td>").append(key.getSrcIp()).append("</td><td>").
                            append(key.getURI()).append("</td><td>").
                            append(key.getEndTime().toString()).append("</td><td>").
                            append(key.getSentBytes()).append("</td><td>").
                            append(key.getReceivedBytes()).append("</td><td>").
                            append(key.getSpeed()).append("</td><tr>");
        }

        return stringBuilder.toString();
    }

    public synchronized Integer getTotalCountConnections() {
        int total = 0;

        for (Pair value : iprequests.values() ) {
            total = total + (Integer)value.getLeft();
        }
        return total;

    }
    public static DefaultChannelGroup getAllChannels() {
        return allChannels;
    }

    public static void setAllChannels(DefaultChannelGroup allChannels) {
        NettyStatistics.allChannels = allChannels;
    }

    public void addChannel(Channel c) {
        allChannels.add(c);
    }

    public synchronized void addNettyConnectionLog (NettyConnectionLog nettyConnectionLog) {
        /* Store no more than 16 entries */
        if (connectionLogs.size() == 16) {
            connectionLogs.remove(0);
        }
        connectionLogs.add(nettyConnectionLog);
    }

    public List<NettyConnectionLog> getNettyConnectionLog() {
        return connectionLogs;
    }

    public static String getIpFromChannel(Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }


}
