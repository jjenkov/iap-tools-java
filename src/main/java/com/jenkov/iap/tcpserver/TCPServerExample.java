package com.jenkov.iap.tcpserver;

import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jjenkov on 31-05-2016.
 */
public class TCPServerExample {

    public static void main(String[] args) throws IOException {

        BlockingQueue newSocketQueue = new ArrayBlockingQueue(1024);
        int tcpPort = 11111;
        TCPServer tcpServer = new TCPServer(tcpPort, newSocketQueue);

        Thread socketAcceptThread = new Thread(tcpServer);
        socketAcceptThread.start();


        TCPSocketsProxy tcpSocketsProxy = new TCPSocketsProxy(newSocketQueue, new IAPMessageReaderFactory());

        MemoryBlock[] inboundMessages  = new MemoryBlock[1024];
        MemoryBlock[] outboundMessages = new MemoryBlock[1024];

        System.out.println("IAP TCP Server started on port " + tcpPort);


        while(true) {
            int messageCount = 0;
            try {
                tcpSocketsProxy.checkForNewInboundSockets();

                //main loop - repeat more often than checking for new sockets and closing sockets.
                for(int i=0; i< 100; i++ ){
                    messageCount = tcpSocketsProxy.read(inboundMessages);

                    for(int j=0; j < messageCount; j++){
                        TCPMessage messageIn = (TCPMessage) inboundMessages[j];

                        //echo incoming message back to the same socket is was received from
                        tcpSocketsProxy.enqueue(messageIn.tcpSocket, messageIn);
                    }

                    tcpSocketsProxy.writeToSockets();// Write whatever is enqueued in TCPSockets.
                }

                tcpSocketsProxy.cleanupSockets();// Close whatever sockets are in an invalid state, or which have
                                                 // reached end-of-stream (closed by client).

            } catch(IOException e){
                e.printStackTrace();
            }
        }

    }
}
