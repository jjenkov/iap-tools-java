package com.jenkov.iap.ion;

/**
 * Created by jjenkov on 02-11-2015.
 */
public class IonFieldTypes {

    /* Core type ID constants */
    public static final int BYTES           =  0;  //a series of raw bytes
    public static final int TINY            =  1;  // a number between 1 and 15, useful for booleans and small enums
    public static final int INT_POS         =  2;
    public static final int INT_NEG         =  3;
    public static final int FLOAT           =  4;
    public static final int UTF_8           =  5;

    public static final int UTF_8_SHORT     =  6;
    public static final int UTC_DATE_TIME   =  7;
    public static final int COPY            =  8;   //a reference to a field stored in the IAP connection cache.

    public static final int OBJECT          =  9;
    public static final int TABLE           = 10;
    public static final int ARRAY           = 11;
    public static final int COMPLEX_TYPE_ID_SHORT = 12;   //the type of an object - reserved for special IAP object types - none so far.
    public static final int KEY             = 13;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names.
    public static final int KEY_SHORT       = 14;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names - 15 bytes or less.

    public static final int EXTENDED = 15;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names - 15 bytes or less.


    /*
        Extended type ID constants - can be from 0 to 255 - but we use only from 16 to 255 to avoid
        numeric clashes with the core type IDs.
     */
    public static final int ELEMENT_COUNT = 16;

    /*
        complex type id (not short version)
        reference
        UTC time
     */






}
