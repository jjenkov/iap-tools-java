package com.jenkov.iap.tcpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * An IAP TCP client capable of writing IAP messages to a server, and reading IAP messages from a server.
 *
 * When writing, the IapTcpClient does not check if the bytes you pass it constitutes a full, valid message.
 * It is up to you to make sure that the bytes passed to the IapTcpClient write methods contain a valid IAP message.
 *
 * When reading, the IapTcpClient does check when a full IAP message has been received. Only full messages are
 * returned. If only a partial message was read, the partial message is kept internally until a full message
 * has arrived.
 */
public class IapTcpClient {

    private SocketChannel socketChannel = null;


    public IapTcpClient(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
    }

    public IapTcpClient(InetSocketAddress address) throws IOException {
        this.socketChannel = SocketChannel.open();
        this.socketChannel.connect(address);
        this.socketChannel.configureBlocking(false);
    }

    public IapTcpClient(String host, int port) throws IOException {
        this(new InetSocketAddress(host, port));
    }


    /*
    public int writeSome() {

    }
    */

    /**
     * Writes all bytes in the ByteBuffer to the underlying socket channel.
     * You must make sure that the ByteBuffer has been "flipped" correctly before the writing
     * takes place. This method does not call ByteBuffer.flip() before accessing the ByteBuffer.
     *
     * @param source The ByteBuffer which contents you want written to the underlying SocketChannel.
     * @param nanoSecondDelay In case a write attempt results in 0 bytes being written, the thread will sleep this amount
     *                        of nano seconds before trying next time, to give the OS + network some time to send the data
     *                        queued up.
     */
    public int writeAll(ByteBuffer source, int nanoSecondDelay) throws IOException, InterruptedException {
        int totalBytesWritten = 0;
        while(source.hasRemaining()){
            int bytesWritten = this.socketChannel.write(source);
            totalBytesWritten += bytesWritten;

            if(bytesWritten == 0){
                Thread.sleep(0, nanoSecondDelay);
            }
        }

        return totalBytesWritten;
    }


    /*
    public int readSome() {

    }
    */


    /**
     * This method reads all immediately available data from the underlying SocketChannel. In other words, this
     * method keeps calling SocketChannel.read() until either the ByteBuffer is full, or the SocketChannel.read()
     * method returns 0 (= 0 bytes read). After one of these events occur, this readAll() method returns - with
     * the total number of bytes read as return value.
     *
     * @param dest The ByteBuffer to read the available data into.
     * @return     The total number of bytes read into the ByteBuffer.
     * @throws IOException If reading data from the underlying SocketChannel fails.
     */
    public int readAll(ByteBuffer dest) throws IOException {
        int totalBytesRead = 0;
        int bytesRead = this.socketChannel.read(dest);

        while(bytesRead > 0 && dest.hasRemaining()){
            totalBytesRead += bytesRead;
            this.socketChannel.read(dest);
        }
        return totalBytesRead;
    }

    /*
    public int readOne() {

    }
    */


    public static boolean containsAtLeastOneMessage(ByteBuffer buffer){
        boolean containsFullMessage = false;

        int leadByte = buffer.get(buffer.position());

        int lengthLength = 15 & leadByte;

        int length = 0;
        for(int i=0; i<lengthLength; i++){
            length <<= 8;
            length |= buffer.get(buffer.position() + i + 1);
        }

        int bytesInBuffer = buffer.limit() - buffer.position();

        if(bytesInBuffer >= 1 + lengthLength + length){
            containsFullMessage = true;
        }

        return containsFullMessage;
    }




}
