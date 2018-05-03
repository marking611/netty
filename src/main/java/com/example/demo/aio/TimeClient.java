package com.example.demo.aio;

/**
 * Created by makai on 2018/5/3.
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandle("127.0.0.1",port),"AIO-Client").start();
    }
}
