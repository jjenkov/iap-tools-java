package com.jenkov.iap.write;

import com.jenkov.iap.TestPojo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IapTypedObjectWriterTest {

    /*
    IapTypedObjectWriter writer = new IapTypedObjectWriter(TestPojo.class);

    @Test
    public void test() {
        byte[] dest   = new byte[1024];

        TestPojo testPojo = new TestPojo();

        writer.writeObject(testPojo, 2, dest, 0);

        int index = 0;

        assertEquals((IapFieldTypes.OBJECT << 3) | 2, 255 & dest[index++]);  //object field started - 194 = object field type << 3 | 2 (length length)
        assertEquals(  0, 255 & dest[index++]);  //length of object - MSB
        assertEquals( 97, 255 & dest[index++]);  //length of object - LSB

        assertEquals((IapFieldTypes.KEY << 3) | 1, 255 & dest[index++]);   //lead byte of key field
        assertEquals(  8, 255 & dest[index++]);   //length of key field
        assertEquals( 98, 255 & dest[index++]);   //value of char 1 field
        assertEquals(111, 255 & dest[index++]);   //value of char 2 field
        assertEquals(111, 255 & dest[index++]);   //value of char 3 field
        assertEquals(108, 255 & dest[index++]);   //value of char 4 field
        assertEquals(101, 255 & dest[index++]);   //value of char 5 field
        assertEquals( 97, 255 & dest[index++]);  //value of char 6 field
        assertEquals(110, 255 & dest[index++]);  //value of char 7 field
        assertEquals( 49, 255 & dest[index++]);  //value of char 8 field

        assertEquals((IapFieldTypes.BOOLEAN << 3) | 1, 255 & dest[index++]);  //lead byte of boolean field

        assertEquals((IapFieldTypes.KEY << 3) | 1, 255 & dest[index++]);   //lead byte of key field
        assertEquals(  8, 255 & dest[index++]);   //length of key field
        assertEquals( 98, 255 & dest[index++]);   //value of char 1 field
        assertEquals(111, 255 & dest[index++]);   //value of char 2 field
        assertEquals(111, 255 & dest[index++]);   //value of char 3 field
        assertEquals(108, 255 & dest[index++]);   //value of char 4 field
        assertEquals(101, 255 & dest[index++]);   //value of char 5 field
        assertEquals( 97, 255 & dest[index++]);  //value of char 6 field
        assertEquals(110, 255 & dest[index++]);  //value of char 7 field
        assertEquals( 50, 255 & dest[index++]);  //value of char 8 field

        assertEquals((IapFieldTypes.BOOLEAN << 3) | 2, 255 & dest[index++]);  //lead byte of boolean field

        assertEquals((IapFieldTypes.KEY_COMPACT << 3) | 6, 255 & dest[index++]);  //lead byte of compact key field
        assertEquals(115, 255 & dest[index++]);  //value of char 1 field
        assertEquals(104, 255 & dest[index++]);  //value of char 2 field
        assertEquals(111, 255 & dest[index++]);  //value of char 3 field
        assertEquals(114, 255 & dest[index++]);  //value of char 4 field
        assertEquals(116, 255 & dest[index++]);  //value of char 5 field
        assertEquals( 49, 255 & dest[index++]);  //value of char 6 field

        assertEquals((IapFieldTypes.SSHORT << 3) | 1, 255 & dest[index++]);  //lead byte of sshort field
        assertEquals( 12, 255 & dest[index++]);  //value of char 1 field

        assertEquals((IapFieldTypes.KEY_COMPACT << 3) | 4, 255 & dest[index++]);  //value of char 2 field
        assertEquals(105, 255 & dest[index++]);  //value of char 3 field
        assertEquals(110, 255 & dest[index++]);  //value of char 4 field
        assertEquals(116, 255 & dest[index++]);  //value of char 5 field
        assertEquals( 49, 255 & dest[index++]);  //value of char 5 field

        assertEquals((IapFieldTypes.SINT << 3) | 1, 255 & dest[index++]);  //lead byte of sint field
        assertEquals( 13, 255 & dest[index++]);  //value of byte 1

        assertEquals((IapFieldTypes.KEY_COMPACT << 3) | 5, 255 & dest[index++]);  //value of char 2 field
        assertEquals(108, 255 & dest[index++]);  //value of char 3 field
        assertEquals(111, 255 & dest[index++]);  //value of char 4 field
        assertEquals(110, 255 & dest[index++]);  //value of char 5 field
        assertEquals(103, 255 & dest[index++]);  //value of char 5 field
        assertEquals( 49, 255 & dest[index++]);  //value of char 5 field

        assertEquals((IapFieldTypes.SLONG << 3) | 1, 255 & dest[index++]);  //lead byte of long field
        assertEquals(  1, 255 & dest[index++]);  //length of long field
        assertEquals( 14, 255 & dest[index++]);  //value of long field


    }
    */
}
