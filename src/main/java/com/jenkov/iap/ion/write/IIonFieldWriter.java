package com.jenkov.iap.ion.write;

/**
 * Created by jjenkov on 04-11-2015.
 */
public interface IIonFieldWriter {

    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength);

    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength);

}
