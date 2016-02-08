package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * The IonWriter class can write raw ION fields to a byte array. You can either instantiate an IonWriter or use the
 * static write methods. An IonWriter object is a bit simpler to work with, as there are less parameters to the
 * write methods, and you get more help to correctly write the length of complex fields.
 *
 */
public class IonWriter {

	/** Maximum length of keyShort String. */
	public static int KEY_SHORT_MAX = 15; 

    public byte[] dest      = null;
    public int    destIndex = 0;

    public IonWriter() {
    }

    public IonWriter(byte[] dest) {
        this.dest = dest;
    }

    public IonWriter(byte[] dest, int destIndex) {
        this.dest = dest;
        this.destIndex = destIndex;
    }

    public void setDestination(byte[] dest, int offset){
        this.dest      = dest;
        this.destIndex = offset;
    }

    public void setOffset(int offset){
        this.destIndex = offset;
    }

    public void writeBytes(byte[] value) {
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.BYTES << 4)); //lengthLength = 0 means null value
            return ;
        }

        int length = value.length;
        int lengthLength = IonUtil.lengthOfInt64Value(length);

        this.dest[destIndex++] = (byte) (255 & ((IonFieldTypes.BYTES << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destIndex++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(value, 0, dest, destIndex, length);
        this.destIndex += length;
    }

    public void writeBoolean(boolean value){
        if(value){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 1));
        } else {
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 2));
        }
    }

    public void writeBooleanObj(Boolean value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 0));
            return;
        }
        if(value){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 1));
        } else {
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 2));
        }
    }

    public void writeInt64(long value){
        int ionFieldType = IonFieldTypes.INT_POS;
        if(value < 0){
            ionFieldType = IonFieldTypes.INT_NEG;
            value  = -value;
        }

        int length = IonUtil.lengthOfInt64Value(value);

        this.dest[this.destIndex++] = (byte) (255 & ((ionFieldType << 4) | length)); //todo optimize this so the shift left can be pre-calculated by the compiler?

        for(int i=(length-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (value >> i));
        }
    }

    public void writeInt64Obj(Long value){
        if(value == null){
            dest[destIndex++] = (byte) (255 & (IonFieldTypes.INT_POS << 4));
            return ;
        }

        int ionFieldType = IonFieldTypes.INT_POS;
        if(value < 0){
            ionFieldType = IonFieldTypes.INT_NEG;
            value  = -value;
        }

        int length = IonUtil.lengthOfInt64Value(value);

        dest[destIndex++] = (byte) (255 & ((ionFieldType << 4) | length));

        for(int i=(length-1)*8; i >= 0; i-=8){
            dest[destIndex++] = (byte) (255 & (value >> i));
        }
    }

    public void writeFloat32(float value){
        int intBits = Float.floatToIntBits(value);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

        for(int i=(4-1)*8; i >= 0; i-=8){
            dest[this.destIndex++] = (byte) (255 & (intBits >> i));
        }
    }

    public void writeFloat32Obj(Float value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.FLOAT << 4));
            return ;
        }

        int intBits = Float.floatToIntBits(value);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

        for(int i=(4-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (intBits >> i));
        }

    }

    public void writeFloat64(double value){
        long longBits = Double.doubleToLongBits(value);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 8));

        for(int i=(8-1)*8; i >= 0; i-=8){
            dest[this.destIndex++] = (byte) (255 & (longBits >> i));
        }
    }

    public void writeFloat64Obj(Double value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.FLOAT << 4));
            return ;
        }

        long longBits = Double.doubleToLongBits(value);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 8));

        for(int i=(8-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (longBits >> i));
        }
    }

    public void writeUtf8(String value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return ;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;

        if(length <=15){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | length));
            System.arraycopy(utf8Bytes, 0, this.dest, this.destIndex, length);
            this.destIndex += length;
        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(length);
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                this.dest[this.destIndex++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(utf8Bytes, 0, this.dest, this.destIndex, length);
            this.destIndex += length;
        }

    }

    public void writeUtf8(byte[] value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return ;
        }

        int length         = value.length;

        if(length <=15){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | length));
            System.arraycopy(value, 0, this.dest, this.destIndex, length);
            this.destIndex += length;
        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(length);
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                this.dest[this.destIndex++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(value, 0, this.dest, this.destIndex, length);
            this.destIndex += length;
        }

    }

    public void writeUtc(Calendar dateTime, int length){
        if(dateTime == null){
            dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.UTC_DATE_TIME << 4));
            return ;
        }
        dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTC_DATE_TIME << 4) | length));

        int year = dateTime.get(Calendar.YEAR);
        dest[this.destIndex++] = (byte) (255 & (year >>   8));
        dest[this.destIndex++] = (byte) (255 & (year &  255));

        if(length == 2) { return; }  // 1 + length (2)

        dest[this.destIndex++] = (byte) (255 & (dateTime.get(Calendar.MONTH) + 1));

        if(length == 3) { return ;}  // 1 + length (3)

        dest[this.destIndex++] = (byte) (255 & (dateTime.get(Calendar.DAY_OF_MONTH)));

        if(length == 4) { return ;}  // 1 + length (4)

        dest[this.destIndex++] = (byte) (255 & (dateTime.get(Calendar.HOUR_OF_DAY)));

        if(length == 5) { return ;}  // 1 + length (5)

        dest[this.destIndex++] = (byte) (255 & (dateTime.get(Calendar.MINUTE)));

        if(length == 6) { return ;}  // 1 + length (6)

        dest[this.destIndex++] = (byte) (255 & (dateTime.get(Calendar.SECOND)));

        if(length == 7) { return ;}  // 1 + length (7)

        int millis =  dateTime.get(Calendar.MILLISECOND);
        dest[this.destIndex++] = (byte) (255 & (millis >>  8));
        dest[this.destIndex++] = (byte) (255 & (millis));

        return;

    }

    public void writeObjectBegin(int lengthLength){
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.OBJECT << 4) | lengthLength));
    }

    public void writeObjectEnd(int objectStartIndex, int lengthLength, int length){
        objectStartIndex++;  //jump over the lead byte of the ION Object field

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[objectStartIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeTableBegin(int lengthLength){
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TABLE << 4) | lengthLength));
    }

    public void writeTableEnd(int objectStartIndex, int lengthLength, int length){
        objectStartIndex++;  //jump over the lead byte of the ION Object field

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[objectStartIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeArrayBegin(int lengthLength){
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4) | lengthLength));
    }

    public void writeArrayEnd(int objectStartIndex, int lengthLength, int length){
        objectStartIndex++;  //jump over the lead byte of the ION Object field

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[objectStartIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeKey(String value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return ;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;
        int lengthLength   = IonUtil.lengthOfInt64Value(length);
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[destIndex++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(utf8Bytes, 0, dest, this.destIndex, length);
        this.destIndex += length;
    }

    public void writeKey(byte[] value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return ;
        }

        int length         = value.length;
        int lengthLength   = IonUtil.lengthOfInt64Value(length);
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(value, 0, this.dest, this.destIndex, length);
        this.destIndex += length;

    }

    public void writeKeyShort(String value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return ;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | length));

        System.arraycopy(utf8Bytes, 0, this.dest, this.destIndex, length);
        this.destIndex += length;
    }
    
    public void writeKeyShort(byte[] value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return ;
        }

        int length         = value.length;
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | length));

        System.arraycopy(value, 0, this.dest, this.destIndex, length);
        this.destIndex += length;
    }

    public void writeDirect(byte[] ionFieldBytes){
        System.arraycopy(ionFieldBytes, 0, this.dest, this.destIndex, ionFieldBytes.length );
        this.destIndex += ionFieldBytes.length;
    }

    public void writeComplexTypeIdShort(byte[] value){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4));
            return;
        }

        int length         = value.length;
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4) | length));

        System.arraycopy(value, 0, this.dest, this.destIndex, length);
        this.destIndex += length;
    }


    /*
    Extended field types
    */
    public void writeElementCount(long elementCount){
        int lengthLength = IonUtil.lengthOfInt64Value(elementCount);
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        this.dest[this.destIndex++] = IonFieldTypes.ELEMENT_COUNT; //extended type id follows after lead byte.

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (elementCount >> i));
        }

    }


    /*
     ======================================================
     Static versions follow below, of same methods as above
     ======================================================
     */

    public static int writeBytes(byte[] dest, int destOffset, byte[] value){

        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.BYTES << 4)); //lengthLength = 0 means null value
            return 1;
        }

        int length = value.length;
        int lengthLength = IonUtil.lengthOfInt64Value(length);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BYTES << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(value, 0, dest, destOffset, length);

        return 1 + lengthLength + length;
    }

    public static int writeBoolean(byte[] dest, int destOffset, boolean value){
        if(value){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 1));
        } else {
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 2));
        }
        return 1;
    }

    public static int writeBooleanObj(byte[] dest, int destOffset, Boolean value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.TINY << 4));
            return 1;
        }
        if(value){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 1));
        } else {
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.TINY << 4) | 2));
        }
        return 1;

    }

    public static int writeInt64(byte[] dest, int destOffset, long value){
        int ionFieldType = IonFieldTypes.INT_POS;
        if(value < 0){
            ionFieldType = IonFieldTypes.INT_NEG;
            value  = -value;
        }

        int length = IonUtil.lengthOfInt64Value(value);

        dest[destOffset++] = (byte) (255 & ((ionFieldType << 4) | length)); //todo optimize this so the shift left can be pre-calculated by the compiler?

        for(int i=(length-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (value >> i));
        }

        return 1 + length;
    }

    public static int writeInt64Obj(byte[] dest, int destOffset, Long value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.INT_POS << 4));
            return 1;
        }

        int ionFieldType = IonFieldTypes.INT_POS;
        if(value < 0){
            ionFieldType = IonFieldTypes.INT_NEG;
            value  = -value;
        }

        int length = IonUtil.lengthOfInt64Value(value);

        dest[destOffset++] = (byte) (255 & ((ionFieldType << 4) | length));

        for(int i=(length-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (value >> i));
        }

        return 1 + length;
    }

    public static int writeFloat32(byte[] dest, int destOffset, float value){
        int intBits = Float.floatToIntBits(value);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

        for(int i=(4-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (intBits >> i));
        }

        return 5;
    }

    public static int writeFloat32Obj(byte[] dest, int destOffset, Float value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.FLOAT << 4));
            return 1;
        }

        int intBits = Float.floatToIntBits(value);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

        for(int i=(4-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (intBits >> i));
        }

        return 5;
    }

    public static int writeFloat64(byte[] dest, int destOffset, double value){
        long longBits = Double.doubleToLongBits(value);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 8));

        for(int i=(8-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (longBits >> i));
        }

        return 9;
    }

    public static int writeFloat64Obj(byte[] dest, int destOffset, Double value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.FLOAT << 4));
            return 1;
        }

        long longBits = Double.doubleToLongBits(value);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 8));

        for(int i=(8-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (longBits >> i));
        }

        return 9;
    }

    public static int writeUtf8(byte[] dest, int destOffset, String value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return 1;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;

        if(length <=15){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | length));
            System.arraycopy(utf8Bytes, 0, dest, destOffset, length);

            return 1 + length;

        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(length);
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(utf8Bytes, 0, dest, destOffset, length);

            return 1 + lengthLength + length;
        }
    }

    public static int writeUtf8(byte[] dest, int destOffset, byte[] value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return 1;
        }

        int length         = value.length;

        if(length <= 15 ){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | length));
            System.arraycopy(value, 0, dest, destOffset, length);
            return 1 + length;
        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(length);
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(value, 0, dest, destOffset, length);

            return 1 + lengthLength + length;
        }
    }

    public static int writeUtc(byte[] dest, int destOffset, Calendar dateTime, int length) {
        if(dateTime == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.UTC_DATE_TIME << 4));
            return 1;
        }
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTC_DATE_TIME << 4) | length));

        int year = dateTime.get(Calendar.YEAR);
        dest[destOffset++] = (byte) (255 & (year >>   8));
        dest[destOffset++] = (byte) (255 & (year &  255));

        if(length == 2) { return 3;}  // 1 + length (2)

        dest[destOffset++] = (byte) (255 & (dateTime.get(Calendar.MONTH) + 1));

        if(length == 3) { return 4;}  // 1 + length (3)

        dest[destOffset++] = (byte) (255 & (dateTime.get(Calendar.DAY_OF_MONTH)));

        if(length == 4) { return 5;}  // 1 + length (4)

        dest[destOffset++] = (byte) (255 & (dateTime.get(Calendar.HOUR_OF_DAY)));

        if(length == 5) { return 6;}  // 1 + length (5)

        dest[destOffset++] = (byte) (255 & (dateTime.get(Calendar.MINUTE)));

        if(length == 6) { return 7;}  // 1 + length (6)

        dest[destOffset++] = (byte) (255 & (dateTime.get(Calendar.SECOND)));

        if(length == 7) { return 8;}  // 1 + length (7)

        int millis =  dateTime.get(Calendar.MILLISECOND);
        dest[destOffset++] = (byte) (255 & (millis >>  8));
        dest[destOffset++] = (byte) (255 & (millis));

        return 10;  // 1 + length (9)
    }

    public static int writeComplexTypeIdShort(byte[] dest, int destOffset, byte[] value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4));
            return 1;
        }

        int length         = value.length;
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.COMPLEX_TYPE_ID_SHORT << 4) | length));

        System.arraycopy(value, 0, dest, destOffset, length);

        return 1 + length;
    }

    public static int writeKey(byte[] dest, int destOffset, String value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return 1;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;
        int lengthLength   = IonUtil.lengthOfInt64Value(length);
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(utf8Bytes, 0, dest, destOffset, length);

        return 1 + lengthLength + length;
    }

    public static int writeKey(byte[] dest, int destOffset, byte[] value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return 1;
        }

        int length         = value.length;
        int lengthLength   = IonUtil.lengthOfInt64Value(length);
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(value, 0, dest, destOffset, length);

        return 1 + lengthLength + length;
    }

    public static int writeKeyShort(byte[] dest, int destOffset, String value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return 1;
        }

        byte[] utf8Bytes = null;
        try {
            //todo Faster way to encode UTF-8 from String?
            utf8Bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //should never happen - UTF-8 is always supported.
        }

        int length         = utf8Bytes.length;
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | length));

        System.arraycopy(utf8Bytes, 0, dest, destOffset, length);

        return 1 + length;
    }

    public static int writeKeyShort(byte[] dest, int destOffset, byte[] value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return 1;
        }

        int length         = value.length;
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | length));

        System.arraycopy(value, 0, dest, destOffset, length);

        return 1 + length;
    }

    public static int writeObjectBegin(byte[] dest, int destOffset, int lengthLength){
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.OBJECT << 4) | lengthLength));

        return 1 + lengthLength;
    }

    public static void writeObjectEnd(byte[] dest, int destOffset, int lengthLength, int length){
        destOffset++;

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }
    }

    public static int writeTableBegin(byte[] dest, int destOffset, int lengthLength){
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.TABLE << 4) | lengthLength));

        return 1 + lengthLength;
    }

    public static void writeTableEnd(byte[] dest, int destOffset, int lengthLength, int length){
        destOffset++;

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }
    }

    public static int writeArrayBegin(byte[] dest, int destOffset, int lengthLength){
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4) | lengthLength));

        return 1 + lengthLength;
    }

    public static void writeArrayEnd(byte[] dest, int destOffset, int lengthLength, int length){
        destOffset++;

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (length >> i));
        }
    }

    public static int writeDirect(byte[] dest, int destOffset, byte[] ionFieldBytes){
        System.arraycopy(ionFieldBytes, 0, dest, destOffset, ionFieldBytes.length );
        return ionFieldBytes.length;
    }


    /*
        Extended field types
     */
    public static int writeElementCount(byte[] dest, int destOffset, long elementCount){
        int lengthLength = IonUtil.lengthOfInt64Value(elementCount);
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        dest[destOffset++] = IonFieldTypes.ELEMENT_COUNT; //extended type id follows after lead byte.

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (elementCount >> i));
        }

        return 2 + lengthLength; // 1 lead byte, 1 extended type id byte, lengthLength element count bytes
    }

    /** Method for use with tables. Writes proper type based on Object class. */
    public void writeValues( Object [] values){
    	int index = 0;
    	for ( Object value : values ) {
    	    String clazz = value.getClass().getSimpleName();
    	    switch( clazz ) {
    	    	case "String": { writeUtf8( (String) values[ index++ ] ); break;}
    	    	case "Boolean": { writeBooleanObj( (Boolean) values[ index++ ] ); break;}
    	    	case "Float": { writeFloat32Obj( (Float) values[ index++ ] ); break;}
    	    	case "Double": { writeFloat64Obj( (Double) values[ index++ ] ); break;}
    	    	case "Integer": { writeInt64Obj( (Long) values[ index++ ] ); break;}
    	    	case "Long": { writeInt64Obj( (Long) values[ index++ ] ); break;}
    	    	default: throw new IllegalArgumentException( "cannot handle object \"" + value + "\" type at position " + index + ". Do not use primitives with this method." );
    	    }
    	}
    }
    
    /** Method for use with tables. Writes proper type based on Object class. */
    public static int writeValues( byte[] dest, int destOffset, Object [] values){
        if(values == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.INT_POS << 4));
            return 1;
        }
    	int index = 0;
    	int length = 0;
    	for ( Object value : values ) {
    	    String clazz = value.getClass().getSimpleName();
    	    switch( clazz ) {
    	    	case "String": { length += IonWriter.writeUtf8( dest, destOffset + length, (String) values[ index++ ] ); break;}
    	    	case "Boolean": { length += IonWriter.writeBooleanObj( dest, destOffset + length, (Boolean) values[ index++ ] ); break;}
    	    	case "Float": { length += IonWriter.writeFloat32Obj( dest, destOffset + length, (Float) values[ index++ ] ); break;}
    	    	case "Double": { length += IonWriter.writeFloat64Obj( dest, destOffset + length, (Double) values[ index++ ] ); break;}
    	    	case "Integer": { length += IonWriter.writeInt64Obj( dest, destOffset + length, new Long( (Integer) values[ index++ ]) ); break;}
    	    	case "Long": { length += IonWriter.writeInt64Obj( dest, destOffset + length, (Long) values[ index++ ] ); break;}
    	    	default: throw new IllegalArgumentException( "cannot handle object \"" + value + "\" type at position " + index + ". Do not use primitives with this method." );
    	    }
    	}
    	return length;
    }
    
    /** A convenience method for writing multiple keys. */
    public void writeKeys(String [] keys){
    	for ( String key : keys ) {
    		this.writeKey(key);
    	}
    }
    
    /** A convenience method for writing multiple short keys. */
    public void writeKeyShorts(String [] keys){
    	int index = 0;
    	for ( String key : keys ) {
    		if ( key.length() >= KEY_SHORT_MAX) {
    			throw new IllegalArgumentException( "key \"" + key + "\" in position " + index++ + " is longer than maxium length " + KEY_SHORT_MAX );
    		}    		
    		this.writeKeyShort(key);
    	}
    }
    
    /** A convenience method for writing multiple keys. */
    public static int writeKeys(byte [] dest, int destOffset, String [] keys ) {
        if(keys == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return 1;
        }
        int total = 0;
    	for ( String key: keys) {
    		int length = IonWriter.writeKey(dest, destOffset, key);
    		destOffset += length;
    		total += length;
        }
    	return total;
    }

    /** A convenience method for writing multiple short keys. */
    public static int writeKeyShorts(byte [] dest, int destOffset, String [] keys ) {
        if(keys == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return 1;
        }
        int total = 0;
        int index = 0;
    	for ( String key: keys) {
    		if ( key.length() >= KEY_SHORT_MAX) {
    			throw new IllegalArgumentException( "key \"" + key + "\" in position " + index++ + " is longer than maxium length " + KEY_SHORT_MAX );
    		}
    		int length = IonWriter.writeKeyShort(dest, destOffset, key);
    		destOffset += length;
    		total += length;
        }
    	return total;
    }
       
}