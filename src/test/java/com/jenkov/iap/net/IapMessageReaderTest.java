package com.jenkov.iap.net;

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
        assertEquals(-1, destMessage.senderIdOffset);

        iapMessageReader.readIapMessage(dest, index, destMessage);

        assertEquals(6, destMessage.senderIdOffset);
        assertEquals(3, destMessage.senderIdLength);

        assertEquals(12, destMessage.receiverIdOffset);
        assertEquals(3, destMessage.receiverIdLength);

        assertEquals(  -1, destMessage.connectionId);
        assertEquals(  11, destMessage.channelId);
        assertEquals(  12, destMessage.sequenceId);
        assertEquals(  13, destMessage.sequenceIndex);
        assertEquals(true, destMessage.isLastInSequence);

        assertEquals(33, destMessage.semanticProtocolIdOffset);
        assertEquals( 3, destMessage.semanticProtocolIdLength);

        assertEquals(39, destMessage.semanticProtocolVersionOffset);
        assertEquals( 3, destMessage.semanticProtocolVersionLength);

        assertEquals(45, destMessage.messageTypeOffset);
        assertEquals( 3, destMessage.messageTypeLength);

        System.out.println("done...");
    }

    private void configureIapMessage(IapMessage message) {
        message.data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

        message.senderIdOffset   = 1;
        message.senderIdLength   = 3;

        message.receiverIdOffset = 6;
        message.receiverIdLength = 3;

        message.channelId        = 11;
        message.sequenceId       = 12;
        message.sequenceIndex    = 13;
        message.isLastInSequence = true;

        message.semanticProtocolIdOffset = 16;
        message.semanticProtocolIdLength = 3;

        message.semanticProtocolVersionOffset = 21;
        message.semanticProtocolVersionLength = 3;

        message.messageTypeOffset = 26;
        message.messageTypeLength = 3;
    }

}
