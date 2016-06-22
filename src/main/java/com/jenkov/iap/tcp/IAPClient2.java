package com.jenkov.iap.tcp;

import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.write.IonWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *  This IAP client implementation uses the IapTcpClient in IAP Tools.
 *  This primarily a "test" class, so do not attemp to use it in your own applications.
 *
 */
public class IAPClient2 implements Runnable{

    private byte[] ionMessageBytesOut = new byte[1024];
    private byte[] ionMessageBytesIn  = new byte[1024];

    private int iterationsPerStatusMessage = 0;
    private int messageBatchSize = 1;

    public IAPClient2(int iterationsPerStatusMessage, int messageBatchSize) {
        this.iterationsPerStatusMessage = iterationsPerStatusMessage;
        this.messageBatchSize = messageBatchSize;
    }

    public void run() {

        int messageLength = generateMessage(ionMessageBytesOut, 0);
        System.out.println("messageLength = " + messageLength);

        ByteBuffer inBuffer  = ByteBuffer.allocate(1024);
        ByteBuffer outBuffer = ByteBuffer.allocate(1024);

        IapTcpClient iapTcpClient = null;
        try{

            iapTcpClient = new IapTcpClient("localhost", 11111);

            long startTime = System.currentTimeMillis();
            int count = 0;
            while(true){

                /*
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */

                for(int i = 0; i<this.messageBatchSize; i++){
                    outBuffer.clear();
                    outBuffer.put(ionMessageBytesOut, 0, messageLength);
                    outBuffer.flip();
                    try {
                        writeSingleMessage(outBuffer, iapTcpClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("e.getMemoryBlock() = " + e.getMessage());
                    }
                }

                for(int i = 0; i<this.messageBatchSize; i++){
                    inBuffer.clear();
                    readSingleMessage(iapTcpClient, inBuffer, messageLength);
                    inBuffer.flip();
                }

                count++;
                if(count == this.iterationsPerStatusMessage){
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    System.out.println("Iterations     : " + count);
                    System.out.println("Total time (ms): " + totalTime);
                    System.out.println("Iterations /s  : " + (count*1000 / (totalTime)));
                    System.out.println("Messages   /s  : " + (count*1000*this.messageBatchSize / (totalTime)));
                    count = 0;
                    startTime = System.currentTimeMillis();
                }
            }

        } catch(IOException e){
            e.printStackTrace();
        } finally{

        }
    }

    private static int generateMessage(byte[] dest, int destOffset) {
        int index = destOffset;
        index += IonWriter.writeObjectBegin(dest, index, 1);

        index += IonWriter.writeDirect(dest, index, IonUtil.preGenerateKeyField("field0"));
        index += IonWriter.writeBoolean(dest, index, true);
        index += IonWriter.writeDirect(dest, index, IonUtil.preGenerateKeyField("field1"));
        index += IonWriter.writeInt64(dest, index, 12345);
        index += IonWriter.writeDirect(dest, index, IonUtil.preGenerateKeyField("field2"));
        index += IonWriter.writeUtf8(dest, index, "abcdefgh");

        IonWriter.writeObjectEnd(dest, 0, 1, index -1 -1); //-1 for object lead byte, -1 for length byte

        return index - destOffset; //return length of message - not end index of message
    }

    private static void readSingleMessage(IapTcpClient iapTcpClient, ByteBuffer inBuffer, int messageLength) throws IOException {
        int totalBytesRead = 0;
        while(totalBytesRead < messageLength){
            int bytesRead = iapTcpClient.readAvailable(inBuffer);
            totalBytesRead += bytesRead;
            /*
            if(bytesRead == 0 && totalBytesRead < messageLength) {
                try {
                    Thread.sleep(0, 1);
                    System.out.println("sleeping during read" );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            */
        }
    }

    private static void writeSingleMessage(ByteBuffer outBuffer, IapTcpClient iapTcpClient) throws IOException, InterruptedException {
        iapTcpClient.writeAll(outBuffer);
    }

}
