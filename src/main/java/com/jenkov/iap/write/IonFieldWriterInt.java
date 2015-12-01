package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterInt implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;

    public IonFieldWriterInt(Field field) {
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
            long value = (int) field.get(sourceObject);
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

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
