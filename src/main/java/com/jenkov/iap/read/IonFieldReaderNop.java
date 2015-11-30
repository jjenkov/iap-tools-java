package com.jenkov.iap.read;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderNop implements IIonFieldReader {

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int fieldType    = leadByte >> 3;
        int lengthLength = leadByte & 7;  // 7 = binary 00000111 - filters out 5 top bits

        if(lengthLength == 0){
            return 1; //field with null value is always 1 byte long
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }

        return 1 + lengthLength + length;
    }

    @Override
    public void setNull(Object destination) {
        //do nothing, right?
    }


}
