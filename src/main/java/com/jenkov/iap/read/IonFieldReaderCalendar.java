package com.jenkov.iap.read;

import com.jenkov.iap.IonFieldTypes;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonFieldReaderCalendar implements IIonFieldReader {

    private static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");


    private Field field = null;

    public IonFieldReaderCalendar(Field field) {
        this.field = field;
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int lengthLength = leadByte & 15;

        if(lengthLength == 0){
            return 1; //string field (UTF-8) with null value is always 1 byte long
        }

        //int fieldType    = leadByte >> 4;   //todo use field type for validation?

        //todo can be optimized ?
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(UTC_TIME_ZONE);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        try {
            field.set(destination, calendar);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int year = (255) & source[sourceOffset++];
        year <<=8;
        year |= (255) & source[sourceOffset++];
        calendar.set(Calendar.YEAR, year);
        if(lengthLength == 2){ return 3; /* 1 + length (2) */ }

        calendar.set(Calendar.MONTH, (255 & source[sourceOffset++]) -1);
        if(lengthLength == 3){ return 4; /* 1 + length (3) */ }

        calendar.set(Calendar.DAY_OF_MONTH, (255 & source[sourceOffset++]));
        if(lengthLength == 4){ return 5; /* 1 + length (4) */ }

        calendar.set(Calendar.HOUR_OF_DAY, (255 & source[sourceOffset++]));
        if(lengthLength == 5){ return 6; /* 1 + length (5) */ }

        calendar.set(Calendar.MINUTE, (255 & source[sourceOffset++]));
        if(lengthLength == 6){ return 7; /* 1 + length (6) */ }

        calendar.set(Calendar.SECOND, (255 & source[sourceOffset++]));
        if(lengthLength == 7){ return 8; /* 1 + length (7) */ }

        int millis = 255 & source[sourceOffset++];
        millis <<= 8;
        millis |= 255 & source[sourceOffset++];
        calendar.set(Calendar.MILLISECOND, millis);

        if(lengthLength == 9){ return 10; /* 1 + length (9) */ }

        return 1 + lengthLength;
    }


    @Override
    public void setNull(Object destination) {
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
