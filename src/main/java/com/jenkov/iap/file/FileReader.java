package com.jenkov.iap.file;

import com.jenkov.iap.error.IapToolsException;

import java.io.*;
import java.nio.file.Paths;

/**
 * The FileReader is capable of iterating over all log files belonging to the same log (based on the log path).
 * For each log file the FileReader can return a LogFileReader which can be used to read that concrete log file.
 * 
 * A log path could be "data/logs/accounts" but no concrete log files would be named that. Log files belonging to the
 * log identified by this log path would be named "data/logs/accounts.2015-08-17.14.00.00.0.wlog", 
 * "data/logs/accounts.2015-08-17.14.00.00.1.wlog", "data/logs/accounts.2015-08-17.15.00.00.0.wlog" etc. Each log file
 * name consists of the log base name + date + time + file number + wlog. 
 * 
 * The log base name is e.g "accounts". The date is the day the log file was created. The time is the time interval of the
 * day the log file belongs to, e.g. from 14.00.00 to 15.00.00 . The file number tells which number this log file is
 * within the same time interval, in case the log files within a time interval exceed their maximum size. Each time a new
 * time interval begins, the file number starts from 0 again.
 * 
 * The time interval size is set via the LogWriter via the maxAge parameter to its constructor. The maxAge tells how many
 * seconds old a log file is allowed to be, before a new log file should be created. Rolling log files based on their
 * age makes it easier to perform regular file backups, and analyze write load within a given time interval.
 * 
 * The maximum log file size is also set via the LogWriter. The maximum log file size dictates the maximum log file
 * size any given log file can have. If a log file for a given time interval exceeds its maximum size, a new log file
 * will be created by the LogWriter, with the
 *
 * Created by jjenkov on 17-08-2015.
 */
public class FileReader {

    private String filePath = null;
    private String fileDir  = null;

    private FileList fileList = null;

    /**
     * Creates a FileReader pointing to the log with the given log path. The log path should not point to a specific
     * log file, but rather to a logical log path which consists of the path to the directory where the log files are
     * located, plus the the base name of the log files. As an example the directory could be "data/logs" and the
     * log base name could be "accounts" . The full logical log path for this log would be "data/logs/accounts".
     *
     * The individual log files will be named "logical log path + date + time + file number + wlog". For instance,
     * "data/logs/accounts.2015-08-17.14.00.00.0.wlog", "data/logs/accounts.2015-08-17.14.00.00.1.wlog" etc.
     * The logical log path does not include the date, time, file number and extension.
     *
     * @param filePath  The logical path (directory + log base name) of the log.
     */
    public FileReader(String filePath, int maxFileCount) throws IOException {
        this.filePath = filePath;
        this.fileDir = Paths.get(filePath).getParent().toString();
        this.fileList = new FileList(this.filePath, maxFileCount);
        this.fileList.syncWithFileSystem();
        this.fileList.sort();
    }

    public int fileCount() {
        return this.fileList.count;
    }

    public String fileName(int fileIndex){
        return this.fileList.fileNames[fileIndex];
    }

    public String filePath(int fileIndex) { return this.fileDir + File.separator + this.fileList.fileNames[fileIndex]; }

    public File file(int fileIndex) { return new File(filePath(fileIndex)); }

    public int readFile(int fileIndex, byte[] dest, int destOffset) throws IOException {
        File file = new File(filePath(fileIndex));
        if(file.length() > dest.length){
            throw new IapToolsException("File too big for array: File length: " + file.length() + " , array length: " + dest.length);
        }

        try(InputStream input = new FileInputStream(file)) {
            return input.read(dest, destOffset, (int) file.length());
        }
    }

}
