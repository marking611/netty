package com.example.demo.netty.tcp_time;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 支持TCP粘包
 * LineBasedFrameDecoder + StringDecoder 解决拆包/粘包
 * Created by makai on 2018/5/7.
 */
public class TimeServer {
    public void bind(int port) throws InterruptedException {
        //配置NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandle());
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandle extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            //解决TCP粘包
            //LineBasedFrameDecoder 依次遍历ByteBuf的可读字节，看是否有"\n"或者"\r\n"，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2014));
            //StringDecoder 将接收到的对象转换成字符串，然后继续调用后面的handle
            socketChannel.pipeline().addLast(new StringDecoder());

            socketChannel.pipeline().addLast(new TimeServerHandle());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimeServer().bind(port);
    }
}
