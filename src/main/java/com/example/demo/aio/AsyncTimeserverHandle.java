package com.example.demo.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by makai on 2018/5/3.
 */
public class AsyncTimeServerHandle implements Runnable {
    private int port;

    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    CountDownLatch latch;

    public AsyncTimeServerHandle(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("server port is : "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //初始化CountDownLatch  在完成一组正在执行的操作之前，允许当前线程一直阻塞
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
    }


}
