package com.jenkov.iap.tcp;

/**
 * Created by jjenkov on 10-06-2016.
 */
public class DataBlock {

    public byte[] data = null;
    public int    offset = 0;
    public int    length = 0;

    public DataBlock(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }
}
