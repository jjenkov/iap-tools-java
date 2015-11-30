package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterDouble implements IIonFieldWriter {
    protected Field  field    = null;
    protected byte[] keyField = null;


    public IonFieldWriterDouble(Field field) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(field);
    }


    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);

    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            double value = (double) field.get(sourceObject);
            long valueLongBits = Double.doubleToLongBits(value);

            //magic number "8" is the length in bytes of a 32 bit floating point number in ION.

            dest[destOffset++] = (byte) (255 & ((8 << 4) | IonFieldTypes.FLOAT));

            for(int i=(8-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (valueLongBits >> i));
            }

            return 9;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
