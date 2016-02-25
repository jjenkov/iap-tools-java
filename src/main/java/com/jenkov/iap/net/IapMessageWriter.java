package com.jenkov.iap.net;

import com.jenkov.iap.ion.IonKeyField;
import com.jenkov.iap.ion.write.IonWriter;

/**
 * Created by jjenkov on 20-02-2016.
 */
public class IapMessageWriter {

    public static final IonKeyField SENDER_ID_KEY                 = new IonKeyField(new byte[]{IapMessageHeaders.SENDER_ID_KEY_FIELD_VALUE});
    public static final IonKeyField RECEIVER_ID_KEY               = new IonKeyField(new byte[]{IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE});
    public static final IonKeyField CHANNEL_ID_KEY                = new IonKeyField(new byte[]{IapMessageHeaders.CHANNEL_ID_KEY_FIELD_VALUE});
    public static final IonKeyField SEQUENCE_ID_KEY               = new IonKeyField(new byte[]{IapMessageHeaders.SEQUENCE_ID_KEY_FIELD_VALUE});
    public static final IonKeyField SEQUENCE_INDEX_KEY            = new IonKeyField(new byte[]{IapMessageHeaders.SEQUENCE_INDEX_KEY_FIELD_VALUE});
    public static final IonKeyField IS_LAST_IN_SEQUENCE_KEY       = new IonKeyField(new byte[]{IapMessageHeaders.IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE});

    public static final IonKeyField SEMANTIC_PROTOCOL_ID_KEY      = new IonKeyField(new byte[]{IapMessageHeaders.SEMANTIC_PROTOCOL_ID_KEY_FIELD_VALUE});
    public static final IonKeyField SEMANTIC_PROTOCOL_VERSION_KEY = new IonKeyField(new byte[]{IapMessageHeaders.SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE});
    public static final IonKeyField MESSAGE_TYPE_KEY              = new IonKeyField(new byte[]{IapMessageHeaders.MESSAGE_TYPE_KEY_FIELD_VALUE});


    public static int writeSenderId(byte[] dest, int destOffset, byte[] senderId) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SENDER_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, senderId);
    }

    public static int writeSenderId(byte[] dest, int destOffset, byte[] senderId, int senderIdOffset, int senderIdLength) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SENDER_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, senderId, senderIdOffset, senderIdLength);
    }

    public static int writeReceiverId(byte[] dest, int destOffset, byte[] receiverId) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, RECEIVER_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, receiverId);
    }

    public static int writeReceiverId(byte[] dest, int destOffset, byte[] receiverId, int receiverIdOffset, int receiverIdLength) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, RECEIVER_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, receiverId, receiverIdOffset, receiverIdLength );
    }

    public static int writeChannelId(byte[] dest, int destOffset, long channelId) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, CHANNEL_ID_KEY.bytes);

        return totalLength + IonWriter.writeInt64(dest, destOffset + totalLength, channelId);
    }

    public static int writeSequenceId(byte[] dest, int destOffset, long sequenceId) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEQUENCE_ID_KEY.bytes);

        return totalLength + IonWriter.writeInt64(dest, destOffset + totalLength, sequenceId);
    }

    public static int writeSequenceIndex(byte[] dest, int destOffset, long sequenceIndex) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEQUENCE_INDEX_KEY.bytes);

        return totalLength + IonWriter.writeInt64(dest, destOffset + totalLength, sequenceIndex);
    }

    public static int writeIsLastInSequence(byte[] dest, int destOffset, boolean isLastInSequence) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, IS_LAST_IN_SEQUENCE_KEY.bytes);

        return totalLength + IonWriter.writeBoolean(dest, destOffset + totalLength, isLastInSequence);
    }



    public static int writeSemanticProtocolId(byte[] dest, int destOffset, byte[] semanticProtocolId){
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEMANTIC_PROTOCOL_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, semanticProtocolId);
    }

    public static int writeSemanticProtocolId(byte[] dest, int destOffset, byte[] semanticProtocolId,
                                              int semanticProtocolIdOffset, int semanticProtocolIdLength){
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEMANTIC_PROTOCOL_ID_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, semanticProtocolId, semanticProtocolIdOffset, semanticProtocolIdLength);
    }


    public static int writeSemanticProtocolVersion(byte[] dest, int destOffset, byte[] semanticProtocolVersion) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEMANTIC_PROTOCOL_VERSION_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, semanticProtocolVersion);
    }


    public static int writeSemanticProtocolVersion(byte[] dest, int destOffset, byte[] semanticProtocolVersion, int semanticProtocolVersionOffset, int semanticProtocolVersionLength) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, SEMANTIC_PROTOCOL_VERSION_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, semanticProtocolVersion, semanticProtocolVersionOffset, semanticProtocolVersionLength);
    }


    public static int writeMessageType(byte[] dest, int destOffset, byte[] action) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, MESSAGE_TYPE_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, action);  //should this really be a UTF-8 string?
    }


    public static int writeMessageType(byte[] dest, int destOffset, byte[] action, int messageTypeOffset, int messageTypeLength) {
        int totalLength = 0;
        totalLength += IonWriter.writeKeyShort(dest, destOffset, MESSAGE_TYPE_KEY.bytes);

        return totalLength + IonWriter.writeUtf8(dest, destOffset + totalLength, action, messageTypeOffset, messageTypeLength);  //should this really be a UTF-8 string?
    }


    public static int writeIapMessageHeaders(byte[] dest, int destOffset, IapMessage message){
        int totalLength = 0;

        if(message.senderIdLength > 0){
            totalLength += writeSenderId(dest, destOffset, message.data, message.senderIdOffset, message.senderIdLength);
        }
        if(message.receiverIdLength > 0 ){
            totalLength += writeReceiverId(dest, destOffset + totalLength, message.data, message.receiverIdOffset, message.receiverIdLength);
        }

        if(message.channelId > -1 ){
            totalLength += writeChannelId(dest, destOffset + totalLength, message.channelId);
        }
        if(message.sequenceId > -1 ){
            totalLength += writeSequenceId(dest, destOffset + totalLength, message.sequenceId);
        }
        if(message.sequenceIndex > -1 ){
            totalLength += writeSequenceIndex(dest, destOffset + totalLength, message.sequenceIndex);
        }
        if(message.isLastInSequence ){
            totalLength += writeIsLastInSequence(dest, destOffset + totalLength, message.isLastInSequence);
        }

        if(message.semanticProtocolIdLength > 0){
            totalLength += writeSemanticProtocolId(dest, destOffset + totalLength, message.data, message.semanticProtocolIdOffset, message.semanticProtocolIdLength);
        }

        if(message.semanticProtocolVersionLength > 0){
            totalLength += writeSemanticProtocolVersion(dest, destOffset + totalLength, message.data, message.semanticProtocolIdOffset, message.semanticProtocolIdLength);
        }

        if(message.messageTypeLength > 0){
            totalLength += writeMessageType(dest, destOffset + totalLength, message.data, message.messageTypeOffset, message.messageTypeLength);
        }

        return totalLength;
    }





    /*
    public static int writeConnectionId() {

    }
    */

    //security level?
}
