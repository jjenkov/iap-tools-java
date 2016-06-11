package com.jenkov.iap.tcpserver;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.write.IonWriter;
import com.jenkov.iap.mem.MemoryAllocator;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jjenkov on 10-06-2016.
 */
public class TCPSocketTest {



    @Test
    public void testRead() throws IOException {
        TCPSocketPool   tcpSocketPool   = new TCPSocketPool(1024);
        MemoryAllocator memoryAllocator = new MemoryAllocator(new byte[1024 * 1024], new long[1024], (allocator) -> new TCPMessage(allocator));

        TCPSocketMock tcpSocketMock = new TCPSocketMock(tcpSocketPool);

        tcpSocketMock.messageReader = new IAPMessageReader();
        tcpSocketMock.messageReader.init(memoryAllocator);

        byte[] message       = new byte[1024];
        int    messageLength = createMessage(message);
        assertEquals(6, messageLength);

        tcpSocketMock.dataBlocks.add(new DataBlock(message, 0, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(message, 3, messageLength - 3));

        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        Object[] messageDest = new Object[16];

        int messagesRead = tcpSocketMock.readMessages(inBuffer, messageDest, 0);

        assertEquals(1, messagesRead);
        assertNotNull(messageDest[0]);

        TCPMessage tcpMessage = (TCPMessage) messageDest[0];
        assertEquals(6, tcpMessage.endIndex - tcpMessage.startIndex);

        int index = tcpMessage.startIndex;

        assertEquals((IonFieldTypes.OBJECT << 4 | 1), 255 & tcpMessage.memoryAllocator.data[index++]);
        assertEquals(4, 255 & tcpMessage.memoryAllocator.data[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT << 4 | 1), 255 & tcpMessage.memoryAllocator.data[index++]);
        assertEquals(0, 255 & tcpMessage.memoryAllocator.data[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4 | 1), 255 & tcpMessage.memoryAllocator.data[index++]);
        assertEquals(123, 255 & tcpMessage.memoryAllocator.data[index++]);

        tcpMessage.free();

    }

    public int createMessage(byte[] dest){
        IonWriter writer = new IonWriter();

        writer.setDestination(dest, 0);

        int objectStartIndex = writer.destIndex;
        writer.writeObjectBegin(1);

        writer.writeKeyShort(new byte[] {0});
        writer.writeInt64(123);

        writer.writeObjectEnd(objectStartIndex, 1, writer.destIndex - objectStartIndex -1 -1 );

        return writer.destIndex;

    }
}
