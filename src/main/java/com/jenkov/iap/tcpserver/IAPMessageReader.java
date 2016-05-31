package com.jenkov.iap.tcpserver;

import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class IAPMessageReader implements IMessageReader {
    private static final int STATUS_NOTHING_READ   = 0;
    private static final int STATUS_LEAD_BYTE_READ = 1;
    private static final int STATUS_LENGTH_READ    = 2;

    private MemoryAllocator readMemoryAllocator = null;

    private int status = STATUS_NOTHING_READ;

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
    public int read(TCPSocket socket, ByteBuffer byteBuffer, Object[] dest, int destOffset) throws IOException {
        while(byteBuffer.hasRemaining()){
            switch(this.status){
                case STATUS_NOTHING_READ:  {
                    this.currentMemoryBlock = this.readMemoryAllocator.getMemoryBlock(); //allocate new Message object

                    int leadByte = 255 & byteBuffer.get();
                    fieldType    = leadByte >> 4;
                    lengthLength = leadByte & 15;
                    status       = STATUS_LEAD_BYTE_READ;

                    if(!byteBuffer.hasRemaining()) {
                        break;
                    }
                }

                case STATUS_LEAD_BYTE_READ: {
                    int lengthBytesMissing = lengthLength - lengthBytesRead;
                    int bytesRemainingInBuffer = byteBuffer.remaining();
                    int  lengthBytesToRead = Math.min(lengthBytesMissing, bytesRemainingInBuffer);

                    for(int i=0; i < lengthBytesToRead; i++){
                        this.length <<= 8;
                        this.length |= 255 & byteBuffer.get();
                    }

                    this.lengthBytesRead += lengthBytesToRead;

                    if(this.lengthBytesRead == this.lengthLength){
                        this.status = STATUS_LENGTH_READ;

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
                        this.status = STATUS_NOTHING_READ;
                        this.currentMemoryBlock = null; //todo not necessary really - but just for clarity.
                        this.length = 0;
                        this.valueBytesRead = 0;
                        this.lengthBytesRead = 0;
                    }
                }
            }
        }
        return destOffset; //return next free slot in dest array
    }
}
