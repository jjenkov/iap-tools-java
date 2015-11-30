package com.jenkov.iap.read;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonObjectReader {

    private Class typeClass = null;
    private Field[] fields = null;

    private Map<IonKeyFieldKey, IIonFieldReader> fieldReaderMap = new HashMap<>();
    private IonFieldReaderNop nopFieldReader = new IonFieldReaderNop();

    private IonKeyFieldKey currentKeyFieldKey = new IonKeyFieldKey();

    public IonObjectReader(Class typeClass) {
        this.typeClass = typeClass;

        this.fields = this.typeClass.getDeclaredFields();

        for(int i=0; i < this.fields.length; i++){
            fields[i].setAccessible(true);
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


    public Object read(byte[] source, int sourceOffset){
        this.currentKeyFieldKey.setSource(source);

        Object destination =  instantiateType();

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 3;

        //todo if not object - throw exception

        int lengthLength = leadByte & 7;  // 7 = binary 00000111 - filters out 5 top bits

        if(lengthLength == 0){
            return null; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int endIndex = sourceOffset + length;



        while(sourceOffset < endIndex){
            leadByte     = 255 & source[sourceOffset++];
            fieldType    = leadByte >> 3;
            lengthLength = leadByte & 7;  // 7 = binary 00000111 - filters out 5 top bits

            //todo can this be optimized with a switch statement?

            //expect a key field
            if(fieldType == IonFieldTypes.KEY || fieldType == IonFieldTypes.KEY_COMPACT){

                //distinguish between length and lengthLength depending on compact key field or normal key field
                length = 0;
                if(fieldType == IonFieldTypes.KEY_COMPACT){
                    length = leadByte & 7;
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
                int nextFieldType = nextLeadByte >> 3;

                if(nextFieldType != IonFieldTypes.KEY && nextFieldType != IonFieldTypes.KEY_COMPACT){
                    sourceOffset += reader.read(source, sourceOffset, destination);
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    reader.setNull(destination);
               }
            }

        }

        return destination;
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
