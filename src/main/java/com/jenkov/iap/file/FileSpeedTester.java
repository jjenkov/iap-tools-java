package com.jenkov.iap.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by jjenkov on 28-03-2016.
 */
public class FileSpeedTester {

    public static DecimalFormat decimalFormat = new DecimalFormat("###,000");

    public static void main(String[] args) throws IOException {

        System.out.println("IAP Tools - File Speed Tester");

        String logFileBasePath = "test-data/log-file";
        if(args.length > 0){
            logFileBasePath = args[0];
        }
        System.out.println("Test file base name: " + logFileBasePath);

        int iterations = 1024;
        if(args.length > 1) {
            iterations = Integer.parseInt(args[1]);
        }
        System.out.println("Iterations: " + iterations);

        int flushInterval = 10;
        if(args.length > 2) {
            flushInterval = Integer.parseInt(args[2]);
        }
        System.out.println("Flush interval: " + flushInterval);

        writeTest(logFileBasePath, iterations, flushInterval);
        readTest(logFileBasePath);

    }

    private static void readTest(String logFileBasePath) throws IOException {
        FileReader reader = new FileReader(logFileBasePath, 1024);
        byte[] dest = new byte[24 * 1024 * 1024];

        long totalBytesRead = 0;
        long startTime = System.currentTimeMillis();
        for(int i=0; i < reader.fileCount(); i++) {
            totalBytesRead += reader.readFile(i, dest, 0);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Files read: " + reader.fileCount());
        System.out.println("Total read time: " + totalTime);
        System.out.println("Read speed: " + decimalFormat.format((totalBytesRead * 1000L) / totalTime) + " bytes per second");
    }

    private static void writeTest(String logFileBasePath, int iterations, int flushInterval) throws IOException {

        FileWriter fileWriter    = new FileWriter(logFileBasePath, FileWriter.ONE_HOUR, 16 * FileWriter.ONE_MB);
        LogFileWriter logFileWriter = new LogFileWriter(fileWriter, new byte[1024 * 1024]);

        byte[] data = new byte[1024];

        long startTime = System.currentTimeMillis();
        for(int j=0; j<iterations; j++){
            logFileWriter.writeRecord(data, 0, data.length);

            if( (j % flushInterval) == 0 ){
                logFileWriter.flushToDisk();
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Total write time = " + totalTime);
        long totalBytesWritten = data.length;
        totalBytesWritten *= iterations;
        System.out.println("Write speed: " + decimalFormat.format( totalBytesWritten * 1000L / totalTime ) + " bytes per second");

    }

}
