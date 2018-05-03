package com.example.demo.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by makai on 2018/5/3.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandle> {
    /**
     * Invoked when an operation has completed.
     *
     * @param result     The result of the I/O operation.
     * @param attachment
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandle attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        result.read(byteBuffer,byteBuffer,new ReadCompletionHandle(result));
    }

    /**
     * Invoked when an operation fails.
     *
     * @param exc        The exception to indicate why the I/O operation failed
     * @param attachment
     */
    @Override
    public void failed(Throwable exc, AsyncTimeServerHandle attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
