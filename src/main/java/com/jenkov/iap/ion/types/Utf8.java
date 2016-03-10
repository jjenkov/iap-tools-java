package com.jenkov.iap.ion.types;

/**
 * This class represents a UTF-8 byte sequence. An Utf8 instance contains a reference to a byte[] array in which the UTF-8 byte
 * sequence is stored, an offset into the byte array where the UTF-8 byte sequence starts, and a length specifying
 * the length in bytes (not characters) of the UTF-8 byte sequence.
 */
public class Utf8 {
    public byte[] source = null;
    public int    offset = 0;
    public int    length = 0;

}
