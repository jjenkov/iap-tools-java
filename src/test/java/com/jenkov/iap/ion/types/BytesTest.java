package com.jenkov.iap.ion.types;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jjenkov on 18-05-2016.
 */
public class BytesTest {


    @Test
    public void test() {
        byte[] byteArray1 = new byte[1024];
        Bytes bytes1 = new Bytes(byteArray1, 0, byteArray1.length);


        byte[] byteArray2 = new byte[1024];
        Bytes bytes2 = new Bytes(byteArray2, 0, byteArray2.length);


        assertTrue(bytes1.equals(bytes2.source, bytes2.offset, bytes2.length));

        byteArray2[0] = 1;

        assertFalse(bytes1.equals(bytes2.source, bytes2.offset, bytes2.length));

        byteArray1[0] = 1;

        assertTrue(bytes1.equals(bytes2.source, bytes2.offset, bytes2.length));
    }
}
