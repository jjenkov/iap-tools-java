package com.jenkov.iap.ion.util;

/**
 * Note: This class is not currently used, as the current implementation is significantly slower than the
 *       java.util.HashMap . We still keep this implementation here for future experimentation and measurements.
 *       Perhaps it can be possible to improve it in the future.
 */
public class OpenHashTable {

    public static class Key{
        public byte[] source = null;
        public int offset    = 0;
        public int length    = 0;
        public int hashCode  = 0;

        public Key(){}

        public Key(byte[] source){
            setSource(source, 0, source.length);
        }

        public Key(byte[] source, int offset, int length){
            setSource(source, offset, length);
        }

        public void setSource(byte[] newSource, int offset, int length){
            this.source = newSource;
            this.offset = offset;
            this.length = length;
            this.calcHashCode();
        }

        //todo smarter hashcode algorithm than +
        public void calcHashCode(){
            this.hashCode = 0;
            for(int i=this.offset, n=this.offset + this.length; i < n; i++){
                this.hashCode += this.source[i];
            }
        }

        public boolean equals(Key otherKey){
            if(this.length != otherKey.length) {
                return false;
            }

            for(int i=0; i<this.length; i++){
                if(this.source[this.offset + i] != otherKey.source[otherKey.offset + i]){
                    return false;
                }
            }
            return true;
        }
    }

    public static Key key(byte[] dest){
        return new Key(dest);
    }

    public static Key key(byte[] dest, int offset, int length){
        return new Key(dest, offset, length);
    }

    public Key[] keys   = null;
    public Object[] values = null;

    public int size     = 0;
    public int capacity = 0;
    public int nextIndexOffset = 7; //set to a prime number to minimize number of collisions, and make sure that all cells are eventually reached.

    public OpenHashTable(int capacity){
        this.capacity   = capacity;
        this.keys   = new Key[capacity];
        this.values = new Object[capacity];

        for(int i=0; i<capacity; i++){
            this.keys[i]   = null;
            this.values[i] = null;
        }
    }

    public boolean put(Key key, Object value){
        if(this.size == this.capacity){
            return false;
        }

        int index = key.hashCode % this.capacity;

        while(this.keys[index] != null && !key.equals(this.keys[index])){
            index += this.nextIndexOffset;
            if(index >= this.capacity){
                index -= this.capacity;
            }
            //index %= this.capacity;
        }
        this.keys  [index] = key;
        this.values[index] = value;

        return true;

    }

    public Object get(Key key){
        //todo add fix for the situation where all keys are filled in, but non matches.
        //     currently that situation results in an infinite loop.

        int index = key.hashCode % this.capacity;
        int startIndex = index;
        Key currentKey = this.keys[index];
        while(currentKey != null && !key.equals(this.keys[index]) && index != startIndex){
            index += this.nextIndexOffset;
            if(index >= this.capacity){
                index -= this.capacity;
            }
            //index %= this.capacity;
        }
        return this.values[index];

    }



}
