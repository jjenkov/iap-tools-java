package com.jenkov.iap.ion.write.custom;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.write.IIonFieldWriter;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterString implements IIonFieldWriter {

    protected byte[] keyField = null;
    protected IGetterString getter = null;

    public IonFieldWriterString(String fieldName, IGetterString getter) {
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
        try {
            String value = this.getter.get(sourceObject);

            //todo optimize this - do not get bytes from a string like this. UTF-8 encode char-for-char with charAt() instead.
            byte[] valueBytes = value.getBytes("UTF-8");

            int length = valueBytes.length;
            int lengthLength = IonUtil.lengthOfInt64Value(length);
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength) );

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(valueBytes, 0, dest, destOffset, valueBytes.length);

            return 1 + lengthLength + length; //total length of a UTF-8 field
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //will never happen - UTF-8 always supported
        }
        return 0;
    }
}
