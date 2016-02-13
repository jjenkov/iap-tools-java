package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderNop implements IIonFieldReader {

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int fieldType    = leadByte >> 4;
        int lengthLength = leadByte & 15;  // 7 = binary 00000111 - filters out 5 top bits

        if(lengthLength == 0){
            return 1; //field with null value is always 1 byte long
        }

        //todo skip correct amount of bytes - depending on field type. Not all field types have explicit length bytes.

        switch(fieldType){
            case IonFieldTypes.TINY: {
                return 1;
            }
            case IonFieldTypes.UTF_8_SHORT: ;
            case IonFieldTypes.UTC_DATE_TIME: ;
            case IonFieldTypes.COMPLEX_TYPE_ID_SHORT: ;
            case IonFieldTypes.KEY_SHORT: ;
            case IonFieldTypes.INT_POS: ;
            case IonFieldTypes.INT_NEG: ;
            case IonFieldTypes.FLOAT : {
                return 1 + lengthLength;
            }

            case IonFieldTypes.EXTENDED : {
                int fieldTypeExtended = source[sourceOffset++]; //read extended field type - first byte after lead byte
                switch(fieldTypeExtended) {
                    case IonFieldTypes.ELEMENT_COUNT : {
                        return 1 + 1 + lengthLength; //element count uses extended short encoding.
                    }
                }
                return 1 + 1 + lengthLength; //default extended element encoding uses 1 byte for extended type
            }

            //fine for all fields that use the lengthLength field normally - meaning Normal length fields (not Short and Tiny).
            default : {
                int fieldLength = 0;
                for(int i=0; i<lengthLength; i++){
                    fieldLength <<= 8;
                    fieldLength |= 255 & source[sourceOffset++];
                }
                return 1 + lengthLength + fieldLength;
            }
        }

    }

    @Override
    public void setNull(Object destination) {
        //do nothing, right?
    }


}
