package com.jenkov.iap.tcpserver;


import com.jenkov.iap.mem.MemoryAllocator;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 27-10-2015.
 */
public interface IMessageReader {

    public void init(MemoryAllocator readMemoryAllocator);

    public int read(TCPSocket socket, ByteBuffer byteBuffer, Object[] dest, int destOffset) throws IOException;

    public void dispose();

    //public List<Message> getMessages();


}
