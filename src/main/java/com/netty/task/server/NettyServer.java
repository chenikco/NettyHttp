package com.netty.task.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;



/**
 * Created by nikolay on 25.03.2015.
 */
public class NettyServer  {
    private final int port;
    public NettyServer(int port) {
        this.port = port;
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + NettyServer.class.getSimpleName() +" <port>");
        }
        //Set the port value (throws a NumberFormatException if the port argument is malformed)
        int port = Integer.parseInt(args[0]);
        //Call the server's start() method.
        new NettyServer(port).start();
    }
    public void start() throws Exception {
        //Create the EventLoopGroup
//TODO Create description for  EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //Create the ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    //Specify the use of an NIO transport Channel
                    .channel(NioServerSocketChannel.class)
                    //Set the socket address using the selected port
                   .childHandler(new NettyServerInitializer() );
            //Bind the server; sync waits for the server to close
            Channel ch = b.bind(port).sync().channel();
            //Close the channel and block until it is closed
            ch.closeFuture().sync();
        } finally {
            //Shutdown the EventLoopGroup, which releases all resources.
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
