package com.jenkov.iap.tcp;


import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;

/**
 * Created by jjenkov on 26-05-2016.
 */

public class TCPMessage extends MemoryBlock {

    public long socketId = 0;

    public TCPSocket tcpSocket = null;

    public TCPMessage(MemoryAllocator memoryAllocator) {
        super(memoryAllocator);
    }


}
