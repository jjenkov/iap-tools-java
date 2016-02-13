package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderObject implements IIonFieldReader {

    private Field field = null;

    private Class typeClass = null;

    private Map<IonKeyFieldKey, IIonFieldReader> fieldReaderMap = new HashMap<>();
    private IonFieldReaderNop nopFieldReader = new IonFieldReaderNop();

    private IonKeyFieldKey currentKeyFieldKey = new IonKeyFieldKey();

    public IonFieldReaderObject(Field field) {
        this.field = field;

        this.typeClass = field.getType();

        Field[] fields = this.typeClass.getDeclaredFields();

        for(int i=0; i < fields.length; i++){
            putFieldReader(fields[i], IonUtil.createFieldReader(fields[i]));
        }

    }

    private void putFieldReader(Field field, IIonFieldReader fieldReader) {
        try {
            this.fieldReaderMap.put(new IonKeyFieldKey(field.getName().getBytes("UTF-8")), fieldReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object finalDestination) {
        this.currentKeyFieldKey.setSource(source);

        Object destination =  instantiateType();

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 4;

        //todo if not object - throw exception

        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(lengthLength == 0){
            return 1; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int endIndex = sourceOffset + length;

        while(sourceOffset < endIndex){
            leadByte     = 255 & source[sourceOffset++];
            fieldType    = leadByte >> 4;
            lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

            //todo can this be optimized with a switch statement?

            //expect a key field
            if(fieldType == IonFieldTypes.KEY || fieldType == IonFieldTypes.KEY_SHORT){

                //distinguish between length and lengthLength depending on compact key field or normal key field
                length = 0;
                if(fieldType == IonFieldTypes.KEY_SHORT){
                    length = leadByte & 15;
                } else {
                    for(int i=0; i<lengthLength; i++){
                        length <<= 8;
                        length |= 255 & source[sourceOffset++];
                    }
                }

                this.currentKeyFieldKey.setOffsets(sourceOffset, length);

                IIonFieldReader reader = this.fieldReaderMap.get(this.currentKeyFieldKey);
                if(reader == null){
                    reader = this.nopFieldReader;
                }

                //find beginning of next field value - then call field reader.
                sourceOffset += length;

                //todo check for end of object - if found, call reader.setNull() - no value field following the key field.

                int nextLeadByte  = 255 & source[sourceOffset];
                int nextFieldType = nextLeadByte >> 4;

                if(nextFieldType != IonFieldTypes.KEY && nextFieldType != IonFieldTypes.KEY_SHORT){
                    sourceOffset += reader.read(source, sourceOffset, destination);
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    reader.setNull(destination);
                }
            }

        }

        try {
            this.field.set(finalDestination, destination);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1 + lengthLength + length;

    }



    @Override
    public void setNull(Object destination) {
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object instantiateType() {
        try {
            return this.typeClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null; //todo remove later when rethrowing exceptions.
    }

}
