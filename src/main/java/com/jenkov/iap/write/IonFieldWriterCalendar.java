package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterCalendar implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;

    protected int length = 7;

    public IonFieldWriterCalendar(Field field) {
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
            Calendar value = (Calendar) field.get(sourceObject);

            if(value == null){
                dest[destOffset++] = (byte) (255 & (IonFieldTypes.UTC_DATE_TIME << 4));
                return 1;
            }
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTC_DATE_TIME << 4) | length));

            int year = value.get(Calendar.YEAR);
            dest[destOffset++] = (byte) (255 & (year >>   8));
            dest[destOffset++] = (byte) (255 & (year &  255));

            if(length == 2) { return 3;}  // 1 + length (2)

            dest[destOffset++] = (byte) (255 & (value.get(Calendar.MONTH) + 1));

            if(length == 3) { return 4;}  // 1 + length (3)

            dest[destOffset++] = (byte) (255 & (value.get(Calendar.DAY_OF_MONTH)));

            if(length == 4) { return 5;}  // 1 + length (4)

            dest[destOffset++] = (byte) (255 & (value.get(Calendar.HOUR_OF_DAY)));

            if(length == 5) { return 6;}  // 1 + length (5)

            dest[destOffset++] = (byte) (255 & (value.get(Calendar.MINUTE)));

            if(length == 6) { return 7;}  // 1 + length (6)

            dest[destOffset++] = (byte) (255 & (value.get(Calendar.SECOND)));

            if(length == 7) { return 8;}  // 1 + length (7)

            int millis =  value.get(Calendar.MILLISECOND);
            dest[destOffset++] = (byte) (255 & (millis >>  8));
            dest[destOffset++] = (byte) (255 & (millis));

            return 10;  // 1 + length (9)

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
