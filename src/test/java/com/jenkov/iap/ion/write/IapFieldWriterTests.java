package com.jenkov.iap.ion.write;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IapFieldWriterTests {

    /*
    @Test
    public void testBooleanFieldWriter() throws NoSuchFieldException {
        byte[] dest = new byte[10 * 1024];

        Class testPojoClass = TestPojo.class;
        Field boolean1Field1 = testPojoClass.getDeclaredField("boolean1");
        IapFieldWriterBoolean writer1 = new IapFieldWriterBoolean(boolean1Field1);

        TestPojo testPojo = new TestPojo();

        assertEquals(11, writer1.writeKeyAndValueFields(testPojo, dest, 0, 2));
        assertEquals((IapFieldTypes.KEY << 3 ) | 1, 255 & dest[0]);
        assertEquals(  8, 255 & dest[1]);
        assertEquals( 98, 255 & dest[2]);  //value of char 1 field
        assertEquals(111, 255 & dest[3]);  //value of char 2 field
        assertEquals(111, 255 & dest[4]);  //value of char 3 field
        assertEquals(108, 255 & dest[5]);  //value of char 4 field
        assertEquals(101, 255 & dest[6]);  //value of char 5 field
        assertEquals( 97, 255 & dest[7]);  //value of char 6 field
        assertEquals(110, 255 & dest[8]);  //value of char 7 field
        assertEquals( 49, 255 & dest[9]);  //value of char 8 field


        Field boolean1Field2 = testPojoClass.getDeclaredField("boolean2");
        IapFieldWriterBoolean writer2 = new IapFieldWriterBoolean(boolean1Field2);

        assertEquals(11, writer2.writeKeyAndValueFields(testPojo, dest, 11, 2));
        assertEquals((IapFieldTypes.KEY << 3 ) | 1, 255 & dest[11]);
        assertEquals(  8, 255 & dest[12]);
        assertEquals( 98, 255 & dest[13]);  //value of char 1 field
        assertEquals(111, 255 & dest[14]);  //value of char 2 field
        assertEquals(111, 255 & dest[15]);  //value of char 3 field
        assertEquals(108, 255 & dest[16]);  //value of char 4 field
        assertEquals(101, 255 & dest[17]);  //value of char 5 field
        assertEquals( 97, 255 & dest[18]);  //value of char 6 field
        assertEquals(110, 255 & dest[19]);  //value of char 7 field
        assertEquals( 50, 255 & dest[20]);  //value of char 8 field
    }


    @Test
    public void testTableField() throws NoSuchFieldException {
        byte[] dest = new byte[10 * 1024];

        Class testPojoClass = TestPojoArray.class;
        Field arrayField = testPojoClass.getDeclaredField("testObjects");
        IapFieldWriterTable writer = new IapFieldWriterTable(arrayField);

        TestPojoArray testPojo = new TestPojoArray();
        testPojo.testObjects    = new TestPojoArray.TestObject[2];
        testPojo.testObjects[0] = new TestPojoArray.TestObject();
        testPojo.testObjects[1] = new TestPojoArray.TestObject();

        int tableLength = writer.writeKeyAndValueFields(testPojo, dest, 0, 2);

        //table length should be:
        // key field : 1 + 1 + "testObjects" =  13


        int index = 0;
        assertEquals((IapFieldTypes.KEY   << 3) | 1, 255 & dest[index++]);
        assertEquals(11, dest[index++]); //key length
        assertEquals('t', dest[index++]);
        assertEquals('e', dest[index++]);
        assertEquals('s', dest[index++]);
        assertEquals('t', dest[index++]);
        assertEquals('O', dest[index++]);
        assertEquals('b', dest[index++]);
        assertEquals('j', dest[index++]);
        assertEquals('e', dest[index++]);
        assertEquals('c', dest[index++]);
        assertEquals('t', dest[index++]);
        assertEquals('s', dest[index++]);


        // table lead byte + 2 length bytes = 3
        // key field = 1 + 2 (id field) = 3
        // 2 long fields of 1 lead byte + 1 length byte + 1 value byte = 2 x 3 = 6;
        assertEquals((IapFieldTypes.TABLE << 3) | 2, 255 & dest[index++]);
        assertEquals( 0, dest[index++]);
        assertEquals(61, dest[index++]);   // 5 compact keys (3 bytes), 1 boolean, 1 short, 1 int, 1 long and 1 float field

        assertEquals((IapFieldTypes.KEY_SHORT << 3) | 6, 255 & dest[index++]);
        assertEquals('f', dest[index++]);
        assertEquals('i', dest[index++]);
        assertEquals('e', dest[index++]);
        assertEquals('l', dest[index++]);
        assertEquals('d', dest[index++]);
        assertEquals('0', dest[index++]);

        //todo insert tests for the rest of the fields in the table


        /*
        assertEquals((IapFieldTypes.SLONG << 3) | 1, 255 & dest[index++]);
        assertEquals(  1, dest[index++]);
        assertEquals(123, dest[index++]);

        assertEquals((IapFieldTypes.SLONG << 3) | 1, 255 & dest[index++]);
        assertEquals(  1, dest[index++]);
        assertEquals(123, dest[index++]);

        assertEquals(0, dest[index++]); //should be empty - no more written to byte array

        */

        //System.out.println("Done");

    //}


}
