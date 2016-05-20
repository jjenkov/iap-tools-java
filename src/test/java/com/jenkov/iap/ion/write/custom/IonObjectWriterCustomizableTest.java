package com.jenkov.iap.ion.write.custom;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.TestPojo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 23-11-2015.
 */
public class IonObjectWriterCustomizableTest {


    @Test
    public void test() {
        byte[] dest = new byte[10 * 1024];

        IonObjectWriterCustomizable objectWriter = new IonObjectWriterCustomizable();
        objectWriter.addBooleanFieldWriter("field0"  , (source) -> { return ((TestPojo) source).field0; });
        objectWriter.addInt64FieldWriter  ("field1"  , (source) -> { return ((TestPojo) source).field1; });
        objectWriter.addFloatFieldWriter  ("field2"  , (source) -> { return ((TestPojo) source).field2; });
        objectWriter.addDoubleFieldWriter ("field3"  , (source) -> { return ((TestPojo) source).field3; });

        objectWriter.init();

        TestPojo testPojo = new TestPojo();

        int bytesWritten = objectWriter.writeObject(testPojo, 2, dest, 0);

        assertEquals(49, bytesWritten);

        int index = 0;
        assertEquals((IonFieldTypes.OBJECT<<4) | 2, 255 & dest[index++]);
        assertEquals(  0, 255 & dest[index++]);
        assertEquals( 46, 255 & dest[index++]);
        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((IonFieldTypes.BOOLEAN <<4) | 1, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((IonFieldTypes.INT_POS<<4) | 2, 255 & dest[index++]);
        assertEquals(1234 >> 8, 255 & dest[index++]);
        assertEquals(1234 &255, 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        int floatBits = Float.floatToIntBits(testPojo.field2);
        assertEquals((IonFieldTypes.FLOAT<<4) | 4, 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 24), 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 16), 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 8), 255 & dest[index++]);
        assertEquals(255 & (floatBits)     , 255 & dest[index++]);

        assertEquals((IonFieldTypes.KEY_SHORT <<4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);

        long doubleBits = Double.doubleToLongBits(testPojo.field3);
        assertEquals((IonFieldTypes.FLOAT<<4) | 8, 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 56), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 48), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 40), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 32), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 24), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >> 16), 255 & dest[index++]);
        assertEquals(255 & (doubleBits >>  8), 255 & dest[index++]);
        assertEquals(255 & (doubleBits)      , 255 & dest[index++]);


    }

}
