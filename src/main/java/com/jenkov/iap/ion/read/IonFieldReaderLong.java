package com.jenkov.iap.ion.read;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderLong implements IIonFieldReader {

    private Field field = null;

    public IonFieldReaderLong(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use field type for validation ?
        int length = leadByte & 15;

        if(length == 0){
            return 1; //long field with null value is always 1 byte long
        }

        long theLong = 0;
        for(int i=0;i<length; i++){
            theLong <<= 8;
            theLong |= (255 & source[sourceOffset++]);
        }

        try {
            field.set(destination, theLong);
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
