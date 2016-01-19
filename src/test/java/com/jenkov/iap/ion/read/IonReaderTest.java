package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.write.IonWriter;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;


/**
    This class tests the IapNavigator, but uses the IapGenerator in the tests, so IapGenerator is also tested implicitly.
 */
public class IonReaderTest {

    IonReader reader = new IonReader();

    @Test
    public void testReadBytes() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeBytes(source, index, new byte[]{1, 2, 3, 4, 5});
        index += IonWriter.writeBytes(source, index, null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.BYTES, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readBytes(dest);
        assertEquals(5, length);
        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(3, dest[2]);
        assertEquals(4, dest[3]);
        assertEquals(5, dest[4]);

        length = reader.readBytes(dest, 0, 3);
        assertEquals(3, length);
        assertEquals(2, dest[0]);
        assertEquals(3, dest[1]);
        assertEquals(4, dest[2]);

        reader.next();
        reader.parse();
        length = reader.readBytes(dest, 0, 3);
        assertEquals(0, length);
    }


    @Test
    public void testReadBoolean() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeBoolean(source, index, true);
        index += IonWriter.writeBoolean(source, index, false);
        index += IonWriter.writeBooleanObj(source, index, null);

        reader.parse();
        assertEquals(IonFieldTypes.TINY, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertTrue(reader.readBoolean());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.TINY, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertFalse(reader.readBoolean());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.TINY, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertNull(reader.readBooleanObj());
    }


    @Test
    public void testReadInt64(){
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeInt64(source, index,  65535);
        index += IonWriter.writeInt64(source, index, -65535);
        index += IonWriter.writeInt64Obj(source, index, null);

        reader.parse();
        assertEquals(65535, reader.readInt64());

        reader.next();
        reader.parse();
        assertEquals(-65535, reader.readInt64());

        reader.next();
        reader.parse();
        assertEquals(0   , reader.readInt64());
        assertEquals(null, reader.readInt64Obj());
    }


    @Test
    public void testReadFloat32() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeFloat32(source, index, 123.45F);
        index += IonWriter.writeFloat32Obj(source, index, null);

        reader.parse();
        assertEquals(123.45F, reader.readFloat32(), 0);

        reader.next();
        reader.parse();
        assertEquals(0, reader.readFloat32(),0);
        assertNull(reader.readFloat32Obj());

    }


    @Test
    public void testReadFloat64() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeFloat64(source, index, 123456.123456D);
        index += IonWriter.writeFloat64Obj(source, index, null);

        reader.parse();
        assertEquals(123456.123456D, reader.readFloat64(), 0);

        reader.next();
        reader.parse();
        assertEquals(0, reader.readFloat64(),0);
        assertNull(reader.readFloat64Obj());

    }

    @Test
    public void testReadUtf8() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeUtf8(source, index, "Hello");
        index += IonWriter.writeUtf8(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readUtf8(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readUtf8(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readUtf8String());

        reader.next();
        reader.parse();
        length = reader.readUtf8(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readUtf8String());
    }


    @Test
    public void testReadUtcCalendar() {
        byte[] source = new byte[10 * 1024];

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        int index = 0;
        int bytesWritten = IonWriter.writeUtc(source, index, calendar, 9);

        reader.setSource(source, 0, bytesWritten);

        reader.parse();
        Calendar calendar2 = reader.readUtcCalendar();

        assertEquals(2014, calendar2.get(Calendar.YEAR)) ;
        assertEquals(11  , calendar2.get(Calendar.MONTH)) ;
        assertEquals(31  , calendar2.get(Calendar.DAY_OF_MONTH)) ;
        assertEquals(23  , calendar2.get(Calendar.HOUR_OF_DAY)) ;
        assertEquals(59  , calendar2.get(Calendar.MINUTE)) ;
        assertEquals(59  , calendar2.get(Calendar.SECOND)) ;
        assertEquals(999  , calendar2.get(Calendar.MILLISECOND)) ;

        assertEquals(TimeZone.getTimeZone("UTC")  , calendar2.getTimeZone()) ;


    }



    @Test
    public void testReadComplexTypeIdShort() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;

        byte[] bytes = new byte[]{1,2,4 };
        index += IonWriter.writeComplexTypeIdShort(source, index, bytes);

        reader.setSource(source, 0, 4);
        reader.parse();

        assertEquals(IonFieldTypes.COMPLEX_TYPE_ID_SHORT, reader.fieldType);
        reader.readComplexTypeIdShort(dest);

        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(4, dest[2]);

        assertEquals(0, dest[3]);
        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);

        reader.readComplexTypeIdShort(dest, 3, 3);

        assertEquals(1, dest[3]);
        assertEquals(2, dest[4]);
        assertEquals(4, dest[5]);

    }

        @Test
    public void testReadKey() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeKey(source, index, "Hello");
        index += IonWriter.writeKey(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKey(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKey(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKey(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readKeyAsUtf8String());

    }


    @Test
    public void testReadKeyCompact() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeKeyShort(source, index, "Hello");
        index += IonWriter.writeKeyShort(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.KEY_SHORT, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKeyShort(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKeyShort(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyShortAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKeyShort(dest, 0, 3);
        assertEquals(0, length);
        assertNull(reader.readKeyShortAsUtf8String());

    }

    @Test
    public void testReadElementCount() {
        byte[] dest = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeElementCount(dest, index, 1024);
        index += IonWriter.writeElementCount(dest, index, 2048);

        assertEquals(8, index);

        reader.setSource(dest, 0, dest.length);
        reader.next();
        reader.parse();

        assertEquals(IonFieldTypes.EXTENDED     , reader.fieldType);
        assertEquals(IonFieldTypes.ELEMENT_COUNT, reader.fieldTypeExtended);
        assertEquals(2, reader.fieldLengthLength);
        assertEquals(2, reader.fieldLength);

        int offset = 2;
        assertEquals(1024 >> 8 , 255 & dest[offset++]);
        assertEquals(1024 & 255, 255 & dest[offset++]);

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.EXTENDED     , reader.fieldType);
        assertEquals(IonFieldTypes.ELEMENT_COUNT, reader.fieldTypeExtended);
        assertEquals(2, reader.fieldLengthLength);
        assertEquals(2, reader.fieldLength);

        offset = 6;
        assertEquals(2048 >> 8  , 255 & dest[offset++]);
        assertEquals(2048 & 255, 255 & dest[offset++]);

    }




    @Test
    public void testMoveIntoAndOutOf() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        int object1StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        index += IonWriter.writeKey (source, index, "field1");
        index += IonWriter.writeUtf8(source, index, "value1");
        index += IonWriter.writeKey (source, index, "field2");
        index += IonWriter.writeInt64 (source, index, 1234);
        IonWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        int object2StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        index += IonWriter.writeKey (source, index, "field1");
        index += IonWriter.writeUtf8(source, index, "value1");
        index += IonWriter.writeKey (source, index, "field2");
        index += IonWriter.writeInt64(source, index, 1234);
        IonWriter.writeObjectEnd(source, object2StartIndex, 2, index - object2StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte


        // First check that outer level navigation works - skipping over the fields of the objects.
        reader.setSource(source, 0, index);
        assertTrue(reader.hasNext());

        reader.parse();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertFalse(reader.hasNext());
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);

        reader.next();
        assertFalse(reader.hasNext());
        assertEquals(index, reader.nextIndex);


        // Second check that parsing into the objects also works
        reader.setSource(source, 0, index);
        reader.parse();

        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.moveInto();
        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveOutOf();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertEquals(object1StartIndex + 1 + 2, reader.index); // +1 for lead byte, +2 for lengthLength
        assertEquals(object2StartIndex        , reader.nextIndex);
        assertTrue(reader.hasNext());

        reader.next();
        assertEquals(object2StartIndex        , reader.index);

        reader.parse();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveInto();
        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveOutOf();
        assertFalse(reader.hasNext());

    }


    @Test
    public void testReadObjects() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        int object1StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        index += IonWriter.writeKey (source, index, "name");
        index += IonWriter.writeUtf8(source, index, "John");
        index += IonWriter.writeKey (source, index, "id");
        index += IonWriter.writeInt64 (source, index, 1234);
        IonWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        reader.setSource(source, 0, index);

        Map object = null;
        while(reader.hasNext()){
            reader.next();
            reader.parse();

            if(reader.fieldType == IonFieldTypes.OBJECT){
                reader.moveInto();

                object = parseObject();

                reader.moveOutOf();
            }
        }

        assertNotNull(object);
        assertEquals(2, object.size());
        assertEquals("John", object.get("name")) ;
        assertEquals(new Long(1234)  , object.get("id")) ;



        index = 0;
        object1StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        IonWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        reader.setSource(source, 0, index);

        while(reader.hasNext()){
            reader.next();
            reader.parse();

            if(reader.fieldType == IonFieldTypes.OBJECT){
                reader.moveInto();

                object = parseObject();

                reader.moveOutOf();
            }
        }

        assertNotNull(object);
        assertEquals(0, object.size());

    }

    private Map parseObject() {
        Map object;
        object = new HashMap();
        while(reader.hasNext()){
            reader.next();
            reader.parse();

            String key = null;
            if(reader.fieldType == IonFieldTypes.KEY ||
               reader.fieldType == IonFieldTypes.KEY_SHORT){

                key = reader.readKeyAsUtf8String();
            }

            if(reader.hasNext()){
                reader.next();
                reader.parse();

                if("name".equals(key)){
                    object.put(key, reader.readUtf8String());
                } else if("id".equals(key)){
                    object.put(key, reader.readInt64());
                }
            }
        }
        return object;
    }




}
