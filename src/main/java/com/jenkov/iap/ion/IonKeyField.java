package com.jenkov.iap.ion;

import java.io.UnsupportedEncodingException;

/**
 * Represents an Ion Key Field.
 *
 * Created by jjenkov on 20-02-2016.
 */
public class IonKeyField {

    public byte[] bytes = null;

    public IonKeyField(byte[] bytes) {
        this.bytes = bytes;
    }

    public IonKeyField(String keyName) {
        try{
            byte[] keyNameBytes = keyName.getBytes("UTF-8");

            if(keyNameBytes.length < 16) {
                this.bytes = new byte[1 + keyNameBytes.length];

                this.bytes[0] = (byte) (255 & (IonFieldTypes.KEY_SHORT | keyNameBytes.length));
                System.arraycopy(keyNameBytes, 0, this.bytes, 1, keyNameBytes.length);
            } else {
                int lengthLength = IonUtil.lengthOfInt64Value(keyNameBytes.length);
                this.bytes = new byte[1 + lengthLength + keyNameBytes.length];

                this.bytes[0] = (byte) (255 & (IonFieldTypes.KEY | lengthLength));

                for(int i= (lengthLength-1) * 8; i>=0; i-= 8){
                    this.bytes[i + 1] = (byte) (255 & (keyNameBytes.length >> i));
                }
                System.arraycopy(keyNameBytes, 0, this.bytes, 1 + lengthLength, keyNameBytes.length);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
