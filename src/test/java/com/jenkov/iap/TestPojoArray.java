package com.jenkov.iap;

/**
 * Created by jjenkov on 10-11-2015.
 */
public class TestPojoArray {

    public static class TestObject{
        public boolean field0   = true;
        public short   field1   = 1;
        public int     field2   = 12;
        public long    field3   = 123;
        public float   field4   = 1234.12F;
        public TestObject() {}
    }

    public TestObject[] testObjects = new TestObject[0];

}
