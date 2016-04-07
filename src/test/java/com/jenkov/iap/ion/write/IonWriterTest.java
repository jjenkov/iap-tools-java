package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
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

    byte[] dest = new byte[100 *1024];


    @Test
    public void testDirectWriteMethod() throws UnsupportedEncodingException {
        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        writer.writeDirect(new byte[]{1,2,3});
        assertEquals(13, writer.destIndex);
        assertEquals(1, 255 & dest[index++]);
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);
    }

    @Test
    public void testComplexTypeIdWriteMethod() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 * 1024];

        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        writer.writeComplexTypeIdShort(new byte[]{1,2,3});
        assertEquals(14, writer.destIndex);
        assertEquals((IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(1, 255 & dest[index++]);
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);
    }


    @Test
    public void testKeyWriteMethods() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 *1024];

        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        writer.writeKey("hello");
        assertEquals(17, writer.destIndex);
        assertEquals((IonFieldTypes.KEY << 4)| 1, 255 & dest[index++]);
        assertEquals(5, 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);

        writer.writeKey("hello".getBytes("UTF-8"));
        assertEquals(24, writer.destIndex);
        assertEquals((IonFieldTypes.KEY << 4)| 1, 255 & dest[index++]);
        assertEquals(5, 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);

        writer.writeKeyShort("hello");
        assertEquals(30, writer.destIndex);
        assertEquals((IonFieldTypes.KEY_SHORT << 4)| 5, 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);

        writer.writeKeyShort("hello".getBytes("UTF-8"));
        assertEquals(36, writer.destIndex);
        assertEquals((IonFieldTypes.KEY_SHORT << 4)| 5, 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
    }


    @Test
    public void testArrayWriteMethods() {
        byte[] dest = new byte[100 *1024];

        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        int tableStartIndex = writer.destIndex;
        int lengthLength = 2;
        writer.writeArrayBegin(lengthLength);

        assertEquals(13, writer.destIndex);
        assertEquals((IonFieldTypes.ARRAY << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);

        writer.writeArrayEnd(tableStartIndex, lengthLength,123);

        index = tableStartIndex;
        assertEquals((IonFieldTypes.ARRAY << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);
    }


    @Test
    public void testTableWriteMethods() {
        byte[] dest = new byte[100 *1024];

        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        int tableStartIndex = writer.destIndex;
        int lengthLength = 2;
        writer.writeTableBegin(lengthLength);

        assertEquals(13, writer.destIndex);
        assertEquals((IonFieldTypes.TABLE << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);

        writer.writeTableEnd(tableStartIndex, lengthLength,123);

        index = tableStartIndex;
        assertEquals((IonFieldTypes.TABLE << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);
    }


    @Test
    public void testObjectWriteMethods() {
        byte[] dest = new byte[100 *1024];

        int index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, index);

        int objectStartIndex = writer.destIndex;
        int lengthLength = 2;
        writer.writeObjectBegin(lengthLength);

        assertEquals(13, writer.destIndex);
        assertEquals((IonFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);

        writer.writeObjectEnd(objectStartIndex, lengthLength,123);

        index = objectStartIndex;
        assertEquals((IonFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);

    }


    @Test
    public void testWriteBytes() {

        int index = 0;
        int bytesWritten = IonWriter.writeBytes(dest, 0, new byte[]{1,2,3,4,5}, 1,3);
        assertEquals(5, bytesWritten);
        assertEquals((IonFieldTypes.BYTES << 4) | 1, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]); //length byte
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);
        assertEquals(4, 255 & dest[index++]);

        index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 10);
        writer.writeBytes(new byte[]{6,7,8,9,10}, 1,3);

        assertEquals(15, writer.destIndex);
        assertEquals((IonFieldTypes.BYTES << 4) | 1, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]); //length byte
        assertEquals(7, 255 & dest[index++]);
        assertEquals(8, 255 & dest[index++]);
        assertEquals(9, 255 & dest[index++]);
    }


    @Test
    public void testWriteUtf8() {
        int index = 0;
        int bytesWritten = IonWriter.writeUtf8(dest, 0, new byte[]{1,2,3,4,5}, 1,3);
        assertEquals(4, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);
        assertEquals(4, 255 & dest[index++]);

        index = 10;
        bytesWritten = IonWriter.writeUtf8(dest, index, new byte[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r'}, 1,16);
        assertEquals(18, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);
        assertEquals(16, 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('g', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('j', 255 & dest[index++]);
        assertEquals('k', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
        assertEquals('q', 255 & dest[index++]);

        index = 10;
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 10);
        writer.writeUtf8(new byte[]{6,7,8,9,10}, 1,3);

        assertEquals(14, writer.destIndex);
        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 3, 255 & dest[index++]);
        assertEquals(7, 255 & dest[index++]);
        assertEquals(8, 255 & dest[index++]);
        assertEquals(9, 255 & dest[index++]);


        index = 10;
        writer.setDestination(dest, 10);
        writer.writeUtf8(new byte[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r'}, 1,16);

        assertEquals(28, writer.destIndex);
        assertEquals((IonFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);
        assertEquals(16, 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('g', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('j', 255 & dest[index++]);
        assertEquals('k', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
        assertEquals('q', 255 & dest[index++]);


    }


    @Test
    public void testPrimitiveWriteMethods() {
        byte[] dest = new byte[100 *1024];

        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);


        int index = 0;
        writer.writeBytes(new byte[]{1,2,3});
        assertEquals(5, writer.destIndex);
        assertEquals((IonFieldTypes.BYTES << 4) | 1, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);
        assertEquals(1, 255 & dest[index++]);
        assertEquals(2, 255 & dest[index++]);
        assertEquals(3, 255 & dest[index++]);


        writer.writeBytes(null);
        assertEquals(6, writer.destIndex);
        assertEquals((IonFieldTypes.BYTES << 4) | 0, 255 & dest[index++]);

        writer.writeBoolean(true);
        assertEquals(7, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 1, 255 & dest[index++]);

        writer.writeBoolean(false);
        assertEquals(8, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 2, 255 & dest[index++]);

        writer.writeBoolean(false);
        assertEquals(9, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 2, 255 & dest[index++]);

        writer.writeBooleanObj(null);
        assertEquals(10, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 0, 255 & dest[index++]);

        writer.writeBooleanObj(new Boolean(true));
        assertEquals(11, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 1, 255 & dest[index++]);

        writer.writeBooleanObj(new Boolean(false));
        assertEquals(12, writer.destIndex);
        assertEquals((IonFieldTypes.TINY << 4) | 2, 255 & dest[index++]);

        writer.writeInt64(123);
        assertEquals(14, writer.destIndex);
        assertEquals((IonFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);

        writer.writeInt64(123456);
        assertEquals(18, writer.destIndex);
        assertEquals((IonFieldTypes.INT_POS << 4) | 3, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 16, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 8, 255 & dest[index++]);
        assertEquals(255 & 123456     , 255 & dest[index++]);

        writer.writeInt64(-123);
        assertEquals(20, writer.destIndex);
        assertEquals((IonFieldTypes.INT_NEG << 4) | 1, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);

        writer.writeInt64(-123456);
        assertEquals(24, writer.destIndex);
        assertEquals((IonFieldTypes.INT_NEG << 4) | 3, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 16, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 8, 255 & dest[index++]);
        assertEquals(255 & 123456     , 255 & dest[index++]);


        writer.writeInt64Obj(null);
        assertEquals(25, writer.destIndex);
        assertEquals((IonFieldTypes.INT_POS << 4) | 0, 255 & dest[index++]);


        writer.writeInt64Obj(new Long(123));
        assertEquals(27, writer.destIndex);
        assertEquals((IonFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);

        writer.writeInt64Obj(new Long(123456));
        assertEquals(31, writer.destIndex);
        assertEquals((IonFieldTypes.INT_POS << 4) | 3, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 16, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 8, 255 & dest[index++]);
        assertEquals(255 & 123456     , 255 & dest[index++]);

        writer.writeInt64Obj(new Long(-123));
        assertEquals(33, writer.destIndex);
        assertEquals((IonFieldTypes.INT_NEG << 4) | 1, 255 & dest[index++]);
        assertEquals(123, 255 & dest[index++]);

        writer.writeInt64Obj(new Long(-123456));
        assertEquals(37, writer.destIndex);
        assertEquals((IonFieldTypes.INT_NEG << 4) | 3, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 16, 255 & dest[index++]);
        assertEquals(255 & 123456 >> 8, 255 & dest[index++]);
        assertEquals(255 & 123456     , 255 & dest[index++]);


        writer.writeFloat32(123.123f);
        assertEquals(42, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f)      ), 255 & dest[index++]);

        writer.writeFloat32Obj(null);
        assertEquals(43, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 0, 255 & dest[index++]);

        writer.writeFloat32Obj(123.123f);
        assertEquals(48, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(123.123f)      ), 255 & dest[index++]);

        writer.writeFloat64(123.123d);
        assertEquals(57, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 56), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 48), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 40), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 32), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d)      ), 255 & dest[index++]);


        writer.writeFloat64Obj(null);
        assertEquals(58, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 0, 255 & dest[index++]);

        writer.writeFloat64Obj(123.123d);
        assertEquals(67, writer.destIndex);
        assertEquals((IonFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 56), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 48), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 40), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 32), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Double.doubleToLongBits(123.123d)      ), 255 & dest[index++]);


        writer.writeUtf8((String) null);
        assertEquals(68, writer.destIndex);
        assertEquals((IonFieldTypes.UTF_8 << 4) | 0, 255 & dest[index++]);

        writer.writeUtf8("Hello");
        assertEquals(74, writer.destIndex);
        assertEquals((IonFieldTypes.UTF_8_SHORT << 4) | 5, 255 & dest[index++]);
        assertEquals('H', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);

        writer.writeUtf8("01234567890123456789");
        assertEquals(96, writer.destIndex);
        assertEquals((IonFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);
        assertEquals(20, 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);
        assertEquals('4', 255 & dest[index++]);
        assertEquals('5', 255 & dest[index++]);
        assertEquals('6', 255 & dest[index++]);
        assertEquals('7', 255 & dest[index++]);
        assertEquals('8', 255 & dest[index++]);
        assertEquals('9', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);
        assertEquals('4', 255 & dest[index++]);
        assertEquals('5', 255 & dest[index++]);
        assertEquals('6', 255 & dest[index++]);
        assertEquals('7', 255 & dest[index++]);
        assertEquals('8', 255 & dest[index++]);
        assertEquals('9', 255 & dest[index++]);


        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2015);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        writer.writeUtc(calendar, 9);
        assertEquals(106, writer.destIndex);
        assertEquals((IonFieldTypes.UTC_DATE_TIME << 4) | 9, 255 & dest[index++]);
        assertEquals(2015 >>   8, 255 & dest[index++]);
        assertEquals(2015  & 255, 255 & dest[index++]);
        assertEquals(  12, 255 & dest[index++]);
        assertEquals(  31, 255 & dest[index++]);
        assertEquals(  23, 255 & dest[index++]);
        assertEquals(  59, 255 & dest[index++]);
        assertEquals(  59, 255 & dest[index++]);
        assertEquals( 999 >> 8 , 255 & dest[index++]);
        assertEquals( 999 & 255, 255 & dest[index++]);
    }

    @Test
    public void testWriteComplexTypeId() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 * 1024];

        byte[] complexTypeId = "123abc".getBytes("UTF-8");

        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);

        writer.writeComplexTypeId(dest, 0, complexTypeId);

        int index = 0;
        assertEquals( (IonFieldTypes.EXTENDED << 4) | 1, 255 & dest[index++]);
        assertEquals( IonFieldTypes.COMPLEX_TYPE_ID    , 255 & dest[index++]);
        assertEquals( complexTypeId.length             , 255 & dest[index++]);

        assertEquals( '1' , 255 & dest[index++]);
        assertEquals( '2' , 255 & dest[index++]);
        assertEquals( '3' , 255 & dest[index++]);
        assertEquals( 'a' , 255 & dest[index++]);
        assertEquals( 'b' , 255 & dest[index++]);
        assertEquals( 'c' , 255 & dest[index++]);
    }

    @Test
    public void testWriteComplexTypeIdStatic() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 * 1024];

        byte[] complexTypeId = "123abc".getBytes("UTF-8");

        IonWriter.writeComplexTypeId(dest, 0, complexTypeId);

        int index = 0;
        assertEquals( (IonFieldTypes.EXTENDED << 4) | 1, 255 & dest[index++]);
        assertEquals( IonFieldTypes.COMPLEX_TYPE_ID    , 255 & dest[index++]);
        assertEquals( complexTypeId.length             , 255 & dest[index++]);

        assertEquals( '1' , 255 & dest[index++]);
        assertEquals( '2' , 255 & dest[index++]);
        assertEquals( '3' , 255 & dest[index++]);
        assertEquals( 'a' , 255 & dest[index++]);
        assertEquals( 'b' , 255 & dest[index++]);
        assertEquals( 'c' , 255 & dest[index++]);
    }

    @Test
    public void testWriteComplexTypeVersion() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 * 1024];

        byte[] complexTypeId = "01.02.03".getBytes("UTF-8");

        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);

        writer.writeComplexTypeVersion(dest, 0, complexTypeId);

        int index = 0;
        assertEquals( (IonFieldTypes.EXTENDED << 4) | 1   , 255 & dest[index++]);
        assertEquals( IonFieldTypes.COMPLEX_TYPE_VERSION  , 255 & dest[index++]);
        assertEquals( complexTypeId.length                , 255 & dest[index++]);

        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '1' , 255 & dest[index++]);
        assertEquals( '.' , 255 & dest[index++]);
        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '2' , 255 & dest[index++]);
        assertEquals( '.' , 255 & dest[index++]);
        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '3' , 255 & dest[index++]);
    }

    @Test
    public void testWriteComplexTypeVersionStatic() throws UnsupportedEncodingException {
        byte[] dest = new byte[100 * 1024];

        byte[] complexTypeVersion = "01.02.03".getBytes("UTF-8");

        IonWriter.writeComplexTypeVersion(dest, 0, complexTypeVersion);

        int index = 0;
        assertEquals( (IonFieldTypes.EXTENDED << 4) | 1   , 255 & dest[index++]);
        assertEquals( IonFieldTypes.COMPLEX_TYPE_VERSION  , 255 & dest[index++]);
        assertEquals( complexTypeVersion.length                , 255 & dest[index++]);

        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '1' , 255 & dest[index++]);
        assertEquals( '.' , 255 & dest[index++]);
        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '2' , 255 & dest[index++]);
        assertEquals( '.' , 255 & dest[index++]);
        assertEquals( '0' , 255 & dest[index++]);
        assertEquals( '3' , 255 & dest[index++]);
    }


    @Test
    public void testWriteElementCount() {
        byte[] dest = new byte[100 *1024];

        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);


        int elementCount = 1024;
        int index = 0;
        writer.writeElementCount(elementCount);
        assertEquals(4, writer.destIndex);
        assertEquals((IonFieldTypes.EXTENDED << 4) | 2, 255 & dest[index++]);
        assertEquals(IonFieldTypes.ELEMENT_COUNT, 255 & dest[index++]);

        assertEquals(elementCount >> 8, 255 & dest[index++]);
        assertEquals(elementCount & 255, 255 & dest[index++]);
    }


    @Test
    public void testWriteElementCountStatic() {
        byte[] dest = new byte[100 *1024];
        int destOffset = 0;

        int elementCount = 1024;
        int index = 0;

        destOffset += IonWriter.writeElementCount(dest, destOffset, elementCount);
        assertEquals(4, destOffset);
        assertEquals((IonFieldTypes.EXTENDED << 4) | 2, 255 & dest[index++]);
        assertEquals(IonFieldTypes.ELEMENT_COUNT, 255 & dest[index++]);

        assertEquals(elementCount >> 8, 255 & dest[index++]);
        assertEquals(elementCount & 255, 255 & dest[index++]);
    }


    @Test
    public void testStaticWriteBytes() {
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
    public void testStaticWriteBoolean() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeBoolean(dest, offset, false);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.TINY <<4) | 2 , dest[offset]);

        offset = 20;
        bytesWritten = IonWriter.writeBoolean(dest, offset, true);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.TINY <<4) | 1, dest[offset]);
    }


    @Test
    public void testStaticWriteBooleanObj() {
        byte[] dest = new byte[10 * 1024];

        int offset = 10;
        int bytesWritten = IonWriter.writeBooleanObj(dest, offset, false);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.TINY <<4) | 2, dest[offset]);

        offset = 20;
        bytesWritten = IonWriter.writeBooleanObj(dest, offset, true);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.TINY <<4) | 1, dest[offset]);

        offset = 30;
        bytesWritten = IonWriter.writeBooleanObj(dest, offset, null);

        assertEquals(1, bytesWritten);
        assertEquals( (IonFieldTypes.TINY <<4) | 0, dest[offset]);
    }


    @Test
    public void testStaticWriteInt64() {
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
    public void testStaticWriteInt64Obj() {
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
    public void testStaticWriteFloat32() {
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
    public void testStaticWriteFloat32Obj() {
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
    public void testStaticWriteFloat64() {
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
    public void testStaticWriteFloat64Obj() {
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
    public void testStaticWriteUtf8() throws UnsupportedEncodingException {
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


    public void testStaticWriteUtf8Short() throws UnsupportedEncodingException {
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
    public void testStaticWriteUtcTime() {
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
    public void testStaticWriteComplexTypeIdShort()  {
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
    public void testStaticWriteKey() throws UnsupportedEncodingException {
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
    public void testStaticWriteKeyCompact() throws UnsupportedEncodingException {
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
    public void testStaticWriteObject() {
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
    public void testStaticWriteTable() {
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
    public void testStaticWriteArray() {
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
