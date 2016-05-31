package com.jenkov.iap.tcpserver;

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

        Object[] inboundMessages  = new Object[1024];
        Object[] outboundMessages = new Object[1024];

        System.out.println("IAP TCP Server started on port " + tcpPort);


        while(true) {
            int messageCount = 0;
            try {
                tcpSocketsProxy.checkForNewInboundSockets();
                messageCount = tcpSocketsProxy.read(inboundMessages);

                for(int i=0; i < messageCount; i++){
                    TCPMessage messageIn = (TCPMessage) inboundMessages[i];

                    //echo incoming message back to the same socket is was received from
                    tcpSocketsProxy.enqueue(messageIn.tcpSocket, messageIn);
                }

                tcpSocketsProxy.writeToSockets();// Write whatever is enqueued in TCPSockets.

            } catch(IOException e){
                e.printStackTrace();
            }
        }

    }
}
