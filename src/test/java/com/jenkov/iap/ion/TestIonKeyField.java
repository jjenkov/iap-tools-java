package com.jenkov.iap.ion;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 20-02-2016.
 */
public class TestIonKeyField {

    @Test
    public void testShortKey() {
        IonKeyField keyField = new IonKeyField("abcde");

        assertEquals(6, keyField.bytes.length);

        int index = 0;
        assertEquals((IonFieldTypes.KEY_SHORT | 5), 255 & keyField.bytes[index++]);

        assertEquals('a', 255 & keyField.bytes[index++]);
        assertEquals('b', 255 & keyField.bytes[index++]);
        assertEquals('c', 255 & keyField.bytes[index++]);
        assertEquals('d', 255 & keyField.bytes[index++]);
        assertEquals('e', 255 & keyField.bytes[index++]);
    }


    @Test
    public void testKey() {
        IonKeyField keyField = new IonKeyField("0123456789abcdef");

        int index = 0;
        assertEquals((IonFieldTypes.KEY | 1), 255 & keyField.bytes[index++]);
        assertEquals(16, 255 & keyField.bytes[index++]);

        assertEquals('0', 255 & keyField.bytes[index++]);
        assertEquals('1', 255 & keyField.bytes[index++]);
        assertEquals('2', 255 & keyField.bytes[index++]);
        assertEquals('3', 255 & keyField.bytes[index++]);
        assertEquals('4', 255 & keyField.bytes[index++]);
        assertEquals('5', 255 & keyField.bytes[index++]);
        assertEquals('6', 255 & keyField.bytes[index++]);
        assertEquals('7', 255 & keyField.bytes[index++]);
        assertEquals('8', 255 & keyField.bytes[index++]);
        assertEquals('9', 255 & keyField.bytes[index++]);
        assertEquals('a', 255 & keyField.bytes[index++]);
        assertEquals('b', 255 & keyField.bytes[index++]);
        assertEquals('c', 255 & keyField.bytes[index++]);
        assertEquals('d', 255 & keyField.bytes[index++]);
        assertEquals('e', 255 & keyField.bytes[index++]);
        assertEquals('f', 255 & keyField.bytes[index++]);

    }

}
