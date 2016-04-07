package com.jenkov.iap.file;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jjenkov on 12-03-2016.
 */
public class FileWriterTest {


    //@Test
    public void testLogRolling() throws IOException {
        FileWriter fileWriter = new FileWriter("test-data/testfile", 10 * 1000, 1024);

        byte[] data = new byte[128];

        int iterations = 16;
        for(int i=0; i<iterations; i++){
            fileWriter.write(data);
        }

        try {
            Thread.sleep(11 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fileWriter.write(data);


        fileWriter.close();
    }
}
