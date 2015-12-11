package com.jenkov.iap.ion;

import com.jenkov.iap.ion.read.*;
import com.jenkov.iap.ion.write.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by jjenkov on 19-11-2015.
 */
public class IonUtil {

    public static final long TWO_POW_8  = 256L;
    public static final long TWO_POW_16 = TWO_POW_8 * TWO_POW_8;
    public static final long TWO_POW_24 = TWO_POW_8 * TWO_POW_16;
    public static final long TWO_POW_32 = TWO_POW_8 * TWO_POW_24;
    public static final long TWO_POW_40 = TWO_POW_8 * TWO_POW_32;
    public static final long TWO_POW_48 = TWO_POW_8 * TWO_POW_40;
    public static final long TWO_POW_56 = TWO_POW_8 * TWO_POW_48;

    public static int lengthOfInt64Value(long value){
        if(value < TWO_POW_8)  return 1;
        if(value < TWO_POW_16) return 2;
        if(value < TWO_POW_24) return 3;
        if(value < TWO_POW_32) return 4;
        if(value < TWO_POW_40) return 5;
        if(value < TWO_POW_48) return 6;
        if(value < TWO_POW_56) return 7;
        return 8;
    }

    public static IIonFieldWriter createFieldWriter(Field field){
        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new IonFieldWriterBoolean(field);
        } else if(byte.class.equals(fieldType)){
            return new IonFieldWriterByte(field);
        } else if(short.class.equals(fieldType)){
            return new IonFieldWriterShort(field);
        } else if(int.class.equals(fieldType)){
            return new IonFieldWriterInt(field);
        } else if(long.class.equals(fieldType)){
            return new IonFieldWriterLong(field);
        } else if(float.class.equals(fieldType)){
            return new IonFieldWriterFloat(field);
        } else if(double.class.equals(fieldType)){
            return new IonFieldWriterDouble(field);
        } else if(String.class.equals(fieldType)){
            return new IonFieldWriterString(field);
        } else if(Calendar.class.equals(fieldType)){
            return new IonFieldWriterCalendar(field);
        } else if(GregorianCalendar.class.equals(fieldType)){
            return new IonFieldWriterCalendar(field);
        } else if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayByte(field);
            }
            return new IonFieldWriterTable(field);
        } else {
            return new IonFieldWriterObject(field);
        }
    }

    public static IIonFieldReader createFieldReader(Field field){
        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new IonFieldReaderBoolean(field);
        } else if(short.class.equals(fieldType)){
            return new IonFieldReaderShort(field);
        } else if(int.class.equals(fieldType)){
            return new IonFieldReaderInt(field);
        } else if(long.class.equals(fieldType)){
            return new IonFieldReaderLong(field);
        } else if(float.class.equals(fieldType)){
            return new IonFieldReaderFloat(field);
        } else if(double.class.equals(fieldType)){
            return new IonFieldReaderDouble(field);
        } else if(String.class.equals(fieldType)){
            return new IonFieldReaderString(field);
        } else if(Calendar.class.equals(fieldType)){
            return new IonFieldReaderCalendar(field);
        } else if(GregorianCalendar.class.equals(fieldType)){
            return new IonFieldReaderCalendar(field);
        } else if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayByte(field);
            }
            return new IonFieldReaderTable(field);
        } else {
            return new IonFieldReaderObject(field);
        }

        //todo support object field writer

    }

    public static void writeLength(long length, int lengthLength, byte[] destination, int destinationOffset){
        switch(lengthLength){
            case 8 : { destination[destinationOffset++] = (byte) (255 & (length >> 56));}
            case 7 : { destination[destinationOffset++] = (byte) (255 & (length >> 48));}
            case 6 : { destination[destinationOffset++] = (byte) (255 & (length >> 40));}
            case 5 : { destination[destinationOffset++] = (byte) (255 & (length >> 32));}
            case 4 : { destination[destinationOffset++] = (byte) (255 & (length >> 24));}
            case 3 : { destination[destinationOffset++] = (byte) (255 & (length >> 16));}
            case 2 : { destination[destinationOffset++] = (byte) (255 & (length >>  8));}
            case 1 : { destination[destinationOffset++] = (byte) (255 &  length );}
            default : { }  //don't write anything - no length bytes to write, or invalid lengthLength (> 8)
        }
    }

    public static byte[] preGenerateKeyField(Field field) {
        return preGenerateKeyField(field.getName());
    }

    public static byte[] preGenerateKeyField(String fieldNameStr) {
        byte[] keyField  = null;
        try {
            byte[] fieldName = fieldNameStr.getBytes("UTF-8");

            int fieldNameLength = fieldName.length;
            if(fieldNameLength <= 15){
                keyField = new byte[1 + fieldName.length];
                keyField[0] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | fieldName.length));
                System.arraycopy(fieldName, 0, keyField, 1, fieldName.length);
            } else {
                int length = fieldName.length;
                int lengthLength = IonUtil.lengthOfInt64Value(length);
                keyField = new byte[1 + lengthLength + fieldName.length];

                keyField[0] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));
                int destOffset = 1;
                for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                    keyField[destOffset++] = (byte) (255 & (length >> i));
                }

                System.arraycopy(fieldName, 0, keyField, destOffset, fieldName.length);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //will never happen - UTF-8 is always supported.
        }

        return keyField;
    }




}
