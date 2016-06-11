package com.jenkov.iap.tcpserver;


import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class TCPSocket {

    private TCPSocketPool   tcpSocketPool = null;

    public SocketChannel    socketChannel = null;
    public long             socketId = 0;
    public SelectionKey     readSelectorSelectionKey = null;
    public boolean          isRegisteredWithReadSelector = false;


    public IMessageReader   messageReader = null;

    public boolean          isRegisteredWithWriteSelector = false;
    public boolean          endOfStreamReached = false;
    public int              state = 0;

    /*
       WRITING VARIABLES
     */

    private Queue writeQueue   = new Queue(16);
    private int   bytesWritten = 0;


    public TCPSocket(TCPSocketPool tcpSocketPool) {
        this.tcpSocketPool = tcpSocketPool;
    }


    public int readMessages(ByteBuffer tempBuffer, MemoryBlock[] messageDestination, int messageDestinationOffset) throws IOException {
        if(this.state != 0) {
            return 0; //TCPSocket is in an invalid state - no more messages can be read from it.
        }

        tempBuffer.clear();

        int totalBytesRead = read(tempBuffer);

        int messagesRead = 0;
        if(totalBytesRead > 0){
            tempBuffer.flip();

            //todo if more than messageDestination.length messages are written into messageDestination, this will result in an IndexOutOfBoundsException.
            messagesRead = this.messageReader.read(tempBuffer, messageDestination, messageDestinationOffset);

            if(this.messageReader.state() != 0){
                // continue processing the valid messages received in the for loop below - but by setting state to
                // something other than 0, no more messages can be read from this TCP Socket. It is now invalid and
                // should be closed.
                this.state = this.messageReader.state();
            }

            for(int i=messageDestinationOffset, n = messageDestinationOffset+messagesRead; i < n; i++){
                TCPMessage tcpMessage = (TCPMessage) messageDestination[i];
                tcpMessage.socketId    = this.socketId;
                tcpMessage.tcpSocket   = this;
            }
        }

        return messagesRead;
    }

    public int read(ByteBuffer destinationBuffer) throws IOException {
        int bytesRead = 0;

        try{
            bytesRead = doRead(destinationBuffer);
        } catch(IOException e){
            this.endOfStreamReached = true;
            return -1;
        }

        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            try{
                bytesRead = doRead(destinationBuffer);
                if(bytesRead > 0){
                    totalBytesRead += bytesRead;
                }
            } catch(IOException e){
                this.endOfStreamReached = true;
                return -1;
            }
        }

        if(bytesRead == -1){
            this.endOfStreamReached = true;
        }

        return totalBytesRead;
    }


    /**
     * A method which can be overwritten in mock classes - to NOT read from a socketChannel, but from e.g. a
     * predefined byte array.
     *
     * @param destinationBuffer
     * @return
     * @throws IOException
     */
    protected int doRead(ByteBuffer destinationBuffer) throws IOException {
        int bytesRead;
        bytesRead = this.socketChannel.read(destinationBuffer);
        return bytesRead;
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


    /**
     * Closes the TCPSocket + underlying SocketChannel, and frees up all queued inbound and outbound
     * messages.
     *
     * todo maybe split into three methods: close() + free() + closeAndFree()
     */
    public void closeAndFree() throws IOException {
        if(this.messageReader != null){
            this.messageReader.dispose();
            this.messageReader = null;
        }

        if(this.writeQueue != null){
            while(this.writeQueue.available() > 0){
                TCPMessage queuedOutboundMessage = (TCPMessage) writeQueue.take();
                queuedOutboundMessage.free();
            }
        }

        if(this.readSelectorSelectionKey != null){
            this.readSelectorSelectionKey.attach(null);
            if(this.isRegisteredWithReadSelector){
                this.readSelectorSelectionKey.cancel();
            }
            this.readSelectorSelectionKey = null;
        }

        if(this.socketChannel != null){
            this.socketChannel.close();
            this.socketChannel = null;
        }

    }


}
