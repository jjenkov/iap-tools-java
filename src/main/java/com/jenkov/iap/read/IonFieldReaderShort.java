package com.jenkov.iap.read;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderShort implements IIonFieldReader {

    private Field field = null;

    public IonFieldReaderShort(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use field type for validation ?
        int length = leadByte & 15;

        if(length == 0){
            return 1; //short field with null value is always 1 byte long.
        }

        short theShort = (short) (255 & source[sourceOffset++]);
        for(int i=1;i<length; i++){
            theShort <<= 8;
            theShort |= 255 & source[sourceOffset++];
        }

        try {
            field.set(destination, theShort);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1 + length;
    }

    @Override
    public void setNull(Object destination) {
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
