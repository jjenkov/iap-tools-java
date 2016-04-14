package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by jjenkov on 08-11-2015.
 */
public class IonReader {

    private static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public byte[] source = null;
    public int sourceLength = 0;

    public int index = 0;
    public int nextIndex = 0;
    public int scopeEndIndex = 0;

    public int fieldType   = 0;
    public int fieldLengthLength = 0;
    public int fieldLength = 0;
    public int fieldTypeExtended = 0;

    private long[] intoIndexStack = null;
    private int   intoIndexStackIndex = 0;


    public IonReader() {
        this.intoIndexStack = new long[64];
    }

    public IonReader(byte[] source) {
        this.intoIndexStack = new long[64];
        setSource(source, 0, source.length);
    }

    public IonReader(byte[] source, int offset, int length) {
        this.intoIndexStack = new long[64];
        setSource(source, offset, length);
    }

    public IonReader setSource(byte[] source, int sourceOffset, int length){
        this.source = source;
        this.index  = sourceOffset;
        this.nextIndex = sourceOffset;
        this.sourceLength = length;
        this.intoIndexStackIndex = 0;
        this.scopeEndIndex = length;

        return this;
    }




    public boolean hasNext() {
        return this.nextIndex < this.scopeEndIndex;
    }

    public IonReader next() {
        this.index = this.nextIndex;
        return this;
    }

    public IonReader nextParse() {
        return this.next().parse();
    }

    public IonReader parse() {
        int leadByte = 255 & this.source[index++];
        this.fieldType = leadByte >> 4;
        this.fieldLengthLength = leadByte & 15;

        switch(this.fieldType){
            case IonFieldTypes.TINY: {
                this.fieldLength = 0;
                this.nextIndex += 1;
                break;
            }
            case IonFieldTypes.UTF_8_SHORT: ;
            case IonFieldTypes.UTC_DATE_TIME: ;
            case IonFieldTypes.COMPLEX_TYPE_ID_SHORT: ;
            case IonFieldTypes.KEY_SHORT: ;
            case IonFieldTypes.INT_POS: ;
            case IonFieldTypes.INT_NEG: ;
            case IonFieldTypes.FLOAT : {
                this.fieldLength = this.fieldLengthLength;
                this.nextIndex   += 1 + this.fieldLengthLength;
                break;
            }

            case IonFieldTypes.EXTENDED : {
                this.fieldTypeExtended = this.source[index++]; //read extended field type - first byte after lead byte
                switch(this.fieldTypeExtended) {

                    case IonFieldTypes.ELEMENT_COUNT : {
                        this.fieldLength = this.fieldLengthLength;
                        break;
                    }
                    case IonFieldTypes.COMPLEX_TYPE_ID : {
                        this.fieldLength = 0;
                        for(int i=0; i<this.fieldLengthLength; i++){
                            this.fieldLength <<= 8;
                            this.fieldLength |= 255 & this.source[index++];
                        }
                        this.nextIndex += 1 + this.fieldLengthLength + this.fieldLength;
                        break;
                    }
                    case IonFieldTypes.COMPLEX_TYPE_VERSION : {
                        this.fieldLength = 0;
                        for(int i=0; i<this.fieldLengthLength; i++){
                            this.fieldLength <<= 8;
                            this.fieldLength |= 255 & this.source[index++];
                        }
                        this.nextIndex += 1 + this.fieldLengthLength + this.fieldLength;
                        break;
                    }
                }
                break;
            }

            //fine for all fields that use the lengthLength field normally - meaning Normal length fields (not Short and Tiny).
            default : {
                this.fieldLength = 0;
                for(int i=0; i<this.fieldLengthLength; i++){
                    this.fieldLength <<= 8;
                    this.fieldLength |= 255 & this.source[index++];
                }
                this.nextIndex += 1 + this.fieldLengthLength + this.fieldLength;
            }
        }
        return this;
    }

    public IonReader moveInto() {
        //moveInto() only works for complex types like objects, tables and arrays

        long stackValue = this.index - 1 - this.fieldLengthLength;
        stackValue <<= 32;
        stackValue |= this.scopeEndIndex;

        this.intoIndexStack[this.intoIndexStackIndex++] = stackValue;

        this.scopeEndIndex = this.nextIndex;
        this.nextIndex = this.index;  //restart nextIndex counting from inside object.
        //parse(); //yes?

        return this;
    }

    public void moveOutOf() {

        long stackValue = this.intoIndexStack[--this.intoIndexStackIndex];

        this.scopeEndIndex = (int) (0xFFFFFFFF & stackValue);
        this.index         = (int) (0xFFFFFFFF & (stackValue >> 32));

        this.nextIndex = this.index;  //restart nextIndex counting from outer object.
        parse(); //
    }


    public int readBytes(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readBytes(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        int startOffset = this.index + this.fieldLengthLength;
        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, startOffset, dest, offset, length);
        return length;
    }


    public boolean readBoolean() {
        if(this.fieldLengthLength == 1) return true;
        return false;
    }

    public Boolean readBooleanObj() {
        if(this.fieldLengthLength == 0) return null;
        if(this.fieldLengthLength == 1) return true;
        return false;
    }

    public long readInt64() {
        if(this.fieldLengthLength == 0) {
            return 0;
        }

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }

        if(this.fieldType == IonFieldTypes.INT_NEG){
            value = -value;
        }

        return value;
    }

    public Long readInt64Obj() {
        if(this.fieldLengthLength == 0) {
            return null;
        }

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }

        if(this.fieldType == IonFieldTypes.INT_NEG){
            value = -value;
        }

        return value;
    }

    public float readFloat32() {
        if(this.fieldLengthLength == 0) {
            return 0;
        }

        int valueIndex = this.index;
        int value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }
        return Float.intBitsToFloat(value);
    }

    public Float readFloat32Obj() {
        if(this.fieldLengthLength == 0) {
            return null;
        }

        int valueIndex = this.index;
        int value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }
        return Float.intBitsToFloat(value);
    }

    public double readFloat64() {
        if(this.fieldLengthLength == 0) {
            return 0;
        }

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }
        return Double.longBitsToDouble(value);
    }

    public Double readFloat64Obj() {
        if(this.fieldLengthLength == 0) {
            return null;
        }

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }
        return Double.longBitsToDouble(value);
    }

    public int readUtf8(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readUtf8(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public String readUtf8String(){
        if(this.fieldLengthLength == 0) return null;

        return new String(this.source, this.index, this.fieldLength);
    }

    public Calendar readUtcCalendar() {
        //todo can be optimized ?

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(UTC_TIME_ZONE);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int localIndex = this.index;
        int year = (255) & this.source[localIndex++];
        year <<=8;
        year |= (255) & this.source[localIndex++];
        calendar.set(Calendar.YEAR, year);
        if(this.fieldLength == 2){ return calendar; }

        calendar.set(Calendar.MONTH, (255 & this.source[localIndex++]) -1);
        if(this.fieldLength == 3){ return calendar; }

        calendar.set(Calendar.DAY_OF_MONTH, (255 & this.source[localIndex++]));
        if(this.fieldLength == 4){ return calendar; }

        calendar.set(Calendar.HOUR_OF_DAY, (255 & this.source[localIndex++]));
        if(this.fieldLength == 5){ return calendar; }

        calendar.set(Calendar.MINUTE, (255 & this.source[localIndex++]));
        if(this.fieldLength == 6){ return calendar; }

        calendar.set(Calendar.SECOND, (255 & this.source[localIndex++]));
        if(this.fieldLength == 7){ return calendar; }

        int millis = 255 & this.source[localIndex++];
        millis <<= 8;
        millis |= 255 & this.source[localIndex++];
        calendar.set(Calendar.MILLISECOND, millis);

        if(this.fieldLength == 9){ return calendar; }

        return calendar;
    }

    public int readComplexTypeIdShort(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readComplexTypeIdShort(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public int readComplexTypeId(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readComplexTypeId(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public int readComplexTypeVersion(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readComplexTypeVersion(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }



    public int readKey(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readKey(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public String readKeyAsUtf8String(){
        if(this.fieldLengthLength == 0) return null;

        return new String(this.source, this.index, this.fieldLength);
    }

    public int readKeyShort(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readKeyShort(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public String readKeyShortAsUtf8String(){
        if(this.fieldLengthLength == 0) return null;

        return new String(this.source, this.index, this.fieldLength);
    }

    public long readKeyShortAsLong() {
        if(this.fieldLengthLength == 0) {
            return 0;
        }

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }

        return value;
    }


    public long readElementCount() {
        //this.index++; //move over the first byte after lead byte - because this byte is the extended field id byte.

        int valueIndex = this.index;
        long value = 255 & this.source[valueIndex++];
        for(int i=1; i<this.fieldLengthLength; i++){
            value <<= 8;
            value |= 255 & this.source[valueIndex++];
        }

        return value;

    }

    public boolean matches(byte[] expected) {
        if(expected.length != this.fieldLength){
            return false;
        }
        for(int i=0; i<expected.length; i++){
            if(this.source[this.index + i] != expected[i]){
                return false;
            }
        }
        return true;
    }



}
