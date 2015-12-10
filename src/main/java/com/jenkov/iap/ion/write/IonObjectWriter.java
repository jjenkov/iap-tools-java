package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonObjectWriter {

    public Class   typeClass = null;
    public Field[] fields    = null;
    public IIonFieldWriter[] fieldWriters = null;

    public IonObjectWriter(Class typeClass) {
        this.typeClass = typeClass;

        this.fields = this.typeClass.getDeclaredFields();
        this.fieldWriters = new IIonFieldWriter[this.fields.length];

        for(int i=0; i < this.fields.length; i++){
            fieldWriters[i] = IonUtil.createFieldWriter(this.fields[i]);
        }
    }

    public int writeObject(Object src, int maxLengthLength, byte[] destination, int destinationOffset){

        destination[destinationOffset++] = (byte) (255 & ((IonFieldTypes.OBJECT << 4) | maxLengthLength));

        int lengthOffset   = destinationOffset; //store length start offset for later use
        destinationOffset += maxLengthLength;



        for(int i=0; i<fieldWriters.length; i++){
            if(fieldWriters[i] != null){
                destinationOffset += fieldWriters[i].writeKeyAndValueFields(src, destination, destinationOffset, maxLengthLength);
            }
        }

        int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

        switch(maxLengthLength){
            case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
            case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
            case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
            case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
        }

        return 1 + maxLengthLength + fullFieldLength;
    }


}
