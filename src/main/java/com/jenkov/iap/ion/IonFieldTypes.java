package com.jenkov.iap.ion;

/**
 * Created by jjenkov on 02-11-2015.
 */
public class IonFieldTypes {

    /* Core type ID constants */
    public static final int BYTES           =  0;  //a series of raw bytes
    public static final int BOOLEAN         =  1;  // a number between 1 and 15, useful for booleans and small enums
    public static final int INT_POS         =  2;
    public static final int INT_NEG         =  3;
    public static final int FLOAT           =  4;
    public static final int UTF_8           =  5;

    public static final int UTF_8_SHORT     =  6;
    public static final int UTC_DATE_TIME   =  7;

    public static final int ARRAY           = 10;
    public static final int TABLE           = 11;
    public static final int OBJECT          = 12;
    public static final int KEY             = 13;   //a sequence of bytes identifying a key or a property fieldName - often UTF-8 encoded field names.
    public static final int KEY_SHORT       = 14;   //a sequence of bytes identifying a key or a property fieldName - often UTF-8 encoded field names - 15 bytes or less.

    public static final int EXTENDED = 15;   //a sequence of bytes identifying a key or a property fieldName - often UTF-8 encoded field names - 15 bytes or less.


    /*
        Temporarily moved types

     */
    //public static final int COMPLEX_TYPE_ID_SHORT = 12;   //the type of an object - reserved for special IAP object types - none so far.
    // public static final int COPY            =  8;   //a relative reference to a field located earlier in same ION structure.


    /*
        Extended type ID constants - can be from 0 to 255 - but we use only from 16 to 255 to avoid
        numeric clashes with the core type IDs. The clashes are only "mental". They could be avoided in code.
     */
    public static final int ELEMENT_COUNT        = 16;  //extended short
    public static final int COMPLEX_TYPE_ID      = 17;  //extended normal
    public static final int COMPLEX_TYPE_VERSION = 18;  //extended short - 15 chars to indicate version must be enough

    /*
        reference
        UTC time (without date)
     */






}
