package com.jenkov.iap.message;

import com.jenkov.iap.ion.types.Utf8;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 27-02-2016.
 */
public class IapMessageReaderTest {


    @Test
    public void testReadIapMessage() {
        byte[] dest = new byte[10 * 1024];

        IapMessage sourceMessage = new IapMessage();
        configureIapMessage(sourceMessage);

        int bytesWritten = IapMessageWriter.writeIapMessageHeaders(dest, 0, sourceMessage, 2);
        assertEquals(48, bytesWritten);

        int index = 0;
        bytesWritten = IapMessageWriter.writeIapMessageHeaders(dest, index, sourceMessage, 2);
        IapMessageWriter.writeMessageEnd(dest, index, 2, bytesWritten - 1 - 2); //-1 for message lead byte, -2 for message length length


        IapMessageReader iapMessageReader = new IapMessageReader();

        IapMessage destMessage = new IapMessage();
        destMessage.senderId   = new Utf8();
        destMessage.receiverId = new Utf8();
        destMessage.semanticProtocolId      = new Utf8();
        destMessage.semanticProtocolVersion = new Utf8();
        destMessage.messageType = new Utf8();

        assertEquals(0, destMessage.senderId.offset);

        iapMessageReader.readIapMessage(dest, index, destMessage);

        assertEquals(6, destMessage.receiverId.offset);
        assertEquals(3, destMessage.receiverId.length);

        assertEquals(12, destMessage.senderId.offset);
        assertEquals(3, destMessage.senderId.length);

        assertEquals(  -1, destMessage.connectionId);
        assertEquals(  11, destMessage.channelId);
        assertEquals(  12, destMessage.sequenceId);
        assertEquals(  13, destMessage.sequenceIndex);
        assertEquals(true, destMessage.isLastInSequence);

        assertEquals(33, destMessage.semanticProtocolId.offset);
        assertEquals( 3, destMessage.semanticProtocolId.length);

        assertEquals(39, destMessage.semanticProtocolVersion.offset);
        assertEquals( 3, destMessage.semanticProtocolVersion.length);

        assertEquals(45, destMessage.messageType.offset);
        assertEquals( 3, destMessage.messageType.length);

        System.out.println("done...");
    }

    private void configureIapMessage(IapMessage message) {
        message.data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

        message.senderId = new Utf8();
        message.senderId.source = message.data;
        message.senderId.offset   = 1;
        message.senderId.length   = 3;

        message.receiverId = new Utf8();
        message.receiverId.source = message.data;
        message.receiverId.offset = 6;
        message.receiverId.length = 3;

        message.channelId        = 11;
        message.sequenceId       = 12;
        message.sequenceIndex    = 13;
        message.isLastInSequence = true;

        message.semanticProtocolId = new Utf8();
        message.semanticProtocolId.source = message.data;
        message.semanticProtocolId.offset = 16;
        message.semanticProtocolId.length = 3;


        message.semanticProtocolVersion = new Utf8();
        message.semanticProtocolVersion.source = message.data;
        message.semanticProtocolVersion.offset = 21;
        message.semanticProtocolVersion.length = 3;

        message.messageType = new Utf8();
        message.messageType.source = message.data;
        message.messageType.offset = 26;
        message.messageType.length = 3;
    }

}
