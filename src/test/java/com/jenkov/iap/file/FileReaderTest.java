package com.jenkov.iap.file;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by jjenkov on 13-03-2016.
 */
public class FileReaderTest {


    @Test
    public void testReadFiles() throws IOException {
        FileWriter fileWriter = new FileWriter("test-data/testfile", 10 * 1000, 1024);

        byte[] data = new byte[128];

        int iterations = 16;
        for(int i=0; i<iterations; i++){
            fileWriter.write(data);
        }
        fileWriter.close();


        FileReader fileReader = new FileReader("test-data/testfile", 10);

        for(int i=0; i<fileReader.fileCount(); i++){
            System.out.println(fileReader.filePath(i));
        }

        byte[] dest = new byte[1024];
        for(int i=0; i<fileReader.fileCount(); i++){
            System.out.println(fileReader.readFile(i, dest, 0));
        }

        for(int i=0; i<fileReader.fileCount(); i++){
            System.out.println(fileReader.file(i).delete());
        }



    }
}
