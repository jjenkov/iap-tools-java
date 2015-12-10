package com.jenkov.iap.ion.write.custom;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.write.IIonFieldWriter;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterInt64 implements IIonFieldWriter {

    protected byte[] keyField = null;
    protected IGetterInt64 getter = null;


    public IonFieldWriterInt64(String fieldName, IGetterInt64 getter) {
        this.keyField = IonUtil.preGenerateKeyField(fieldName);
        this.getter = getter;
    }


    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        long value = getter.get(sourceObject);

        int ionFieldType = IonFieldTypes.INT_POS;
        if(value < 0){
            ionFieldType = IonFieldTypes.INT_NEG;
            value  = -value;
        }

        int length = IonUtil.lengthOfInt64Value(value);

        dest[destOffset++] = (byte) (255 & ((ionFieldType << 4) | length));

        for(int i=(length-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (value >> i));
        }

        return 1 + length;
    }


}
