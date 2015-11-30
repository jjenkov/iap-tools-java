package com.jenkov.iap.read;

import com.jenkov.iap.IonFieldTypes;

/**
 * Created by jjenkov on 08-11-2015.
 */
public class IonReader {

    public byte[] source = null;
    public int sourceLength = 0;

    public int index = 0;
    public int nextIndex = 0;
    public int scopeEndIndex = 0;

    public int fieldType   = 0;
    public int fieldLengthLength = 0;
    public int fieldLength = 0;

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

    public void setSource(byte[] source, int sourceOffset, int length){
        this.source = source;
        this.index  = sourceOffset;
        this.nextIndex = sourceOffset;
        this.sourceLength = length;
        this.intoIndexStackIndex = 0;
        this.scopeEndIndex = length;
    }



    public void next() {
        this.index = this.nextIndex;
    }

    public boolean hasNext() {
        return this.nextIndex < this.scopeEndIndex;
    }


    public void parse() {
        int leadByte = 255 & this.source[index++];
        this.fieldType = leadByte >> 4;
        this.fieldLengthLength = leadByte & 15;

        switch(this.fieldType){
            case IonFieldTypes.BOOLEAN : {
                this.fieldLength = 0;
                this.nextIndex += 1;
                break;
            }
            case IonFieldTypes.KEY_COMPACT: ;
            case IonFieldTypes.INT_POS: ;
            case IonFieldTypes.INT_NEG: ;
            case IonFieldTypes.FLOAT : {
                this.fieldLength = this.fieldLengthLength;
                this.nextIndex   += 1 + this.fieldLengthLength;
                break;
            }

            //fine for all fields that use the lengthLength field normally - meaning non-compact fields.
            default : {
                this.fieldLength = 0;
                for(int i=0; i<this.fieldLengthLength; i++){
                    this.fieldLength <<= 8;
                    this.fieldLength |= 255 & this.source[index++];
                }
                this.nextIndex += 1 + this.fieldLengthLength + this.fieldLength;
            }
        }
    }

    public void parseInto() {
        //parseInto() only works for complex types like objects, tables and arrays

        long stackValue = this.index - 1 - this.fieldLengthLength;
        stackValue <<= 32;
        stackValue |= this.scopeEndIndex;

        this.intoIndexStack[this.intoIndexStackIndex++] = stackValue;

        this.scopeEndIndex = this.nextIndex;
        this.nextIndex = this.index;  //restart nextIndex counting from inside object.
        parse(); //yes?
    }

    public void parseOutOf() {

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

    public int readKeyCompact(byte[] dest){
        if(this.fieldLengthLength == 0) return 0;

        int length = Math.min(dest.length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, 0, length);
        return length;
    }

    public int readKeyCompact(byte[] dest, int offset, int length){
        if(this.fieldLengthLength == 0) return 0;

        length = Math.min(length, this.fieldLength);
        System.arraycopy(this.source, this.index, dest, offset, length);
        return length;
    }

    public String readKeyCompactAsUtf8String(){
        if(this.fieldLengthLength == 0) return null;

        return new String(this.source, this.index, this.fieldLength);
    }



}
