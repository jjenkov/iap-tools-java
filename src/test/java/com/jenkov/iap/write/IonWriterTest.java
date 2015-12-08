package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 20-11-2015.
 */
public class IonWriterTest {


    @Test
    public void testBytes() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeBytes(dest, offset, new byte[] { 1,2,3,4,5 });

        assertEquals(7, bytesWritten);

        assertEquals((IonFieldTypes.BYTES << 4) | 1 , 15 & (dest[offset++]));
        assertEquals(5, 255 & (dest[offset++]));
        assertEquals(1, 255 & (dest[offset++]));
        assertEquals(2, 255 & (dest[offset++]));
        assertEquals(3, 255 & (dest[offset++]));
        assertEquals(4, 255 & (dest[offset++]));
        assertEquals(5, 255 & (dest[offset++]));

        offset = 20;
        bytesWritten = IonWriter.writeBytes(dest, offset, null);
        assertEquals(0, 255 & (dest[offset] >> 4));
        assertEquals(IonFieldTypes.BYTES << 4, 15 & (dest[offset++]));

    }



    @Test
    public void testBoolean() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeBoolean(dest, offset, false);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.BOOLEAN<<4) | 2 , dest[offset]);

        offset = 20;
        bytesWritten = IonWriter.writeBoolean(dest, offset, true);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.BOOLEAN<<4) | 1, dest[offset]);
    }


    @Test
    public void testBooleanObj() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeBooleanObj(dest, offset, false);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.BOOLEAN<<4) | 2, dest[offset]);

        offset = 20;
        bytesWritten = IonWriter.writeBooleanObj(dest, offset, true);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.BOOLEAN<<4) | 1, dest[offset]);

        offset = 30;
        bytesWritten = IonWriter.writeBooleanObj(dest, offset, null);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.BOOLEAN<<4) | 0, dest[offset]);
    }


    @Test
    public void testInt64() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeInt64(dest, offset, 65535);

        assertEquals(3, bytesWritten);
        assertEquals((IonFieldTypes.INT_POS<<4) | 2, dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);


        offset = 20;
        bytesWritten = IonWriter.writeInt64(dest, offset, -65535);

        assertEquals(3, bytesWritten);
        assertEquals((IonFieldTypes.INT_NEG<<4) | 2, dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
    }


    @Test
    public void testInt64Obj() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeInt64Obj(dest, offset, 65535L);

        assertEquals(3, bytesWritten);
        assertEquals((IonFieldTypes.INT_POS<<4) | 2, dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeInt64Obj(dest, offset, -65535L);

        assertEquals(3, bytesWritten);
        assertEquals((IonFieldTypes.INT_NEG<<4) | 2, dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeInt64Obj(dest, offset, null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.INT_POS << 4, dest[offset++]);
    }


    @Test
    public void testFloat32() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeFloat32(dest, offset, 123.45F);

        int intBits = Float.floatToIntBits(123.45F);

        assertEquals(5, bytesWritten);
        assertEquals( (IonFieldTypes.FLOAT<<4) | 4, dest[offset++]);
        assertEquals(255 & (intBits >> 24), 255 & dest[offset++]);
        assertEquals(255 & (intBits >> 16), 255 & dest[offset++]);
        assertEquals(255 & (intBits >>  8), 255 & dest[offset++]);
        assertEquals(255 &  intBits       , 255 & dest[offset++]);
    }


    @Test
    public void testFloat32Obj() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeFloat32Obj(dest, offset, 123.45F);

        int intBits = Float.floatToIntBits(123.45F);

        assertEquals(5, bytesWritten);
        assertEquals( (IonFieldTypes.FLOAT<<4) | 4, dest[offset++]);
        assertEquals(255 & (intBits >> 24), 255 & dest[offset++]);
        assertEquals(255 & (intBits >> 16), 255 & dest[offset++]);
        assertEquals(255 & (intBits >>  8), 255 & dest[offset++]);
        assertEquals(255 &  intBits       , 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeFloat32Obj(dest, offset, null);
        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.FLOAT << 4, dest[offset++]);

    }


    @Test
    public void testFloat64() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeFloat64(dest, offset, 123.45D);

        long longBits = Double.doubleToLongBits(123.45D);

        assertEquals(9, bytesWritten);
        assertEquals((IonFieldTypes.FLOAT<<4) | 8, 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 56), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 48), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 40), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 32), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 24), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 16), 255 & dest[offset++]);
        assertEquals(255 & (longBits >>  8), 255 & dest[offset++]);
        assertEquals(255 &  longBits       , 255 & dest[offset++]);
    }


    @Test
    public void testFloat64Obj() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeFloat64Obj(dest, offset, 123.45D);

        long longBits = Double.doubleToLongBits(123.45D);

        assertEquals(9, bytesWritten);
        assertEquals((IonFieldTypes.FLOAT<<4) | 8, 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 56), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 48), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 40), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 32), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 24), 255 & dest[offset++]);
        assertEquals(255 & (longBits >> 16), 255 & dest[offset++]);
        assertEquals(255 & (longBits >>  8), 255 & dest[offset++]);
        assertEquals(255 &  longBits       , 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeFloat64Obj(dest, offset, null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.FLOAT << 4, 255 & dest[offset++]);
    }


    @Test
    public void testUtf8() throws UnsupportedEncodingException {
        byte[] dest = new byte[10 * 1024];

        String value  = "Hello World Long";

        int offset = 10;
        int bytesWritten = IonWriter.writeUtf8(dest, offset, value);

        assertEquals(18, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8<<4) | 1, 255 & dest[offset++]);
        assertEquals(16, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('W', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('r', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('d', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('L', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('n', 255 & dest[offset++]);
        assertEquals('g', 255 & dest[offset++]);


        offset = 20;
        bytesWritten = IonWriter.writeUtf8(dest, offset, (String) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.UTF_8 << 4, 255 & dest[offset++]);


        offset = 30;
        bytesWritten = IonWriter.writeUtf8(dest, offset, (byte[]) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.UTF_8 << 4, 255 & dest[offset++]);

        byte[] value2 = "Hello World Long".getBytes("UTF-8");

        offset = 40;
        bytesWritten = IonWriter.writeUtf8(dest, offset, value2);

        assertEquals(18, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8<<4) | 1, 255 & dest[offset++]);
        assertEquals(16, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('W', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('r', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('d', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('L', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('n', 255 & dest[offset++]);
        assertEquals('g', 255 & dest[offset++]);

    }


    public void testUtf8Short() throws UnsupportedEncodingException {
        byte[] dest = new byte[10 * 1024];

        String value  = "Hello World";

        int offset = 10;
        int bytesWritten = IonWriter.writeUtf8(dest, offset, value);

        assertEquals(12, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8_SHORT<<4) | 1, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('W', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('r', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('d', 255 & dest[offset++]);


        offset = 20;
        bytesWritten = IonWriter.writeUtf8(dest, offset, (String) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.UTF_8 << 4, 255 & dest[offset++]);


        offset = 30;
        bytesWritten = IonWriter.writeUtf8(dest, offset, (byte[]) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.UTF_8 << 4, 255 & dest[offset++]);

        byte[] value2 = "Hello World".getBytes("UTF-8");

        offset = 40;
        bytesWritten = IonWriter.writeUtf8(dest, offset, value2);

        assertEquals(12, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8_SHORT<<4) | 1, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals(' ', 255 & dest[offset++]);
        assertEquals('W', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);
        assertEquals('r', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('d', 255 & dest[offset++]);

    }


    @Test
    public void testUtcTime() {
        byte[] dest   = new byte[10 * 1024];

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2015);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        int index = 0;
        int bytesWritten = IonWriter.writeUtc(dest, index, calendar, 2);
        assertEquals(3, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 2, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015  & 255, 255 & dest[index++]);

        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 3);
        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 3, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);

        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 4);
        assertEquals(5, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 4, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);


        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 5);
        assertEquals(6, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 5, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);


        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 6);
        assertEquals(7, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 6, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);

        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 7);
        assertEquals(8, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 7, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);

        bytesWritten = IonWriter.writeUtc(dest, index, calendar, 9);
        assertEquals(10, bytesWritten);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 9, 255 & dest[index++]);
        assertEquals(2015 >> 8, 255 & dest[index++]);
        assertEquals(2015 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);
        assertEquals( 999 >> 8  , 255 & dest[index++]);
        assertEquals( 999 & 255 , 255 & dest[index++]);

    }


    @Test
    public void testComplexTypeIdShort()  {
        byte[] dest   = new byte[10 * 1024];

        byte[] typeId = new byte[] {1,2,3};

        int offset = 10;
        int bytesWritten = IonWriter.writeComplexTypeIdShort(dest, offset, typeId);

        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4) | 3, 255 & dest[offset++]);
        assertEquals( 1, 255 & dest[offset++]);
        assertEquals( 2, 255 & dest[offset++]);
        assertEquals( 3, 255 & dest[offset++]);

    }


        @Test
    public void testKey() throws UnsupportedEncodingException {
        byte[] dest = new byte[10 * 1024];

        String value  = "Hello";

        int offset = 10;
        int bytesWritten = IonWriter.writeKey(dest, offset, value);

        assertEquals(7, bytesWritten);
        assertEquals((IonFieldTypes.KEY<<4) | 1, 255 & dest[offset++]);
        assertEquals(5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeKey(dest, offset, (String) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY << 4, 255 & dest[offset++]);

        byte[] valueBytes = "Hello".getBytes("UTF-8");

        offset = 30;
        bytesWritten = IonWriter.writeKey(dest, offset, valueBytes);

        assertEquals(7, bytesWritten);
        assertEquals((IonFieldTypes.KEY<<4) | 1, 255 & dest[offset++]);
        assertEquals(5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 40;
        bytesWritten = IonWriter.writeKey(dest, offset, (byte[]) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY << 4, 255 & dest[offset++]);

    }


    @Test
    public void testKeyCompact() throws UnsupportedEncodingException {
        byte[] dest = new byte[10 * 1024];

        String value  = "Hello";

        int offset = 10;
        int bytesWritten = IonWriter.writeKeyShort(dest, offset, value);

        assertEquals(6, bytesWritten);
        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeKeyShort(dest, offset, (String) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY_SHORT << 4, 255 & dest[offset++]);

        byte[] valueBytes = "Hello".getBytes("UTF-8");

        offset = 30;
        bytesWritten = IonWriter.writeKeyShort(dest, offset, valueBytes);

        assertEquals(6, bytesWritten);
        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 40;
        bytesWritten = IonWriter.writeKeyShort(dest, offset, (byte[]) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY_SHORT << 4, 255 & dest[offset++]);
    }


    @Test
    public void testObject() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeObjectBegin(dest, offset, 3);

        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.OBJECT<<4) | 3, 255 & dest[offset++]);

        offset = 10;
        IonWriter.writeObjectEnd(dest, offset, 3, 65535);
        assertEquals((IonFieldTypes.OBJECT<<4) | 3, 255 & dest[offset++]);
        assertEquals(0, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
    }


    @Test
    public void testTable() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeTableBegin(dest, offset, 3);

        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.TABLE<<4) | 3, 255 & dest[offset++]);

        offset = 10;
        IonWriter.writeTableEnd(dest, offset, 3, 65535);
        assertEquals((IonFieldTypes.TABLE<<4) | 3, 255 & dest[offset++]);
        assertEquals(0, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
    }


    @Test
    public void testArray() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeArrayBegin(dest, offset, 3);

        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.ARRAY<<4) | 3, 255 & dest[offset++]);

        offset = 10;
        IonWriter.writeArrayEnd(dest, offset, 3, 65535);
        assertEquals((IonFieldTypes.ARRAY<<4) | 3, 255 & dest[offset++]);
        assertEquals(0, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
        assertEquals(255, 255 & dest[offset++]);
    }



}
