package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.write.IonWriter;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

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

        String value  = "Hello World";

        int offset = 10;
        int bytesWritten = IonWriter.writeUtf8(dest, offset, value);

        assertEquals(13, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8<<4) | 1, 255 & dest[offset++]);
        assertEquals(11, 255 & dest[offset++]);
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

        assertEquals(13, bytesWritten);
        assertEquals((IonFieldTypes.UTF_8<<4) | 1, 255 & dest[offset++]);
        assertEquals(11, 255 & dest[offset++]);
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
        int bytesWritten = IonWriter.writeKeyCompact(dest, offset, value);

        assertEquals(6, bytesWritten);
        assertEquals((IonFieldTypes.KEY_COMPACT<<4) | 5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 20;
        bytesWritten = IonWriter.writeKeyCompact(dest, offset, (String) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY_COMPACT << 4, 255 & dest[offset++]);

        byte[] valueBytes = "Hello".getBytes("UTF-8");

        offset = 30;
        bytesWritten = IonWriter.writeKeyCompact(dest, offset, valueBytes);

        assertEquals(6, bytesWritten);
        assertEquals((IonFieldTypes.KEY_COMPACT<<4) | 5, 255 & dest[offset++]);
        assertEquals('H', 255 & dest[offset++]);
        assertEquals('e', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('l', 255 & dest[offset++]);
        assertEquals('o', 255 & dest[offset++]);

        offset = 40;
        bytesWritten = IonWriter.writeKeyCompact(dest, offset, (byte[]) null);

        assertEquals(1, bytesWritten);
        assertEquals(IonFieldTypes.KEY_COMPACT << 4, 255 & dest[offset++]);
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
