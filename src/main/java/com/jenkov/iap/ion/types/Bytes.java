package com.jenkov.iap.ion.types;

/**
 * This class represents a raw byte sequence. A Bytes instance contains a reference to a byte[] array in which the raw byte
 * sequence is stored, an offset into the byte array where the raw byte sequence starts, and a length specifying
 * the length in bytes (not characters) of the raw byte sequence.
 */
public class Bytes {
    public byte[] source = null;
    public int    offset = 0;
    public int    length = 0;

    public Bytes() {
    }

    public Bytes(byte[] source, int offset, int length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    public boolean equals(byte[] otherSource, int otherOffset, int otherLength) {
        if(this.length != otherLength){
            return false;
        }

        for(int i=0; i<this.length; i++){
            if(this.source[this.offset + i] != otherSource[otherOffset + i]){
                return false;
            }
        }

        return true;
    }

}
