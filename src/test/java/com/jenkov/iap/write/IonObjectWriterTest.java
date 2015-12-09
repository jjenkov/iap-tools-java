package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.TestPojo;
import com.jenkov.iap.pojos.PojoArray10Float;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonObjectWriterTest {


    @Test
    public void test() {
        IonObjectWriter writer = new IonObjectWriter(TestPojo.class);
        byte[] dest   = new byte[1024];

        TestPojo testPojo = new TestPojo();
        Calendar calendar = testPojo.field6;
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        writer.writeObject(testPojo, 2, dest, 0);

        int index = 0;

        assertEquals((IonFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);  //object field started - 194 = object field type << 3 | 2 (length length)
        assertEquals(  0, 255 & dest[index++]);  //length of object - MSB
        assertEquals(101, 255 & dest[index++]);  //length of object - LSB

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('0', 255 & dest[index++]);  //value of char 6 field

        assertEquals((IonFieldTypes.BOOLEAN << 4) | 1, 255 & dest[index++]);  //lead byte of boolean field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('1', 255 & dest[index++]);  //value of char 6 field

        assertEquals((IonFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);  //lead byte of boolean field
        assertEquals( 1234 >> 8 , 255 & dest[index++]);  //value of char 1 field
        assertEquals( 1234 & 255, 255 & dest[index++]);  //value of char 1 field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //lead byte of compact key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('2', 255 & dest[index++]);  //value of char 6 field

        int floatBits = Float.floatToIntBits(123.12F);
        assertEquals((IonFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);  //lead byte of sshort field
        assertEquals( 255 & (floatBits >> 24), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits >> 16), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits >>  8), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits)      , 255 & dest[index++]);  //value of char 1 field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //value of char 2 field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('3', 255 & dest[index++]);  //value of char 6 field

        long longBits = Double.doubleToLongBits(123456.1234D);
        assertEquals((IonFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);  //lead byte of sint field
        assertEquals( 255 & (longBits >> 56), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 48), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 40), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 32), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 24), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 16), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >>  8), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits)      , 255 & dest[index++]);  //value of char 1 field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //value of char 2 field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('4', 255 & dest[index++]);  //value of char 6 field

        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 7, 255 & dest[index++]);  //lead byte of long field
        assertEquals('a', 255 & dest[index++]);  //value of long field
        assertEquals('b', 255 & dest[index++]);  //value of long field
        assertEquals('c', 255 & dest[index++]);  //value of long field
        assertEquals('d', 255 & dest[index++]);  //value of long field
        assertEquals('e', 255 & dest[index++]);  //value of long field
        assertEquals('f', 255 & dest[index++]);  //value of long field
        assertEquals('g', 255 & dest[index++]);  //value of long field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('5', 255 & dest[index++]);  //value of char 6 field


        assertEquals((IonFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);  //lead byte of long field
        assertEquals( 16, 255 & dest[index++]);  //value of long field
        assertEquals('0', 255 & dest[index++]);  //value of long field
        assertEquals('1', 255 & dest[index++]);  //value of long field
        assertEquals('2', 255 & dest[index++]);  //value of long field
        assertEquals('3', 255 & dest[index++]);  //value of long field
        assertEquals('4', 255 & dest[index++]);  //value of long field
        assertEquals('5', 255 & dest[index++]);  //value of long field
        assertEquals('6', 255 & dest[index++]);  //value of long field
        assertEquals('7', 255 & dest[index++]);  //value of long field
        assertEquals('8', 255 & dest[index++]);  //value of long field
        assertEquals('9', 255 & dest[index++]);  //value of long field
        assertEquals('0', 255 & dest[index++]);  //value of long field
        assertEquals('1', 255 & dest[index++]);  //value of long field
        assertEquals('2', 255 & dest[index++]);  //value of long field
        assertEquals('3', 255 & dest[index++]);  //value of long field
        assertEquals('4', 255 & dest[index++]);  //value of long field
        assertEquals('5', 255 & dest[index++]);  //value of long field

        assertEquals((IonFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('6', 255 & dest[index++]);  //value of char 6 field

        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 7, 255 & dest[index++]);  //lead byte of long field
        assertEquals(2014 >> 8 , 255 & dest[index++]);
        assertEquals(2014 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);


    }


    @Test
    public void testPojoArray10Float() {
        IonObjectWriter writer = new IonObjectWriter(PojoArray10Float.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArray10Float pojoArray10 = new PojoArray10Float(10);

        int length = writer.writeObject(pojoArray10, 2, dest, 0);
        System.out.println("length = " + length);

    }

}
