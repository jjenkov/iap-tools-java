package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderString implements IIonFieldReader {

    private Field field = null;

    public IonFieldReaderString(Field field) {
        this.field = field;
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int lengthLength = leadByte & 15;

        if(lengthLength == 0){
            return 1; //string field (UTF-8) with null value is always 1 byte long
        }

        int fieldType    = leadByte >> 4;   //todo use field type for validation?
        if(fieldType == IonFieldTypes.UTF_8_SHORT){
            int length = lengthLength;

            try {
                field.set(destination, new String(source, sourceOffset, length, "UTF-8"));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return 1 + length;

        } else {
            int length = 255 & source[sourceOffset++];
            for(int i=1; i<lengthLength; i++){
                length <<= 8;
                length |= 255 & source[sourceOffset++];
            }

            try {
                field.set(destination, new String(source, sourceOffset, length, "UTF-8"));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return 1 + lengthLength + length;
        }

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
