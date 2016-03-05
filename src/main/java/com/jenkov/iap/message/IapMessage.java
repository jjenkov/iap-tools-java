package com.jenkov.iap.message;

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
    public int senderIdOffset       = -1;
    public int senderIdLength       = 0;

    // start + end index of a byte sequence representing the receiver id
    public int receiverIdOffset     = -1;
    public int receiverIdLength     =  0;


    public long    connectionId     = -1;
    public long    channelId        = -1;
    public long    sequenceId       = -1;
    public long    sequenceIndex    = -1;
    public boolean isLastInSequence = false;


    // start + end index of a byte sequence representing the semantic protocol id
    public int semanticProtocolIdOffset = 0;
    public int semanticProtocolIdLength = 0;

    // start + end index of a byte sequence representing the semantic protocol version
    public int semanticProtocolVersionOffset = 0;
    public int semanticProtocolVersionLength = 0;

    // start + end index of a byte sequence representing the message type
    public int messageTypeOffset = 0;
    public int messageTypeLength = 0;


}
