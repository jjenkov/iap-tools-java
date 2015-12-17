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



}
