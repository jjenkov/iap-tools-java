package com.jenkov.iap.file;

import java.util.Comparator;

/**
 * A Comparator which can compare log file names based on their date, time and file number parts of the file name.
 * The base log file name must be the same. No comparison of the base log file name is done since it does not make
 * sense to compare files belonging to different logs. It only makes sense to compare log files belonging to the
 * same log.
 *
 * Created by jjenkov on 16-08-2015.
 */
class FileComparator implements Comparator<String> {

    private String logFileBaseName = null;

    private int dateStrStartIndex = 0;
    private int timeStrStartIndex = 0;
    private int fileNoStartIndex  = 0;

    public FileComparator(String logFileBaseName) {
        this.logFileBaseName = logFileBaseName;

        this.dateStrStartIndex = this.logFileBaseName.length() + 1;
        this.timeStrStartIndex = this.dateStrStartIndex + FileUtil.DATE_STR_LENGTH;
        this.fileNoStartIndex  = this.timeStrStartIndex + FileUtil.TIME_STR_LENGTH;

    }

    @Override
    public int compare(String fileName1, String fileName2) {
        //compare date string

        for(int i=0; i<10; i++){
            if(fileName1.charAt(dateStrStartIndex + i) != fileName2.charAt(dateStrStartIndex + i)){
                if(fileName1.charAt(dateStrStartIndex + i) < fileName2.charAt(dateStrStartIndex + i)) {
                    return -1;
                }
                return 1;
            }
        }

        //compare time string
        for(int i=0; i<8; i++){
            if(fileName1.charAt(timeStrStartIndex + i) != fileName2.charAt(timeStrStartIndex + i)){
                if(fileName1.charAt(timeStrStartIndex + i) < fileName2.charAt(timeStrStartIndex + i)) {
                    return -1;
                }
                return 1;
            }
        }

        //compare file number
        int fileNo1EndIndex   = fileName1.indexOf('.', fileNoStartIndex + 1);
        int fileNo1 = FileUtil.parseInt(fileName1, fileNoStartIndex, fileNo1EndIndex);

        int fileNo2EndIndex   = fileName2.indexOf('.', fileNoStartIndex + 1);
        int fileNo2 = FileUtil.parseInt(fileName2, fileNoStartIndex, fileNo2EndIndex);

        return fileNo1 - fileNo2;
    }


}
