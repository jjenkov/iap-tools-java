package com.jenkov.iap.tcp;

/**
 * A Queue holds objects of some kind. A Queue normally sits between a producer and a consumer of some kind.
 * A Queue is passive. Adding elements to a Queue does not automatically push those messages through to the consumer.
 * The consumer has to pull the elements out of the Queue again. This gives the consumer greater flexibility to
 * process messages when it suits the consumer.
 *
 * The Queue is intended to be used in a single threaded application, so it is not thread safe. The same thread
 * has to push element into the queue and pull them out again.
 *
 * Also, the elements can only be pulled out of the Queue once. It is not designed for having multiple consumers.
 *
 * Created by jjenkov on 16-09-2015.
 */
public class Queue {

    public Object[] elements = null;

    public int capacity = 0;
    public int writePos = 0;
    public int readPos  = 0;
    public boolean flipped = false;

    public Queue(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];   //todo get from TypeAllocator ?
    }

    public void reset() {
        this.writePos = 0;
        this.readPos  = 0;
        this.flipped  = false;
    }

    public int available() {
        if(!flipped){
            return writePos - readPos;
        }
        return capacity - readPos + writePos;
    }

    public int remainingCapacity() {
        if(!flipped){
            return capacity - writePos;
        }
        return readPos - writePos;
    }

    public boolean put(Object element){
        if(!flipped){
            if(writePos == capacity){
                writePos = 0;
                flipped = true;

                if(writePos < readPos){
                    elements[writePos++] = element;
                    return true;
                } else {
                    return false;
                }
            } else {
                elements[writePos++] = element;
                return true;
            }
        } else {
            if(writePos < readPos ){
                elements[writePos++] = element;
                return true;
            } else {
                return false;
            }
        }
    }

    public int put(Object[] newElements, int length){
        int newElementsReadPos = 0;
        if(!flipped){
            //readPos lower than writePos - free sections are:
            //1) from writePos to capacity
            //2) from 0 to readPos

            if(length <= capacity - writePos){
                //new elements fit into top of elements array - copy directly
                for(; newElementsReadPos < length; newElementsReadPos++){
                    this.elements[this.writePos++] = newElements[newElementsReadPos];
                }

                return newElementsReadPos;
            } else {
                //new elements must be divided between top and bottom of elements array

                //writing to top
                for(;this.writePos < capacity; this.writePos++){
                    this.elements[this.writePos] = newElements[newElementsReadPos++];
                }

                //writing to bottom
                this.writePos = 0;
                this.flipped  = true;
                int endPos = Math.min(this.readPos, length - newElementsReadPos);
                for(; this.writePos < endPos; this.writePos++){
                    this.elements[writePos] = newElements[newElementsReadPos++];
                }


                return newElementsReadPos;
            }

        } else {
            //readPos higher than writePos - free sections are:
            //1) from writePos to readPos

            int endPos = Math.min(this.readPos, this.writePos + length);

            for(; this.writePos < endPos; this.writePos++){
                this.elements[this.writePos] = newElements[newElementsReadPos++];
            }

            return newElementsReadPos;
        }
    }

    public Object peek() {
        if(available() == 0){
            return null;
        }
        if(readPos == capacity){
            readPos = 0;
            flipped = false;
        }
        return elements[readPos];
    }


    public Object take() {
        if(!flipped){
            if(readPos < writePos){
                return elements[readPos++];
            } else {
                return null;
            }
        } else {
            if(readPos == capacity){
                readPos = 0;
                flipped = false;

                if(readPos < writePos){
                    return elements[readPos++];
                } else {
                    return null;
                }
            } else {
                return elements[readPos++];
            }
        }
    }

    public int take(Object[] into, int length){
        int intoWritePos = 0;
        if(!flipped){
            //writePos higher than readPos - available section is writePos - readPos

            int endPos = Math.min(this.writePos, this.readPos + length);
            for(; this.readPos < endPos; this.readPos++){
                into[intoWritePos++] = this.elements[this.readPos];
            }
            return intoWritePos;
        } else {
            //readPos higher than writePos - available sections are top + bottom of elements array

            if(length <= capacity - readPos){
                //length is lower than the elements available at the top of the elements array - copy directly
                for(; intoWritePos < length; intoWritePos++){
                    into[intoWritePos] = this.elements[this.readPos++];
                }

                return intoWritePos;
            } else {
                //length is higher than elements available at the top of the elements array
                //split copy into a copy from both top and bottom of elements array.

                //copy from top
                for(; this.readPos < capacity; this.readPos++){
                    into[intoWritePos++] = this.elements[this.readPos];
                }

                //copy from bottom
                this.readPos = 0;
                this.flipped = false;
                int endPos = Math.min(this.writePos, length - intoWritePos);
                for(; this.readPos < endPos; this.readPos++){
                    into[intoWritePos++] = this.elements[this.readPos];
                }

                return intoWritePos;
            }
        }
    }
}
