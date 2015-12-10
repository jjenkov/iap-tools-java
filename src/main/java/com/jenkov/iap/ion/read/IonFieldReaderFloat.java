package com.jenkov.iap.ion.read;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderFloat implements IIonFieldReader {

    private Field field = null;

    public IonFieldReaderFloat(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //use for validation of field type?
        int length = leadByte & 15;

        if(length == 0){
            return 1;  // float field with null value is always 1 byte long
        }

        /*
        int theInt = 255 & source[sourceOffset++];
        for(int i=1; i < length; i++){
            theInt <<= 8;
            theInt |= 255 & source[sourceOffset++];
        }
        */
        int theInt = 255 & source[sourceOffset++];
        theInt <<= 8;
        theInt |= 255 & source[sourceOffset++];
        theInt <<= 8;
        theInt |= 255 & source[sourceOffset++];
        theInt <<= 8;
        theInt |= 255 & source[sourceOffset++];

        try {
            field.set(destination, Float.intBitsToFloat(theInt));
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
