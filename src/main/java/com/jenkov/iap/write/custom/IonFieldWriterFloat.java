package com.jenkov.iap.write.custom;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;
import com.jenkov.iap.write.IIonFieldWriter;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterFloat implements IIonFieldWriter {

    protected byte[] keyField = null;
    protected IGetterFloat getter = null;

    public IonFieldWriterFloat(String fieldName, IGetterFloat getter) {
        this.keyField = IonUtil.preGenerateKeyField(fieldName);
        this.getter   = getter;
    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);

    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        float value = this.getter.get(sourceObject);
        int valueIntBits = Float.floatToIntBits(value);

        //magic number "4" is the length in bytes of a 32 bit floating point number in ION.

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

        for(int i=(4-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (valueIntBits >> i));
        }

        return 5;
    }
}
