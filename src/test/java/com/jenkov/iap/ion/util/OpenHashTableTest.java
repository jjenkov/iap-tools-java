package com.jenkov.iap.ion.util;

import org.junit.Test;

/**
 * Created by jjenkov on 15-11-2015.
 */
public class OpenHashTableTest {


    @Test
    public void test() {
        OpenHashTable hashTable = new OpenHashTable(64);

        OpenHashTable.Key key1 = OpenHashTable.key("123".getBytes());

        for(int i=0; i<10; i++){
            OpenHashTable.Key key = OpenHashTable.key(("field" + i).getBytes());
            hashTable.put(key, "123");
        }

        //assertTrue(hashTable.put(key1, "123"));

        Object value = hashTable.get(key1);

    }


}
