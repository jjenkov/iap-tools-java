package com.jenkov.iap.message;

import com.jenkov.iap.error.IapToolsException;
import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.read.IonReader;

/**
 * Created by jjenkov on 20-02-2016.
 */
public class IapMessageReader {

    public IonReader ionReader = new IonReader();

    /*
    public void setSource(byte[] source, int offset, int length){
        this.ionReader.setSource(source, offset, length);
    }
    */


    public IapMessage readIapMessage(byte[] source, int sourceOffset){
        return readIapMessage(source, sourceOffset, new IapMessage());
    }



    public IapMessage readIapMessage(byte[] source, int sourceOffset, IapMessage message) {
        message.data = source;


        int leadByte     = 255 & source[sourceOffset];

        int ionFieldType        = leadByte >> 4;

        if(ionFieldType != IonFieldTypes.OBJECT){
            throw new IapToolsException("IAP message should use ION Object field type. Was something else.");
        }

        int messageLengthLength = leadByte & 15;

        System.out.println("messageLengthLength = " + messageLengthLength);

        int messageLength = 0;
        for(int i=0; i<messageLengthLength; i++){
            messageLength <<= 8;
            messageLength |= 255 & source[sourceOffset + 1 + i]; //+1 to get past lead byte
        }
        System.out.println("messageLength = " + messageLength);

        this.ionReader.setSource(source, 0, 1 + messageLengthLength + messageLength);



        //read into message ION Object field - we have verified above that it exists.
        ionReader.next();
        ionReader.parse();
        ionReader.moveInto();


        boolean endOfHeadersFound = false;
        while(!endOfHeadersFound && ionReader.hasNext()){

            ionReader.next();
            ionReader.parse();

            //if header key (name) found...
            if(ionReader.fieldType == IonFieldTypes.KEY_SHORT){

                int messageHeaderKeyValue = (int) ionReader.readKeyShortAsLong();

                switch(messageHeaderKeyValue) {

                    case IapMessageHeaders.SENDER_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        if(message.senderId != null){
                            message.senderId.source = source;
                            message.senderId.offset = ionReader.index;        // index of first byte of sender id
                            message.senderId.length = ionReader.fieldLength;  // length of sender id
                        }
                        break;
                    }

                    case IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        if(message.receiverId != null){
                            message.receiverId.source = source;
                            message.receiverId.offset = ionReader.index;
                            message.receiverId.length = ionReader.fieldLength;
                        }
                        break;
                    }

                    case IapMessageHeaders.CONNECTION_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.connectionId = ionReader.readInt64();
                        break;
                    }
                    case IapMessageHeaders.CHANNEL_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.channelId = ionReader.readInt64();
                        break;
                    }
                    case IapMessageHeaders.SEQUENCE_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.sequenceId = ionReader.readInt64();
                        break;
                    }
                    case IapMessageHeaders.SEQUENCE_INDEX_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.sequenceIndex = ionReader.readInt64();
                        break;
                    }
                    case IapMessageHeaders.IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.isLastInSequence = ionReader.readBoolean();
                        break;
                    }

                    case IapMessageHeaders.SEMANTIC_PROTOCOL_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        if(message.semanticProtocolId != null){
                            message.semanticProtocolId.source = source;
                            message.semanticProtocolId.offset = ionReader.index;
                            message.semanticProtocolId.length = ionReader.fieldLength;

                        }

                        break;
                    }
                    case IapMessageHeaders.SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        if(message.semanticProtocolVersion != null){
                            message.semanticProtocolVersion.source = source;
                            message.semanticProtocolVersion.offset = ionReader.index;
                            message.semanticProtocolVersion.length = ionReader.fieldLength;

                        }
                        break;
                    }
                    case IapMessageHeaders.MESSAGE_TYPE_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        if(message.messageType != null){
                            message.messageType.source = source;
                            message.messageType.offset = ionReader.index;
                            message.messageType.length = ionReader.fieldLength;
                        }
                        break;
                    }

                    default : {
                        endOfHeadersFound = true;
                    }
                }
            }
        }

        ionReader.moveOutOf();

        return message;
    }

}
