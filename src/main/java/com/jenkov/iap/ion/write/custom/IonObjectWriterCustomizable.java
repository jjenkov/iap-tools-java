package com.jenkov.iap.ion.write.custom;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.write.IIonFieldWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonObjectWriterCustomizable {

    public IIonFieldWriter[] fieldWriters = null;
    private List<IIonFieldWriter> fieldWritersTemp = new ArrayList<>();


    public IonObjectWriterCustomizable() {
    }

    public void init() {
        this.fieldWriters = new IIonFieldWriter[this.fieldWritersTemp.size()];

        for(int i=0; i<this.fieldWritersTemp.size(); i++){
            this.fieldWriters[i] = this.fieldWritersTemp.get(i);
        }
    }

    public void addStringFieldWriter(String fieldName, IGetterString getter){
        this.fieldWritersTemp.add(new IonFieldWriterString(fieldName, getter));
    }

    public void addInt64FieldWriter(String fieldName, IGetterInt64 getter){
        this.fieldWritersTemp.add(new IonFieldWriterInt64(fieldName, getter));
    }

    public void addBooleanFieldWriter(String fieldName, IGetterBoolean getter){
        this.fieldWritersTemp.add(new IonFieldWriterBoolean(fieldName, getter));
    }

    public void addFloatFieldWriter(String fieldName, IGetterFloat getter){
        this.fieldWritersTemp.add(new IonFieldWriterFloat(fieldName, getter));
    }

    public void addDoubleFieldWriter(String fieldName, IGetterDouble getter){
        this.fieldWritersTemp.add(new IonFieldWriterDouble(fieldName, getter));
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
