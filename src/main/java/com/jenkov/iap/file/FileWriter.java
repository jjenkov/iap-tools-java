package com.jenkov.iap.file;

import com.jenkov.iap.error.IapToolsException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jjenkov on 29-06-2015.
 */
public class FileWriter implements AutoCloseable {

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final int ONE_HOUR   = ONE_MINUTE * 60;
    public static final int ONE_DAY    = ONE_HOUR   * 24;

    public static final int ONE_MB     = 1024 * 1024;




    /** the record length for each record is stored as an int (4 bytes) in the log file. */
    private static final int RECORD_LENGTH_BYTE_COUNT = 4;
    private static final int BUFFERED_OUTPUT_STREAM_BUFFER_SIZE = 8 * 1024 * 1024;

    private long maxAge  = 60L * 60L * 1000L;   // 1 hour default max age
    private long maxSize = 16L * 1024L * 1024L; // 16MB default max size for log files.

    private Path   logFilePath     = null;
    private Path   logFileDir      = null;
    private String logFileBaseName = null;

    private long currentLogFileTimeInterval = 0;
    private long currentLogFileNumber       = 0;
    private long currentLogFileSize         = 0;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");

    //private DataOutputStream dataOutput = null;
    private OutputStream output = null;

    public FileWriter(String filePath, long maxAge, long maxSize) throws FileNotFoundException {
        this.logFilePath     = Paths.get(filePath);
        this.logFileDir      = this.logFilePath.getParent();
        this.logFileBaseName = this.logFilePath.getFileName().toString();
        this.maxAge  = maxAge;
        this.maxSize = maxSize;

        initFromExistingLogFiles();
    }

    /**
     * This method scans the local file system for existing log files belonging to this log, and initializes the
     * file number of the next log file based on that. If there are already existing log files within the same
     * time interval, this method finds the file number of the last file within that time interval, and sets
     * the current file number to the number of the latest file + 1. This basically means that a new log file
     * is created, regardless of the size of the latest log file.
     *
     * This method should only ever be called once, when the LogWriter is instantiated the first time.
     */
    private void initFromExistingLogFiles() {
        this.currentLogFileTimeInterval = calculateCurrentTimeInterval();
        this.currentLogFileNumber    = 0;

        FileComparator logFileComparator = new FileComparator(this.logFileBaseName);

        if(!Files.exists(this.logFileDir)) {
            throw new IapToolsException(this.logFileDir.toString() + " does not exist.");
        }

        if(!Files.isDirectory(this.logFileDir)){
            throw new IapToolsException(this.logFileDir.toString() + " is not a directory");
        }

        File file = this.logFileDir.toFile();

        String latestFile = null;

        File[] files = file.listFiles();
        for(int i=0; i<files.length; i++ ){
            String thisFile = files[i].getName();
            if(thisFile.startsWith(this.logFileBaseName)){
                if(latestFile == null){
                    latestFile = thisFile;
                } else {
                    if(logFileComparator.compare(latestFile, thisFile) < 0){
                        latestFile = thisFile;
                    }
                }
            }
        }

        if(latestFile != null){
            String currentLogFileDateTimeStr = this.dateFormat.format(new Date(this.currentLogFileTimeInterval));

            int dateTimeStartIndex = this.logFileBaseName.length() + 1;
            int dateTimeEndIndex   = dateTimeStartIndex + FileUtil.DATE_STR_LENGTH + FileUtil.TIME_STR_LENGTH - 1;
            String latestFileDateTimeSubstring = latestFile.substring(dateTimeStartIndex, dateTimeEndIndex);

            if(currentLogFileDateTimeStr.equals(latestFileDateTimeSubstring)){
                //within same date time block as latest existing log file - use a log file with a larger file number than latest file's number.
                int fileNoStartIndex = this.logFileBaseName.length() + 1 + FileUtil.DATE_STR_LENGTH + FileUtil.TIME_STR_LENGTH;
                int fileNoEndIndex   = latestFile.indexOf('.', fileNoStartIndex);
                int latestFileNo     = FileUtil.parseInt(latestFile, fileNoStartIndex, fileNoEndIndex);

                this.currentLogFileNumber = latestFileNo + 1;
            }
        }

    }


    private long calculateCurrentTimeInterval(){

        //todo use Java 8 date time API in the future to make time calculations better.
        return (System.currentTimeMillis() / this.maxAge) * this.maxAge;
    }


    private void checkForLogFileRoll(long fullLogRecordLength) throws IOException {
        long timeIntervalForLogRecord =  calculateCurrentTimeInterval();

        if(this.output == null){
            rollLogFile();
        } else if(timeIntervalForLogRecord != this.currentLogFileTimeInterval){
            //roll to a new log file in a new time block
            this.currentLogFileTimeInterval = timeIntervalForLogRecord;
            this.currentLogFileNumber    = 0;

            rollLogFile();
        } else if(currentLogFileSize + fullLogRecordLength > this.maxSize){
            //roll to a new log file within same time block
            this.currentLogFileNumber++;
            rollLogFile();
        }
    }


    private void rollLogFile() throws IOException {

        //close current log file
        if(this.output != null){
            this.output.flush();
            this.output.close();
        }

        this.currentLogFileSize      = 0;

        StringBuilder newLogFileNameBuilder = new StringBuilder(); //todo reuse a StringBuilder instead - clear it.

        newLogFileNameBuilder.append(this.logFileBaseName);
        newLogFileNameBuilder.append('.');
        newLogFileNameBuilder.append(this.dateFormat.format(new Date(this.currentLogFileTimeInterval)));
        newLogFileNameBuilder.append('.');
        newLogFileNameBuilder.append(String.valueOf(this.currentLogFileNumber));
        newLogFileNameBuilder.append(".wlog");

        String newLogFileName = newLogFileNameBuilder.toString();

        //todo keep this.logFileDir as a String instead? Less string creation
        Path newLogFilePath = Paths.get(this.logFileDir.toString(), newLogFileName);

        //this.dataOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newLogFilePath.toFile()), BUFFERED_OUTPUT_STREAM_BUFFER_SIZE));
        this.output = new BufferedOutputStream(new FileOutputStream(newLogFilePath.toFile()), BUFFERED_OUTPUT_STREAM_BUFFER_SIZE);
    }

    /*
    public void write(String logRecord) throws IOException{
        write(logRecord.getBytes("UTF-8"));
    }
    */

    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    public void write(byte[] dataSource, int offset, int length) throws IOException {
        //checkForLogFileRoll(length + RECORD_LENGTH_BYTE_COUNT);
        //this.dataOutput.writeInt(length);
        //this.currentLogFileSize +=  length + RECORD_LENGTH_BYTE_COUNT; //4 bytes for the record length int stored before each record

        checkForLogFileRoll(length);
        this.output.write(dataSource, offset, length);
        this.currentLogFileSize +=  length;
    }


    public void flush() throws IOException {
        this.output.flush();
    }


    /**
     * Closes the currently open log file, if any.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if(this.output != null){
            this.output.flush();
            this.output.close();
        }
    }
}
