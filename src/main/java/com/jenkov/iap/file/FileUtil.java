package com.jenkov.iap.file;

/**
 * Created by jjenkov on 16-08-2015.
 */
class FileUtil {

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60L * ONE_SECOND;
    public static final long ONE_HOUR   = 60L * ONE_MINUTE;
    public static final long ONE_DAY    = 24L * ONE_HOUR;

    public static final long ONE_KB     = 1024L;
    public static final long ONE_MB     = 1024L * ONE_KB;
    public static final long ONE_GB     = 1024L * ONE_MB;

    public static final int DATE_STR_LENGTH = 11; // 11 characters, e.g.   2015-12-31.
    public static final int TIME_STR_LENGTH =  9; //  9 characters, e.g.   14-59.59.


    public static int parseInt(String str, int startIndex, int endIndex){
        int val = 0;
        for(int i=startIndex; i<endIndex; i++){
            val *= 10; //multiply by 10 before adding next number.
            switch(str.charAt(i)) {
                case '0' : break;
                case '1' : val +=1; break;
                case '2' : val +=2; break;
                case '3' : val +=3; break;
                case '4' : val +=4; break;
                case '5' : val +=5; break;
                case '6' : val +=6; break;
                case '7' : val +=7; break;
                case '8' : val +=8; break;
                case '9' : val +=9; break;
            }
        }
        return val;
    }
}
