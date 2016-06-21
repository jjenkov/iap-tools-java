package com.jenkov.iap.tcp;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class IAPMessageReader implements IMessageReader {
    public static final int INVALID_IAP_MESSAGE_NOT_ION_OBJECT  = -1;
    public static final int INVALID_IAP_MESSAGE_TOO_BIG_MESSAGE = -2;

    // 65535 = max. length expressible in a UDP packet. 8 is the UDP header length. 160 is maximum IP header length (normal is 20).
    private static final int MAX_MESSAGE_SIZE = 65535 - 8 - 160;

    private static final int STATUS_NOTHING_READ   = 0;
    private static final int STATUS_LEAD_BYTE_READ = 1;
    private static final int STATUS_LENGTH_READ    = 2;

    private MemoryAllocator readMemoryAllocator = null;


    private int validityState = 0;

    private int readStatus = STATUS_NOTHING_READ;

    private int fieldType    = 0;
    private int lengthLength = 0;
    private int length       = 0;

    private int lengthBytesRead = 0;
    private int valueBytesRead  = 0;

    private MemoryBlock currentMemoryBlock = null;



    @Override
    public void init(MemoryAllocator readMemoryAllocator) {
        this.readMemoryAllocator = readMemoryAllocator;
    }

    @Override
    public int state() {
        return this.validityState;
    }

    @Override
    public int read(ByteBuffer byteBuffer, MemoryBlock[] dest, int destOffset) throws IOException {
        int startDestOffset = destOffset;
        while(byteBuffer.hasRemaining()){
            switch(this.readStatus){
                case STATUS_NOTHING_READ:  {
                    this.currentMemoryBlock = this.readMemoryAllocator.getMemoryBlock(); //allocate new Message object

                    int leadByte = 255 & byteBuffer.get();
                    fieldType    = leadByte >> 4;

                    if(fieldType != IonFieldTypes.OBJECT){
                        this.validityState = INVALID_IAP_MESSAGE_NOT_ION_OBJECT;
                        return destOffset - startDestOffset; //return how many valid messages were read.
                    }

                    lengthLength = leadByte & 15;
                    readStatus = STATUS_LEAD_BYTE_READ;

                    if(!byteBuffer.hasRemaining()) {
                        break;
                    }
                }

                case STATUS_LEAD_BYTE_READ: {
                    int lengthBytesMissing = lengthLength - lengthBytesRead;
                    int bytesRemainingInBuffer = byteBuffer.remaining();
                    int lengthBytesToRead = Math.min(lengthBytesMissing, bytesRemainingInBuffer);

                    for(int i=0; i < lengthBytesToRead; i++){
                        this.length <<= 8;
                        this.length |= 255 & byteBuffer.get();
                    }

                    this.lengthBytesRead += lengthBytesToRead;

                    if(this.lengthBytesRead == this.lengthLength){
                        this.readStatus = STATUS_LENGTH_READ;

                        if(this.length > MAX_MESSAGE_SIZE){
                            this.validityState = INVALID_IAP_MESSAGE_TOO_BIG_MESSAGE;
                            return destOffset - startDestOffset; //return how many valid messages were read.
                        }

                        this.currentMemoryBlock.reserve(1 + this.lengthBytesRead + this.length);  //reserve space for the message.

                        //write lead byte + length into currentMessage
                        this.currentMemoryBlock.writeLeadByte((this.fieldType << 4) | this.lengthLength);
                        this.currentMemoryBlock.writeLength(this.length, this.lengthLength);

                        if(!byteBuffer.hasRemaining()){
                            break;
                        }
                    }
                }

                case STATUS_LENGTH_READ :  {
                    int valueBytesToRead = Math.min(this.length - this.valueBytesRead, byteBuffer.remaining());

                    this.currentMemoryBlock.writeValue(byteBuffer, valueBytesToRead);
                    this.valueBytesRead += valueBytesToRead;

                    if(this.valueBytesRead == this.length){
                        dest[destOffset++] = this.currentMemoryBlock;
                        this.readStatus = STATUS_NOTHING_READ;
                        this.currentMemoryBlock = null; //necessary, to avoid a MemoryBlock being freed twice (when read and processed, and in dispose() method of this class).
                        this.length = 0;
                        this.valueBytesRead = 0;
                        this.lengthBytesRead = 0;
                    }
                }
            }
        }
        return destOffset - startDestOffset; //return next free slot in dest array
    }

    @Override
    public void dispose() {
        if(this.currentMemoryBlock != null){
            this.currentMemoryBlock.free();
        }
    }
}
