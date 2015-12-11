package com.jenkov.iap.ion.read;

import com.jenkov.iap.TestPojo;
import com.jenkov.iap.TestPojoArray;
import com.jenkov.iap.ion.pojos.PojoArrayByte;
import com.jenkov.iap.ion.pojos.PojoArrayLong;
import com.jenkov.iap.ion.pojos.PojoWithPojo;
import com.jenkov.iap.ion.write.IonObjectWriter;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IonObjectReaderTest {


    @Test
    public void test() {
        IonObjectWriter writer  = new IonObjectWriter(TestPojo.class);
        IonObjectReader reader  = new IonObjectReader(TestPojo.class);

        byte[] source = new byte[10 * 1024];

        TestPojo sourcePojo = new TestPojo();
        sourcePojo.field0 = false;
        sourcePojo.field1 = 123;
        sourcePojo.field2 = 456.456F;
        sourcePojo.field3 = 456789.456789D;
        sourcePojo.field4 = "abc";
        sourcePojo.field5 = "987654321098765";

        Calendar calendar = sourcePojo.field6;
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        TestPojo destPojo = null;

        int length = writer.writeObject(sourcePojo, 2, source, 0);   //write object first

        //todo fix error in TypedObjectReader related to reading compact keys

        destPojo = (TestPojo) reader.read(source, 0);

        assertEquals(false, destPojo.field0);
        assertEquals(123, destPojo.field1);
        assertEquals(456.456F, destPojo.field2, 0);
        assertEquals("abc", destPojo.field4);
        assertEquals("987654321098765", destPojo.field5);
        assertEquals(calendar, destPojo.field6);

    }

    @Test
    public void testByteArrayLong() {
        IonObjectWriter writer = new IonObjectWriter(PojoArrayLong.class);
        IonObjectReader reader = new IonObjectReader(PojoArrayLong.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayLong pojo = new PojoArrayLong();
        pojo.longs = new long[]{1, 4, 9, -1};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayLong pojo2 = (PojoArrayLong) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.longs) ;
        assertEquals(4, pojo2.longs.length);
        assertEquals(1, pojo2.longs[0]);
        assertEquals(4, pojo2.longs[1]);
        assertEquals(9, pojo2.longs[2]);
        assertEquals(-1, pojo2.longs[3]);

    }

    @Test
    public void testByteArrayField() {
        IonObjectWriter writer = new IonObjectWriter(PojoArrayByte.class);
        IonObjectReader reader = new IonObjectReader(PojoArrayByte.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayByte pojo = new PojoArrayByte();
        pojo.bytes = new byte[]{ 1, 4, 9 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayByte pojo2 = (PojoArrayByte) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.bytes) ;
        assertEquals(3, pojo2.bytes.length);
        assertEquals(1, pojo2.bytes[0]);
        assertEquals(4, pojo2.bytes[1]);
        assertEquals(9, pojo2.bytes[2]);
    }



    @Test
    public void testTableField() {
        IonObjectWriter writer  = new IonObjectWriter(TestPojoArray.class);
        IonObjectReader reader  = new IonObjectReader(TestPojoArray.class);

        byte[] source = new byte[10 * 1024];

        TestPojoArray sourcePojoArray = new TestPojoArray();

        sourcePojoArray.testObjects    = new TestPojoArray.TestObject[3];
        sourcePojoArray.testObjects[0] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[1] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[2] = new TestPojoArray.TestObject();

        TestPojoArray destPojo         = new TestPojoArray();

        int length = writer.writeObject(sourcePojoArray, 2, source, 0);

        destPojo = (TestPojoArray) reader.read(source, 0);

    }


    @Test
    public void testObjectField() {
        IonObjectWriter writer = new IonObjectWriter(PojoWithPojo.class);
        IonObjectReader reader = new IonObjectReader(PojoWithPojo.class);

        byte[] source   = new byte[100 * 1024];

        PojoWithPojo pojo = new PojoWithPojo();
        pojo.field0.field0 = 10;
        pojo.field0.field1 = 11;
        pojo.field0.field2 = 12;
        pojo.field0.field3 = 13;
        pojo.field0.field4 = 14;
        pojo.field0.field5 = 15;
        pojo.field0.field6 = 16;
        pojo.field0.field7 = 17;
        pojo.field0.field8 = 18;
        pojo.field0.field9 = 19;

        int bytesWritten = writer.writeObject(pojo, 2, source, 0);

        System.out.println("bytesWritten = " + bytesWritten);

        PojoWithPojo pojo2 = (PojoWithPojo) reader.read(source, 0);

        assertEquals(10, pojo2.field0.field0);
        assertEquals(11, pojo2.field0.field1);
        assertEquals(12, pojo2.field0.field2);
        assertEquals(13, pojo2.field0.field3);
        assertEquals(14, pojo2.field0.field4);
        assertEquals(15, pojo2.field0.field5);
        assertEquals(16, pojo2.field0.field6);
        assertEquals(17, pojo2.field0.field7);
        assertEquals(18, pojo2.field0.field8);
        assertEquals(19, pojo2.field0.field9);

    }



}
