package com.jenkov.iap.read;

import com.jenkov.iap.TestPojo;
import com.jenkov.iap.TestPojoArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class IapTypedObjectReaderTest {

    /*
    @Test
    public void test() {
        IapTypedObjectWriter writer  = new IapTypedObjectWriter(TestPojo.class);
        IapTypedObjectReader reader  = new IapTypedObjectReader(TestPojo.class);

        byte[] source = new byte[10 * 1024];

        TestPojo sourcePojo = new TestPojo();
        sourcePojo.short1 = 123;

        TestPojo destPojo = null;


        int length = writer.writeObject(sourcePojo, 2, source, 0);   //write object first

        //todo fix error in TypedObjectReader related to reading compact keys

        destPojo = (TestPojo) reader.read(source, 0);

        assertEquals(123, destPojo.short1);


    }


    @Test
    public void testTableField() {
        IapTypedObjectWriter writer  = new IapTypedObjectWriter(TestPojoArray.class);
        IapTypedObjectReader reader  = new IapTypedObjectReader(TestPojoArray.class);

        byte[] source = new byte[10 * 1024];

        TestPojoArray sourcePojoArray = new TestPojoArray();

        sourcePojoArray.testObjects    = new TestPojoArray.TestObject[3];
        sourcePojoArray.testObjects[0] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[1] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[2] = new TestPojoArray.TestObject();

        TestPojoArray destPojo        = new TestPojoArray();

        int length = writer.writeObject(sourcePojoArray, 2, source, 0);

        destPojo = (TestPojoArray) reader.read(source, 0);


    }
    */
}
