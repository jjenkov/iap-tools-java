package com.jenkov.iap.tcpserver;


import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class TCPMessageWriter {

    private Queue    writeQueue = new Queue(16);
    private int      bytesWritten      = 0;

    public TCPMessageWriter() {
    }

    public void enqueue(MemoryBlock memoryBlock) {

        this.writeQueue.put(memoryBlock);
    }

    public void write(TCPSocket socket, ByteBuffer byteBuffer) throws IOException {

        MemoryBlock memoryBlockInProgress = (MemoryBlock) this.writeQueue.peek();

        int bytesWrittenNow = 0;

        do{
            byte[] byteArray = memoryBlockInProgress.memoryAllocator.data;

            int offset = memoryBlockInProgress.startIndex + bytesWritten;
            int length = memoryBlockInProgress.writeIndex - offset;

            byteBuffer.put(byteArray, offset, length);
            byteBuffer.flip();

            bytesWrittenNow = socket.writeToSocketChannel(byteBuffer);
            this.bytesWritten += bytesWrittenNow;

            byteBuffer.clear();

            if(bytesWritten == (memoryBlockInProgress.writeIndex - memoryBlockInProgress.startIndex)){
                this.bytesWritten = 0;
                this.writeQueue.take();
                memoryBlockInProgress.free();

                memoryBlockInProgress = (MemoryBlock) this.writeQueue.peek();
            }

        } while(bytesWrittenNow > 0 && memoryBlockInProgress != null);
    }

    public boolean isEmpty() {
        return this.writeQueue.available() == 0;
    }

}
