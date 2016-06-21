package com.jenkov.iap.tcpserver;

import com.jenkov.iap.ion.write.IonWriter;
import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jjenkov on 15-06-2016.
 */
public class TCPClientExample {


    public static void main(String[] args) throws IOException, InterruptedException {

        BlockingQueue blockingQueue = new ArrayBlockingQueue(1024);

        TCPSocketsProxy tcpSocketsProxy = new TCPSocketsProxy(blockingQueue, new IAPMessageReaderFactory());

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 11111));

        blockingQueue.put(socketChannel);

        tcpSocketsProxy.checkForNewInboundSockets();

        TCPMessage writeMemoryBlock = tcpSocketsProxy.getWriteMemoryBlock();
        writeMemoryBlock.reserve(1024);


        TCPSocket tcpSocket = tcpSocketsProxy.getTCPSocket(1);

        createTestMessage(writeMemoryBlock);


        tcpSocketsProxy.enqueue(tcpSocket, writeMemoryBlock);

        tcpSocketsProxy.writeToSockets();

        Thread.sleep(1000);


        MemoryBlock[] dest = new MemoryBlock[64];

        while(true){
            int messagesRead = tcpSocketsProxy.read(dest);
            System.out.println(messagesRead);
        }






    }

    private static void createTestMessage(TCPMessage writeMemoryBlock) {
        IonWriter writer = new IonWriter();
        writer.setDestination(writeMemoryBlock.memoryAllocator.data, writeMemoryBlock.startIndex);

        int startIndex = writer.destIndex;
        writer.writeObjectBegin(1);

        writer.writeKey("test");
        writer.writeInt64(999);

        writer.writeObjectEnd(startIndex, 1, writer.destIndex - startIndex - 1 - 1);

        writeMemoryBlock.writeIndex = writer.destIndex;
    }


}
