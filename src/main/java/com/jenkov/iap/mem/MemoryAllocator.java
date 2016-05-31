package com.jenkov.iap.mem;

import java.util.Arrays;

/**
 * Todo find a better name for this class than MemoryBuffer. E.g. MemorySlotManager, MemorySlotBuffer or something like that.
 * Created by jjenkov on 06-10-2015.
 */
public class MemoryAllocator {

    private static long TO_AND_MASK = (long) Math.pow(2, 32)-1L;

    public byte[] data = null;
    public long[] freeBlocks = null;
    public long[] usedBlocks = null;

    public int nextFreeBlockIndex = 0;
    public int nextUsedBlockIndex = 0;

    private MemoryBlock[] pooledMemoryBlocks = new MemoryBlock[1024 * 1024]; //max 1M messages pooled.
    private int pooledMessageCount = 0;

    private IMemoryBlockFactory memoryBlockFactory = null;


    public MemoryAllocator(byte[] data, long[] freeBlocks, IMemoryBlockFactory memoryBlockFactory) {
        init(data, freeBlocks, memoryBlockFactory);
    }

    public MemoryAllocator(byte[] data, long[] freeBlocks) {
        init(data, freeBlocks, (allocator) -> { return new MemoryBlock(allocator); });
    }

    private void init(byte[] data, long[] freeBlocks, IMemoryBlockFactory factory) {
        this.data = data;
        this.freeBlocks = freeBlocks;
        this.memoryBlockFactory = factory;

        free(0, data.length);
    }

    public MemoryBlock getMemoryBlock() {
        if(this.pooledMessageCount > 0){
            this.pooledMessageCount--;
            return this.pooledMemoryBlocks[this.pooledMessageCount];
        }
        return this.memoryBlockFactory.createMemoryBlock(this);

    }



    //todo allocate()
    public int reserve(int blockSize){

        boolean freeBlockFound = false;

        int freeBlockIndex = 0;

        while(!freeBlockFound && freeBlockIndex < this.nextFreeBlockIndex){
            long fromTemp = this.freeBlocks[freeBlockIndex];
            fromTemp >>=32;

            long toTemp   = this.freeBlocks[freeBlockIndex];
            toTemp &= TO_AND_MASK;

            if(blockSize <= (toTemp-fromTemp)){
                freeBlockFound = true;

                long newBlockDescriptor = fromTemp + blockSize;
                newBlockDescriptor <<= 32;

                newBlockDescriptor += toTemp;

                this.freeBlocks[freeBlockIndex] = newBlockDescriptor;
                return (int) fromTemp;
            } else {
                freeBlockIndex++;
            }
        }
        return -1;
    }

    public void free(MemoryBlock memoryBlock) {
        //pool message if space
        if(this.pooledMessageCount < this.pooledMemoryBlocks.length){
            this.pooledMemoryBlocks[this.pooledMessageCount] = memoryBlock;
            this.pooledMessageCount++;
        }

        free(memoryBlock.startIndex, memoryBlock.endIndex);
    }

    public void free(int from, int to){
        long blockDescriptor = from;
        blockDescriptor <<= 32;

        blockDescriptor += to;

        this.freeBlocks[nextFreeBlockIndex] = blockDescriptor;
        nextFreeBlockIndex++;

        if(nextFreeBlockIndex == 10000){
            garbageCollect();
        }
    }

    public void garbageCollect() {
        //sort
        Arrays.sort(this.freeBlocks, 0, this.nextFreeBlockIndex);

        //merge
        int newIndex = 0;

        for(int i=0; i < nextFreeBlockIndex;){
            long from = this.freeBlocks[i];
            from >>=32;

            long to   = this.freeBlocks[i];
            to &= TO_AND_MASK;

            int nextIndex  = i + 1;

            long nextFrom = this.freeBlocks[nextIndex];
            nextFrom >>=32;

            long nextTo   = this.freeBlocks[nextIndex];
            nextTo &= TO_AND_MASK;

            while(to == nextFrom ){
                to = nextTo;      //todo this can be moved to after while loop?
                nextIndex++;
                if(nextIndex == this.nextFreeBlockIndex){
                    break;
                }

                nextFrom   = this.freeBlocks[nextIndex];
                nextFrom >>=32;

                nextTo     = this.freeBlocks[nextIndex];
                nextTo    &= TO_AND_MASK;
            }

            i = nextIndex;

            long newBlockDescriptor = from;
            newBlockDescriptor <<= 32;

            newBlockDescriptor += to;

            this.freeBlocks[newIndex] = newBlockDescriptor;
            newIndex++;
        }
        this.nextFreeBlockIndex = newIndex;
    }

}
