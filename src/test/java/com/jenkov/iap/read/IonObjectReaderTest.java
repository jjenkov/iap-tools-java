package com.jenkov.iap.read;

import com.jenkov.iap.TestPojo;
import com.jenkov.iap.TestPojoArray;
import com.jenkov.iap.write.IonObjectWriter;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

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


}
