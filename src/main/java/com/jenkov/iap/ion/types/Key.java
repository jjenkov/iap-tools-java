package com.jenkov.iap.ion.types;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 12-04-2016.
 */
public class Key {
    public byte[] source = null;
    public int    offset = 0;
    public int    length = 0;

    public Key() {
    }

    public Key(byte[] source) {
        this.source = source;
        this.offset = 0;
        this.length = source.length;
    }

    public Key(byte[] source, int offset, int length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    public Key(String key){
        try {
            this.source = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.offset = 0;
        this.length = this.source.length;
    }
}
