package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.types.Key;

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

    /*
    public void writeKeyBytes(Key key, byte[] source, int offset, int length){
        writeKey(key.source, key.offset, key.length);
        writeBytes(source, offset, length);
    }

    public void writeKeyBoolean(Key key, boolean value){
        writeKey(key.source, key.offset, key.length);
        writeBoolean(value);
    }

    public void writeKeyBoooleanObj(Key key, Boolean value){
        writeKey(key.source, key.offset, key.length);

    }

    public void writeKeyInt64(Key key, long value){

    }

    public void writeKeyInt64Obj(Key key, Long value){

    }

    public void writeKeyFloat32(Key key, float value){

    }

    public void writeKeyFloat32Obj(Key key, Float value){

    }

    public void writeKeyFloat64(Key key, double value){

    }

    public void writePKeyFloat64Obj(Key key, Double value){

    }

    public void writeKeyUtf8(Key key, String value){

    }

    public void writeKeyUtf8(Key key, byte[] value){

    }

    public void writeKeyUtf8(Key key, byte[] value, int offset, int length){

    }
    */




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

    public void writeBytes(byte[] source, int sourceOffset, int sourceLength) {
        if(source == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.BYTES << 4)); //lengthLength = 0 means null value
            return ;
        }

        int lengthLength = IonUtil.lengthOfInt64Value(sourceLength);

        this.dest[destIndex++] = (byte) (255 & ((IonFieldTypes.BYTES << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destIndex++] = (byte) (255 & (sourceLength >> i));
        }

        System.arraycopy(source, sourceOffset, dest, destIndex, sourceLength);
        this.destIndex += sourceLength;
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

    public void writeUtf8(byte[] source, int sourceOffset, int sourceLength){
        if(source == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return ;
        }

        if(sourceLength <=15){
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | sourceLength));
            System.arraycopy(source, sourceOffset, this.dest, this.destIndex, sourceLength);
            this.destIndex += sourceLength;
        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(sourceLength);
            this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                this.dest[this.destIndex++] = (byte) (255 & (sourceLength >> i));
            }

            System.arraycopy(source, sourceOffset, this.dest, this.destIndex, sourceLength);
            this.destIndex += sourceLength;
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
        this.destIndex += lengthLength;
    }

    public void writeObjectEnd(int objectStartIndex, int lengthLength, int length){
        objectStartIndex++;  //jump over the lead byte of the ION Object field

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[objectStartIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeTableBegin(int lengthLength){
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.TABLE << 4) | lengthLength));
        this.destIndex += lengthLength;
    }

    public void writeTableEnd(int objectStartIndex, int lengthLength, int length){
        objectStartIndex++;  //jump over the lead byte of the ION Object field

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[objectStartIndex++] = (byte) (255 & (length >> i));
        }
    }

    public void writeArrayBegin(int lengthLength){
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4) | lengthLength));
        this.destIndex += lengthLength;
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

    public void writeKey(byte[] source, int offset, int length){
        if(source == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return ;
        }

        int lengthLength   = IonUtil.lengthOfInt64Value(length);
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (length >> i));
        }

        System.arraycopy(source, offset, this.dest, this.destIndex, length);
        this.destIndex += length;
    }

    public void writeKey(Key key){
        if(key.source == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY << 4));
            return ;
        }

        int lengthLength   = IonUtil.lengthOfInt64Value(key.length);
        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (key.length >> i));
        }

        System.arraycopy(key.source, key.offset, this.dest, this.destIndex, key.length);
        this.destIndex += key.length;

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

    public void writeKeyShort(byte[] value, int offset, int length){
        if(value == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return ;
        }

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | length));

        System.arraycopy(value, offset, this.dest, this.destIndex, length);
        this.destIndex += length;
    }

    public void writeKeyShort(Key key){
        if(key.source == null){
            this.dest[this.destIndex++] = (byte) (255 & (IonFieldTypes.KEY_SHORT << 4));
            return ;
        }

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | key.length));

        System.arraycopy(key.source, key.offset, this.dest, this.destIndex, key.length);
        this.destIndex += key.length;
    }

    public void writeKeyOrKeyShort(byte[] value, int offset, int length){
        if(length < 16){
            writeKeyShort(value, offset, length);
        } else {
            writeKey(value, offset, length);
        }

    }

    public void writeKeyOrKeyShort(Key key){
        if(key.length < 16){
            writeKeyShort(key);
        } else {
            writeKey(key);
        }

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

    public void writeComplexTypeId(byte[] complexTypeId){
        int lengthLength = IonUtil.lengthOfInt64Value(complexTypeId.length);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        this.dest[this.destIndex++] = IonFieldTypes.COMPLEX_TYPE_ID; //extended type id follows after lead byte.

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (complexTypeId.length >> i));
        }

        for(int i=0; i<complexTypeId.length; i++){
            this.dest[this.destIndex++] = complexTypeId[i];
        }

    }

    public void writeComplexTypeVersion(byte[] complexTypeVersion){
        int lengthLength = IonUtil.lengthOfInt64Value(complexTypeVersion.length);

        this.dest[this.destIndex++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        this.dest[this.destIndex++] = IonFieldTypes.COMPLEX_TYPE_VERSION; //extended type id follows after lead byte.

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            this.dest[this.destIndex++] = (byte) (255 & (complexTypeVersion.length >> i));
        }

        for(int i=0; i<complexTypeVersion.length; i++){
            this.dest[this.destIndex++] = complexTypeVersion[i];
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

    public static int writeBytes(byte[] dest, int destOffset, byte[] source, int sourceOffset, int sourceLength){

        if(source == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.BYTES << 4)); //lengthLength = 0 means null value
            return 1;
        }

        int lengthLength = IonUtil.lengthOfInt64Value(sourceLength);

        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.BYTES << 4) | lengthLength));

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (sourceLength >> i));
        }

        System.arraycopy(source, sourceOffset, dest, destOffset, sourceLength);

        return 1 + lengthLength + sourceLength;
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

    public static int writeUtf8(byte[] dest, int destOffset, byte[] source, int sourceOffset, int sourceLength){
        if(source == null){
            dest[destOffset++] = (byte) (255 & (IonFieldTypes.UTF_8 << 4));
            return 1;
        }

        if(sourceLength <= 15 ){
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8_SHORT << 4) | sourceLength));
            System.arraycopy(source, sourceOffset, dest, destOffset, sourceLength);
            return 1 + sourceLength;
        } else {
            int lengthLength   = IonUtil.lengthOfInt64Value(sourceLength);
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.UTF_8 << 4) | lengthLength));

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (sourceLength >> i));
            }

            System.arraycopy(source, sourceOffset, dest, destOffset, sourceLength);

            return 1 + lengthLength + sourceLength;
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


    public static int writeComplexTypeId(byte[] dest, int destOffset, byte[] value) {
        int lengthLength = IonUtil.lengthOfInt64Value(value.length);
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        dest[destOffset++] = (byte) IonFieldTypes.COMPLEX_TYPE_ID;

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (value.length >> i));
        }

        for(int i=0; i<value.length; i++){
            dest[destOffset++] = value[i];
        }

        return 2 + lengthLength + value.length;
    }


    public static int writeComplexTypeVersion(byte[] dest, int destOffset, byte[] value) {
        int lengthLength = IonUtil.lengthOfInt64Value(value.length);
        dest[destOffset++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | lengthLength));
        dest[destOffset++] = (byte) IonFieldTypes.COMPLEX_TYPE_VERSION;

        for(int i=(lengthLength-1)*8; i >= 0; i-=8){
            dest[destOffset++] = (byte) (255 & (value.length >> i));
        }

        for(int i=0; i<value.length; i++){
            dest[destOffset++] = value[i];
        }

        return 2 + lengthLength + value.length;
    }



}
