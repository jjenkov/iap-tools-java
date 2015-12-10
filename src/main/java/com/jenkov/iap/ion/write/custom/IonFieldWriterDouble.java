package com.jenkov.iap.ion.write.custom;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.write.IIonFieldWriter;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterDouble implements IIonFieldWriter {
    protected byte[] keyField = null;
    protected IGetterDouble getter = null;


    public IonFieldWriterDouble(String fieldName, IGetterDouble getter) {
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
        double value = this.getter.get(sourceObject);
        long valueLongBits = Double.doubleToLongBits(value);

        //magic number "8" is the length in bytes of a 32 bit floating point number in ION.

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 8));

        for(int i=(8-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (valueLongBits >> i));
        }

        return 9;
    }
}
