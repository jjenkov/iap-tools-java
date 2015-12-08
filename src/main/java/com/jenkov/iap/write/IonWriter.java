package com.jenkov.iap.write;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 08-11-2015.
 */
public class IonWriter {

    /*
    public static final int BOOLEAN       =  1;
    public static final int INT_POS       =  2;
    public static final int INT_NEG       =  3;
    public static final int FLOAT         =  4;
    public static final int UTF_8         =  5;

    public static final int RESERVED_1    =  6;
    public static final int RESERVED_2    =  7;

    public static final int OBJECT        =  8;
    public static final int TABLE         =  9;
    public static final int ARRAY         = 10;

    public static final int COMPLEX_TYPE_ID_SHORT = 11; //the type of an object - reserved for special IAP object types - none so far.

    public static final int KEY           = 12;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names.
    public static final int KEY_SHORT   = 13;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names - 15 bytes or less.

    public static final int REFERENCE     = 14;   //a reference to a field stored in the IAP connection cache.

    public static final int EXTENDED = 15;
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
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BOOLEAN << 4) | 1));
        } else {
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BOOLEAN << 4) | 2));
        }
        return 1;
    }


    public static int writeBooleanObj(byte[] dest, int destOffset, Boolean value){
        if(value == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.BOOLEAN << 4));
            return 1;
        }
        if(value){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BOOLEAN << 4) | 1));
        } else {
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BOOLEAN << 4) | 2));
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



    public static int writeKeyCompact(byte[] dest, int destOffset, String value){
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



    public static int writeKeyCompact(byte[] dest, int destOffset, byte[] value){
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
