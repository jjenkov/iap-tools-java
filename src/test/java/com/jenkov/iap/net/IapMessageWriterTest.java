package com.jenkov.iap.net;

import com.jenkov.iap.ion.IonFieldTypes;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 21-02-2016.
 */
public class IapMessageWriterTest {

    byte[] dest = new byte[10 * 1024];



    @Test
    public void testWriteSemanticProtocolId() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeSemanticProtocolId(dest, 0, "http".getBytes("UTF-8"));

        assertEquals(7, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEMANTIC_PROTOCOL_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 4, 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
    }

    @Test
    public void testWriteSemanticProtocolVersion() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeSemanticProtocolVersion(dest, 0, "1.1".getBytes("UTF-8"));

        assertEquals(6, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);
        assertEquals('.', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);
    }

    @Test
    public void testWriteSenderId() throws UnsupportedEncodingException {

        byte[] senderIdBytes = "sender".getBytes("UTF-8");

        int bytesWritten = IapMessageWriter.writeSenderId(dest, 0, senderIdBytes);

        assertEquals(9, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SENDER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);

        index = 10;
        bytesWritten = IapMessageWriter.writeSenderId(dest, index, senderIdBytes, 1, 4);
        assertEquals(7, bytesWritten);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SENDER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 4, 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
    }


    @Test
    public void testWriteReceiverId() throws UnsupportedEncodingException {

        byte[] receiverIdBytes = "receiver".getBytes("UTF-8");

        int bytesWritten = IapMessageWriter.writeReceiverId(dest, 0, receiverIdBytes);

        assertEquals(11, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 8, 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('v', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);

        index = 10;
        bytesWritten = IapMessageWriter.writeReceiverId(dest, index, receiverIdBytes, 1, 6);
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('v', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

    }

    @Test
    public void testWriteChannelId() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeChannelId(dest, 0, 1024);

        assertEquals(5, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.CHANNEL_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);
        assertEquals(1024 >> 8, 255 & dest[index++]);
        assertEquals(1024 & 255, 255 & dest[index++]);
    }

    @Test
    public void testWriteSequenceId() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeSequenceId(dest, 0, 1024);

        assertEquals(5, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEQUENCE_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);
        assertEquals(1024 >> 8, 255 & dest[index++]);
        assertEquals(1024 & 255, 255 & dest[index++]);
    }

    @Test
    public void testWriteSequenceIndex() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeSequenceIndex(dest, 0, 1024);

        assertEquals(5, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEQUENCE_INDEX_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);
        assertEquals(1024 >> 8, 255 & dest[index++]);
        assertEquals(1024 & 255, 255 & dest[index++]);
    }

    @Test
    public void testWriteIsLastInSequence() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeIsLastInSequence(dest, 0, true);

        assertEquals(3, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.TINY << 4) | 1, 255 & dest[index++]);


        bytesWritten = IapMessageWriter.writeIsLastInSequence(dest, 0, false);

        assertEquals(3, bytesWritten);

        index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.TINY << 4) | 2, 255 & dest[index++]);
    }

    @Test
    public void testWriteMessageType() throws UnsupportedEncodingException {

        int bytesWritten = IapMessageWriter.writeMessageType(dest, 0, "call".getBytes("UTF-8"));

        assertEquals(7, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.MESSAGE_TYPE_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 4, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
    }

    @Test
    public void testWriteIapMessageHeaders() {
        IapMessage message = new IapMessage();

        int bytesWritten = IapMessageWriter.writeIapMessageHeaders(dest, 0, message, 2);

        assertEquals(3, bytesWritten);

        message.data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

        message.senderIdOffset = 1;
        message.senderIdLength = 3;

        message.receiverIdOffset = 6;
        message.receiverIdLength = 3;

        message.channelId = 11;
        message.sequenceId = 12;
        message.sequenceIndex = 13;
        message.isLastInSequence = true;

        message.semanticProtocolIdOffset = 16;
        message.semanticProtocolIdLength = 3;

        message.semanticProtocolVersionOffset = 21;
        message.semanticProtocolVersionLength = 3;

        message.messageTypeOffset = 26;
        message.messageTypeLength = 3;


        //message.semanticProtocolIdLength =

        int index = 0;
        bytesWritten = IapMessageWriter.writeIapMessageHeaders(dest, index, message, 2);

        assertEquals((IonFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);   //reserved 2 empty bytes for the length later (lengthLength = 2).
        assertEquals(0, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SENDER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(1, 255 & dest[index++]);
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(6, 255 & dest[index++]);
        assertEquals(7, 255 & dest[index++]);
        assertEquals(8, 255 & dest[index++]);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.CHANNEL_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(11, 255 & dest[index++]);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEQUENCE_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(12, 255 & dest[index++]);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEQUENCE_INDEX_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(13, 255 & dest[index++]);


        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.TINY << 4) | 1, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEMANTIC_PROTOCOL_ID_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(16, 255 & dest[index++]);
        assertEquals(17, 255 & dest[index++]);
        assertEquals(18, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(16, 255 & dest[index++]);
        assertEquals(17, 255 & dest[index++]);
        assertEquals(18, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 1, 255 & dest[index++]);
        assertEquals(IapMessageHeaders.MESSAGE_TYPE_KEY_FIELD_VALUE, 255 & dest[index++]);

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(26, 255 & dest[index++]);
        assertEquals(27, 255 & dest[index++]);
        assertEquals(28, 255 & dest[index++]);
    }



}
