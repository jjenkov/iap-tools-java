package com.jenkov.iap.tcp;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.write.IonWriter;
import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;
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
    public void testTooBigIapMessage() throws IOException {
        TCPSocketMock tcpSocketMock = createTCPSocketMock();

        byte[] messages = new byte[1024 * 1024];

        int messageLength1 = createTooBigMessage(messages, 0);
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 0, 32 * 1024));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 0, 32 * 1024));

        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        MemoryBlock[] messageDest = new MemoryBlock[16];

        int messagesRead = tcpSocketMock.readMessages(inBuffer, messageDest, 0);

        assertEquals(0, messagesRead);

        assertEquals(-2, tcpSocketMock.state);                 // -2  = IAPMessageReader.INVALID_IAP_MESSAGE_TOO_BIG_MESSAGE
        assertEquals(-2, tcpSocketMock.messageReader.state()); // -2  = IAPMessageReader.INVALID_IAP_MESSAGE_TOO_BIG_MESSAGE

    }

    @Test
    public void testInvalidIapMessage() throws IOException {
        TCPSocketMock tcpSocketMock = createTCPSocketMock();

        byte[] messages       = new byte[1024];

        int    messageLength1 = createMessage(messages, 0);

        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 0, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 3, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 6, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 9, 3));

        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        MemoryBlock[] messageDest = new MemoryBlock[16];

        int messagesRead = tcpSocketMock.readMessages(inBuffer, messageDest, 0);

        assertEquals(1, messagesRead);

        assertEquals(-1, tcpSocketMock.state);                 // -1  = IAPMessageReader.INVALID_IAP_MESSAGE_NOT_ION_OBJECT
        assertEquals(-1, tcpSocketMock.messageReader.state()); // -1  = IAPMessageReader.INVALID_IAP_MESSAGE_NOT_ION_OBJECT

    }


    @Test
    public void testMultipleMessages() throws IOException {
        TCPSocketMock tcpSocketMock = createTCPSocketMock();

        byte[] messages       = new byte[1024];

        int    messageLength1 = createMessage(messages, 0);
        int    messageLength2 = createMessage(messages, messageLength1);
        assertEquals(6, messageLength1);
        assertEquals(6, messageLength2);

        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 0, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 3, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 6, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(messages, 9, 3));

        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        MemoryBlock[] messageDest = new MemoryBlock[16];

        int messagesRead = tcpSocketMock.readMessages(inBuffer, messageDest, 0);

        assertEquals(2, messagesRead);
        assertNotNull(messageDest[0]);
        assertNotNull(messageDest[1]);

    }


    @Test
    public void testReadSingleMessage() throws IOException {
        TCPSocketMock tcpSocketMock = createTCPSocketMock();

        byte[] message       = new byte[1024];
        int    messageLength = createMessage(message, 0);
        assertEquals(6, messageLength);

        tcpSocketMock.dataBlocks.add(new DataBlock(message, 0, 3));
        tcpSocketMock.dataBlocks.add(new DataBlock(message, 3, messageLength - 3));

        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        MemoryBlock[] messageDest = new MemoryBlock[16];

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

    private TCPSocketMock createTCPSocketMock() {
        TCPSocketPool tcpSocketPool = new TCPSocketPool(1024);
        TCPSocketMock tcpSocketMock = new TCPSocketMock(tcpSocketPool);

        MemoryAllocator memoryAllocator = new MemoryAllocator(new byte[1024 * 1024], new long[1024], (allocator) -> new TCPMessage(allocator));
        tcpSocketMock.messageReader = new IAPMessageReader();
        tcpSocketMock.messageReader.init(memoryAllocator);

        return tcpSocketMock;
    }

    public int createMessage(byte[] dest, int destOffset){
        IonWriter writer = new IonWriter();

        writer.setDestination(dest, destOffset);

        int objectStartIndex = writer.destIndex;
        writer.writeObjectBegin(1);

        writer.writeKeyShort(new byte[] {0});
        writer.writeInt64(123);

        writer.writeObjectEnd(objectStartIndex, 1, writer.destIndex - objectStartIndex -1 -1 );

        return writer.destIndex - destOffset;
    }


    public int createTooBigMessage(byte[] dest, int destOffset){
        IonWriter writer = new IonWriter();

        writer.setDestination(dest, destOffset);

        int objectStartIndex = writer.destIndex;
        writer.writeObjectBegin(3);

        writer.writeKeyShort(new byte[] {0});
        writer.writeBytes(new byte[64 * 1024]);

        writer.writeObjectEnd(objectStartIndex, 3, writer.destIndex - objectStartIndex -1 -3 );

        return writer.destIndex - destOffset;
    }

}
