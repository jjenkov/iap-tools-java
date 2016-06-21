package com.jenkov.iap.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

/**
 * A Server is a TCP server using a ServerSocketChannel internally in non-blocking mode to receive incoming messages
 *
 * Don't create a Server instance yourself. A Server instance needs a dedicated thread. The CircuitContainer creates
 * that for you when you create a server via the CircuitContainer.createServer();
 *
 * Created by jjenkov on 27-08-2015.
 */
public class TCPServer implements Runnable {

    private static final int MAX_QUEUED_SOCKETS = 1024;

    private int tcpPort = 9999;
    private InetSocketAddress   ipAddress = null;
    private ServerSocketChannel serverSocketChannel = null;

    private BlockingQueue<SocketChannel> socketQueue = null;

    private boolean isStopped = false;   //todo analyze if this could be done with a volatile variable instead.

    public TCPServer(int tcpPort, BlockingQueue socketQueue){
        this.tcpPort     = tcpPort;
        this.socketQueue = socketQueue;
    }

    private void open() throws IOException {
        open(this.tcpPort);
    }

    private void open(int tcpPort) throws IOException {
        this.tcpPort = tcpPort;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(true);
        this.ipAddress = new InetSocketAddress(this.tcpPort);
        this.serverSocketChannel.bind(this.ipAddress);
    }

    public synchronized void stop()         { this.isStopped = true; }
    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized BlockingQueue<SocketChannel> getSocketQueue(){
        return this.socketQueue;
    }


    @Override
    public void run() {
        try {
            this.open();

            while(!isStopped()){
                SocketChannel incomingSocket = this.serverSocketChannel.accept();
                if(! this.socketQueue.offer(incomingSocket)){
                    incomingSocket.close();

                    //todo log that an incoming socket was dropped.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
