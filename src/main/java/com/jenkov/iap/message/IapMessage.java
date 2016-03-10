package com.jenkov.iap.message;

import com.jenkov.iap.ion.types.Utf8;

/**
 * A class that can be used to represent an IAP message. An IapMessage instance does not contain all information
 * from an IAP message. An IapMessage instance primarily contains the standard IAP message headers, with a potential
 * reference to the full message via the data property.
 *
 *
 */
public class IapMessage {

    public Object dataObj = null;
    public byte[] data    = null; //a reference to the byte array containing the

    // start + end index of a byte sequence representing the sender id
    public Utf8 senderId = null;
    //public int senderIdOffset       = -1;
    //public int senderIdLength       = 0;

    // start + end index of a byte sequence representing the receiver id
    public Utf8 receiverId = null;
    //public int receiverIdOffset     = -1;
    //public int receiverIdLength     =  0;


    public long    connectionId     = -1;
    public long    channelId        = -1;
    public long    sequenceId       = -1;
    public long    sequenceIndex    = -1;
    public boolean isLastInSequence = false;


    // start + end index of a byte sequence representing the semantic protocol id
    public Utf8 semanticProtocolId = null;
    //public int semanticProtocolIdOffset = 0;
    //public int semanticProtocolIdLength = 0;

    // start + end index of a byte sequence representing the semantic protocol version
    public Utf8 semanticProtocolVersion = null;
    //public int semanticProtocolVersionOffset = 0;
    //public int semanticProtocolVersionLength = 0;

    // start + end index of a byte sequence representing the message type
    public Utf8 messageType = null;
    //public int messageTypeOffset = 0;
    //public int messageTypeLength = 0;


}
