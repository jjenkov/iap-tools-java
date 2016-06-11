package com.jenkov.iap.tcpserver;


import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 27-10-2015.
 */
public interface IMessageReader {

    /**
     * Initializes this IMessageReader with a MemoryAllocator. From this MemoryAllocator the IMessageReader can
     * obtain MemoryBlock instances, into which incoming messages are read.
     * @param readMemoryAllocator
     */
    public void init(MemoryAllocator readMemoryAllocator);

    /**
     * Returns the internal state of this IMessageReader. This can be used to check if the message reader is still
     * in a valid state, or if it has encountered invalid data during reading. A value of 0 means OK. Any other
     * value, both positive and negative, is to be considered an invalid state.
     *
     * @return The internal state of this IMessageReader
     */
    public int state();


    /**
     * Disposes this IMessageReader instance. Cleans up all internal variables, and in case the IMessageReader instance
     * is coming from a pool, frees the IMessageReader instance back to the pool.
     */
    public void dispose();


    /**
     * Reads zero, one or more messages from data from the ByteBuffer. The messages are read into MemoryBlock instances.
     * MemoryBlock instances are typically obtained from a MemoryAllocator, and they should be returned (freed) to
     * that MemoryAllocator again after use.
     *
     * Reading messages may change the state of the IMessageReader implementation if the IMessageReader
     * finds an invalid IAP message, or a too big IAP message etc. In that case the read() method should return only
     * the valid messages, and set is state to invalid (the state returned by state() ). It should not return any
     * part of the invalid messages.
     *
     * Once an IMessageReader is in an invalid state, that means that the TCP connection it belongs to is in an invalid
     * state. No further messages can be sensibly read from such a connection. It should just be closed.
     *
     * @param byteBuffer  The ByteBuffer with the data the IMessageReader should read. This can contain 0 messages,
     *                    a partial message, 1 full message, 1 full + 1 partial message or N full + 1 partial message.
     *                    The IMessageReader should be able to track how much it has read from partial messages, and
     *                    continue reading from there on.
     *
     * @param dest        The Object array into which fully read messages will be inserted.
     * @param destOffset  The offset into the destination array from which the IMessageReader can write messages.
     * @return
     * @throws IOException
     */
    public int read(ByteBuffer byteBuffer, MemoryBlock[] dest, int destOffset) throws IOException;

}
