package com.jenkov.iap.tcpserver;


import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class TCPSocket {

    private TCPSocketPool tcpSocketPool = null;

    public TCPSocketsProxy proxy = null;

    public SocketChannel  socketChannel = null;
    public long socketId = 0;

    public IMessageReader   messageReader = null;

    public boolean          isRegisteredWithWriteSelector = false;
    public boolean          endOfStreamReached = false;


    /*
       WRITING VARIABLES
     */

    private Queue writeQueue   = new Queue(16);
    private int   bytesWritten = 0;


    public TCPSocket(TCPSocketPool tcpSocketPool) {
        this.tcpSocketPool = tcpSocketPool;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = 0;

        try{
            bytesRead = this.socketChannel.read(byteBuffer);
        } catch(IOException e){
            this.endOfStreamReached = true;
            return -1;
        }

        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            try{
                bytesRead = this.socketChannel.read(byteBuffer);
            } catch(IOException e){
                this.endOfStreamReached = true;
                return -1;
            }

            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            this.endOfStreamReached = true;
        }

        return totalBytesRead;
    }

    public void enqueue(MemoryBlock memoryBlock) {
        this.writeQueue.put(memoryBlock);
    }

    public void enqueueRest(MemoryBlock memoryBlock, int bytesAlreadyWritten) {
        this.writeQueue.put(memoryBlock);
        this.bytesWritten = bytesAlreadyWritten;
    }

    public boolean isEmpty() {
        return this.writeQueue.available() == 0;
    }


    public int writeDirect(ByteBuffer byteBuffer, TCPMessage message) throws IOException {
        int totalBytesWritten = 0;
        int bytesWrittenNow = 0;

        do {
            byte[] byteArray = message.memoryAllocator.data;

            int offset = message.startIndex + bytesWritten;
            int length = message.writeIndex - offset;

            byteBuffer.put(byteArray, offset, length);
            byteBuffer.flip();

            bytesWrittenNow = writeToSocketChannel(byteBuffer);
            totalBytesWritten += bytesWrittenNow;
            byteBuffer.clear();


        } while (bytesWrittenNow > 0 && (message.startIndex + totalBytesWritten < message.writeIndex));

        return totalBytesWritten;
    }


    public void writeQueued(ByteBuffer byteBuffer) throws IOException {

        MemoryBlock messageInProgress = (MemoryBlock) this.writeQueue.peek();

        int bytesWrittenNow = 0;

        do{
            byte[] byteArray = messageInProgress.memoryAllocator.data;

            int offset = messageInProgress.startIndex + bytesWritten;
            int length = messageInProgress.writeIndex - offset;

            byteBuffer.put(byteArray, offset, length);
            byteBuffer.flip();

            bytesWrittenNow = writeToSocketChannel(byteBuffer);
            this.bytesWritten += bytesWrittenNow;

            byteBuffer.clear();

            if(bytesWritten == (messageInProgress.writeIndex - messageInProgress.startIndex)){
                this.bytesWritten = 0;
                this.writeQueue.take();
                messageInProgress.free();

                messageInProgress = (MemoryBlock) this.writeQueue.peek();
            }

        } while(bytesWrittenNow > 0 && messageInProgress != null);
    }



    public int writeToSocketChannel(ByteBuffer byteBuffer) throws IOException{
        int bytesWritten      = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while(bytesWritten > 0 && byteBuffer.hasRemaining()){
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }


    public void dispose() {
        //todo call dispose on message reader and message writer before nulling references.
        this.messageReader = null;
        this.socketChannel = null;
    }

}
