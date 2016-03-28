package com.jenkov.iap.file;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jjenkov on 28-03-2016.
 */
public class LogFileWriterTest {


    public static void main(String[] args) throws IOException {

        String logFileBasePath = "test-data/log-file";
        if(args.length > 0){
            logFileBasePath = args[0];
        }

        int iterations = 1024;
        if(args.length > 1) {
            iterations = Integer.parseInt(args[1]);
        }

        int flushInterval = 10;
        if(args.length > 2) {
            flushInterval = Integer.parseInt(args[2]);
        }

        FileWriter    fileWriter    = new FileWriter(logFileBasePath, FileWriter.ONE_HOUR, 16 * FileWriter.ONE_MB);
        LogFileWriter logFileWriter = new LogFileWriter(fileWriter, new byte[1024 * 1024]);

        byte[] data = new byte[1024];

        long startTime = System.currentTimeMillis();
        for(int j=0; j<iterations; j++){
            logFileWriter.writeRecord(data, 0, data.length);

            if( (j % 10) == 0 ){
                logFileWriter.flushToDisk();
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("total time = " + totalTime);








    }
}
