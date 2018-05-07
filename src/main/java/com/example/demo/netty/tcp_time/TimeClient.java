package com.example.demo.netty.tcp_time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 支持TCP粘包
 * LineBasedFrameDecoder + StringDecoder 解决拆包/粘包
 * Created by makai on 2018/5/7.
 */
public class TimeClient {
    public void connect(String host, int port) throws InterruptedException {
        //配置客户端NIO线程
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //解决TCP粘包
                            //LineBasedFrameDecoder 依次遍历ByteBuf的可读字节，看是否有"\n"或者"\r\n"，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2014));
                            //StringDecoder 将接收到的对象转换成字符串，然后继续调用后面的handle
                            socketChannel.pipeline().addLast(new StringDecoder());

                            socketChannel.pipeline().addLast(new TimeClientHandle());
                        }
                    });
            //发起异步链接操作
            ChannelFuture f = b.connect(host, port).sync();
            //等待客户端连接关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimeClient().connect("127.0.0.1", port);
    }
}
