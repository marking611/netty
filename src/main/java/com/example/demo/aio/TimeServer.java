package com.example.demo.aio;

/**
 * Created by makai on 2018/5/3.
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        AsyncTimeServerHandle asyncTimeServerHandle = new AsyncTimeServerHandle(port);
        new Thread(asyncTimeServerHandle,"AIO-Server").start();
    }
}
