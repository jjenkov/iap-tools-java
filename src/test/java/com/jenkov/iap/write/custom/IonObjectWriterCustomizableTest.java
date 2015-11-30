package com.jenkov.iap.write.custom;

import com.jenkov.iap.IonFieldTypes;
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
        objectWriter.addBooleanFieldWriter("boolean1", (source) -> { return ((TestPojo) source).boolean1; });
        objectWriter.addInt64FieldWriter  ("short1"  , (source) -> { return ((TestPojo) source).short1; });
        objectWriter.addFloatFieldWriter  ("float1"  , (source) -> { return ((TestPojo) source).float1; });
        objectWriter.addDoubleFieldWriter ("dbl1"    , (source) -> { return ((TestPojo) source).dbl1;   });

        objectWriter.init();

        TestPojo testPojo = new TestPojo();

        int bytesWritten = objectWriter.writeObject(testPojo, 2, dest, 0);

        assertEquals(48, bytesWritten);

        int index = 0;
        assertEquals((2<<4) | IonFieldTypes.OBJECT, 255 & dest[index++]);
        assertEquals(  0, 255 & dest[index++]);
        assertEquals( 45, 255 & dest[index++]);
        assertEquals((8<<4) | IonFieldTypes.KEY_COMPACT, 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((1<<4) | IonFieldTypes.BOOLEAN, 255 & dest[index++]);

        assertEquals((6<<4) | IonFieldTypes.KEY_COMPACT, 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((1<<4) | IonFieldTypes.INT_POS, 255 & dest[index++]);
        assertEquals(12, 255 & dest[index++]);

        assertEquals((6<<4) | IonFieldTypes.KEY_COMPACT, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        int floatBits = Float.floatToIntBits(testPojo.float1);
        assertEquals((4<<4) | IonFieldTypes.FLOAT, 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 24), 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 16), 255 & dest[index++]);
        assertEquals(255 & (floatBits >> 8), 255 & dest[index++]);
        assertEquals(255 & (floatBits)     , 255 & dest[index++]);

        assertEquals((4<<4) | IonFieldTypes.KEY_COMPACT, 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        long doubleBits = Double.doubleToLongBits(testPojo.dbl1);
        assertEquals((8<<4) | IonFieldTypes.FLOAT, 255 & dest[index++]);
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
