package com.jenkov.iap.ion.read;

/**
 * Created by jjenkov on 05-11-2015.
 */
public interface IIonFieldReader {

    public int read(byte[] source, int sourceOffset, Object destination);

    public void setNull(Object destination);


}
