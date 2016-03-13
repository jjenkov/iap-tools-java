package com.jenkov.iap.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by jjenkov on 16-08-2015.
 */
class FileList {

    private Path   fileDir      = null;
    private String fileBaseName = null;

    public String[] fileNames = null;
    public int      count        = 0;

    private FileComparator comparator = null;

    public FileList(String logPath, int maxCount ){
        Path logFilePath      = Paths.get(logPath);
        this.fileDir = logFilePath.getParent();
        this.fileBaseName = logFilePath.getFileName().toString();
        this.fileNames = new String[maxCount];

        this.comparator       = new FileComparator(this.fileBaseName);
    }

    public void add(String logFileName){
        if(this.count < fileNames.length){
            this.fileNames[this.count] = logFileName;
            this.count++;
        }
    }

    public void sort() {
        Arrays.sort(this.fileNames, 0, this.count, this.comparator);
    }


    public void syncWithFileSystem() throws IOException {
        File file = this.fileDir.toFile();
        File[] files = file.listFiles();
        for(int i=0; i<files.length; i++){
            if(files[i].getName().startsWith(this.fileBaseName)){
                //System.out.println(files[i].getName());

                add(files[i].getName());
            }
        }

    }





}
