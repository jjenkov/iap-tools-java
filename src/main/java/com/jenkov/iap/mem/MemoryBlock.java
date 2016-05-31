package com.jenkov.iap.mem;

import java.nio.ByteBuffer;

/**
 *
 *
 */
public class MemoryBlock {

    public MemoryAllocator memoryAllocator = null;

    public int startIndex = 0;
    public int endIndex   = 0;

    public int writeIndex = 0;    //equal to the length of the block already written to.

    private boolean isComplete = false;

    public MemoryBlock(MemoryAllocator memoryAllocator) {
        this.memoryAllocator = memoryAllocator;
    }


    public int reserve(int length) {
        this.startIndex = this.memoryAllocator.reserve(length);
        this.endIndex   = this.startIndex + length;
        this.writeIndex = this.startIndex;

        return this.startIndex;
    }

    public void setComplete(boolean complete){
        this.isComplete = complete;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void free() {
        this.memoryAllocator.free(this);
    }

    public void writeLeadByte(int leadByte){
        this.memoryAllocator.data[this.writeIndex++] = (byte) (255 & (leadByte));
    }

    public void writeLength(int length, int lengthLength){
        for(int i=(lengthLength -1) * 8; i>=0; i-=8){
            this.memoryAllocator.data[this.writeIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeValue(ByteBuffer byteBuffer, int length){
        byteBuffer.get(this.memoryAllocator.data, this.writeIndex, length);
        this.writeIndex += length;
    }

}
