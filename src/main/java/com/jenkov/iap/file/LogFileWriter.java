package com.jenkov.iap.file;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;
import com.jenkov.iap.ion.write.IonWriter;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by jjenkov on 28-03-2016.
 */
public class LogFileWriter {

    private IonWriter  ionWriter  = new IonWriter();
    private FileWriter fileWriter = null;
    private byte[]     buffer     = null;

    private GregorianCalendar calendar = new GregorianCalendar();

    public LogFileWriter(FileWriter fileWriter, byte[] tempBuffer) {
        this.fileWriter = fileWriter;
        this.buffer     = tempBuffer;

        this.ionWriter.setDestination(this.buffer, 0);
        this.calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    }



    public void writeRecord(byte[] source, int sourceOffset, int sourceLength) {

        this.calendar.setTimeInMillis(System.currentTimeMillis());

        this.ionWriter.writeUtc(this.calendar, 9); // time stamp including milliseconds
        this.ionWriter.writeBytes(source, sourceOffset, sourceLength);
    }

    public void flushToDisk() throws IOException {
        this.fileWriter.write(this.buffer, 0, this.ionWriter.destIndex);
        this.fileWriter.flush();

        this.ionWriter.setDestination(this.buffer, 0);
    }

}
