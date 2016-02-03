package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterBoolean implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;

    public IonFieldWriterBoolean(Field field, String alias) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(alias);
    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        try {
            boolean value = (Boolean) field.get(sourceObject);

            if(value){
                destination[destinationOffset] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 1));
            } else {
                destination[destinationOffset] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 2));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1;    //total length of a boolean field is always 1
    }


 }
