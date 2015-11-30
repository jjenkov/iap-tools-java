package com.jenkov.iap;

/**
 * Created by jjenkov on 02-11-2015.
 */
public class IonFieldTypes {

    /* Field type constants */
    public static final int BYTES         =  0;  //a series of raw bytes
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

    public static final int COMPLEX_TYPE_ID = 11; //the type of an object - reserved for special IAP object types - none so far.

    public static final int KEY           = 12;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names.
    public static final int KEY_COMPACT   = 13;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names - 15 bytes or less.

    public static final int REFERENCE     = 14;   //a reference to a field stored in the IAP connection cache.
    public static final int EXTENDED_TYPE = 15;   //a sequence of bytes identifying a key or a property name - often UTF-8 encoded field names - 15 bytes or less.


    /*
      The two reserved types will be used for either:

        UTF_8 - 0 to 15 bytes
        UTF_8 - 16 to 31 bytes
        DATETIME
        TIME
        DURATION

      Which one we choose will be determined after measuring a lot of data traffic going through VStack

      Extended types:
       - DATETIME
       - TIME
       - DURATION

       Note that it does not make sense to represent UTF_8 (0 to 15 bytes) and UTF_8 (16 to 31 bytes) as extended
       types. The one byte these types save, the lose again with the type extension byte.

     */



}
