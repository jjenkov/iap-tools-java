package com.jenkov.iap.net;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.read.IonReader;

/**
 * Created by jjenkov on 20-02-2016.
 */
public class IapMessageReader {

    public IonReader ionReader = new IonReader();

    public void setSource(byte[] source, int offset, int length){
        this.ionReader.setSource(source, offset, length);
    }


    public IapMessage readIapMessage(byte[] source, int sourceOffset){
        return readIapMessage(source, sourceOffset, new IapMessage());
    }



    public IapMessage readIapMessage(byte[] source, int sourceOffset, IapMessage message) {

        message.data = source;

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

                        message.senderIdOffset = ionReader.index;        // index of first byte of sender id
                        message.senderIdLength = ionReader.fieldLength;  // length of sender id
                        break;
                    }

                    case IapMessageHeaders.RECEIVER_ID_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.receiverIdOffset = ionReader.index;
                        message.receiverIdLength = ionReader.fieldLength;
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

                        message.semanticProtocolIdOffset = ionReader.index;
                        message.semanticProtocolIdLength = ionReader.fieldLength;
                        break;
                    }
                    case IapMessageHeaders.SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.semanticProtocolVersionOffset = ionReader.index;
                        message.semanticProtocolVersionLength = ionReader.fieldLength;
                        break;
                    }
                    case IapMessageHeaders.MESSAGE_TYPE_KEY_FIELD_VALUE : {
                        ionReader.next();
                        ionReader.parse();

                        message.messageTypeOffset = ionReader.index;
                        message.messageTypeLength = ionReader.fieldLength;
                        break;
                    }

                    default : {
                        endOfHeadersFound = true;
                    }
                }
            }
        }

        return message;
    }

}
