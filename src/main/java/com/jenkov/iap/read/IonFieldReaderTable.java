package com.jenkov.iap.read;

import com.jenkov.iap.IonFieldTypes;
import com.jenkov.iap.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jjenkov on 11-11-2015.
 */
public class IonFieldReaderTable implements IIonFieldReader {

    private Field field = null;
    private Class typeInTable = null;
    private Map<IonKeyFieldKey, IIonFieldReader> fieldReaderMap = new HashMap<>();

    private List tempList = new ArrayList();


    //This array is first used when reading field values from a table. First the key fields are used to obtain
    //IIapFieldReader instances from the fieldReaderMap. The IIapFieldReader instances are inserted into this
    //array according to the order their corresponding key fields appear in the beginning of the table.
    //Afterwards the value fields in the table are read using the IIapFieldReader instances in this array,
    //cycling through the fieldReaderArray until there are no more value fields in the table.
    private IIonFieldReader[] fieldReaderArray = null;

    private IonKeyFieldKey tempKeyFieldKey = new IonKeyFieldKey();

    public IonFieldReaderTable(Field field) {
        this.field = field;

        this.typeInTable = field.getType().getComponentType();  // field type is an array, so we need to get to the component type of the array.

        Field[] typeInTableFields = typeInTable.getDeclaredFields();

        for(int i=0; i<typeInTableFields.length; i++){
            Field fieldOfTypeInTable = typeInTableFields[i];
            putFieldReader(fieldOfTypeInTable, IonUtil.createFieldReader(fieldOfTypeInTable));
        }

        //don't fill array until during reading.
        fieldReaderArray = new IIonFieldReader[typeInTableFields.length];
    }

    private void putFieldReader(Field field, IIonFieldReader fieldReader) {
        try {
            this.fieldReaderMap.put(new IonKeyFieldKey(field.getName().getBytes("UTF-8")), fieldReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int tableStartIndex = sourceOffset;

        int tableLeadByte = 255 & source[sourceOffset++];
        int tableLengthLength  = tableLeadByte & 15;

        if(tableLengthLength == 0){
            return 1; //table field with null as values is always 1 byte long (has 0 keys and 0 values).
        }

        int tableLength = 255 & source[sourceOffset++];
        for(int i=1; i < tableLengthLength; i++){
            tableLength <<= 8;
            tableLength |= 255 & source[sourceOffset++];
        }

        int tableEndIndex = tableStartIndex + 1 + tableLengthLength + tableLength;


        //read the key fields of the table
        tempKeyFieldKey.setSource(source);
        int fieldReadersInArray = 0;

        boolean endOfKeyFieldsFound = false;
        while(!endOfKeyFieldsFound){
            int fieldLeadByte = 255 & source[sourceOffset++];
            int fieldType     = fieldLeadByte >> 4;

            switch(fieldType){
                case IonFieldTypes.KEY_COMPACT :  {
                    int keyLength = fieldLeadByte & 15;
                    tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                    this.fieldReaderArray[fieldReadersInArray++] = this.fieldReaderMap.get(tempKeyFieldKey);
                    sourceOffset += keyLength;
                    break;
                }
                case IonFieldTypes.KEY : {
                    int keyLengthLength = fieldLeadByte & 15;
                    int keyLength = 0;
                    for(int i=0; i < tableLengthLength; i++){
                        keyLength <<= 8;
                        keyLength |= 255 & source[sourceOffset++];
                    }
                    tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                    this.fieldReaderArray[fieldReadersInArray++] = this.fieldReaderMap.get(tempKeyFieldKey);
                    sourceOffset += keyLength;
                    break;
                }

                default : {
                    endOfKeyFieldsFound = true;
                }
            }
        }

        //start reading the value fields.
        sourceOffset--; //will have skipped over the lead byte of first value field during search for key fields.
        int fieldReaderIndex = 0;
        Object objectInTable = null;
        while(sourceOffset < tableEndIndex){
            if(fieldReaderIndex == 0) {
                try {
                    objectInTable = this.typeInTable.newInstance();
                    this.tempList.add(objectInTable);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            sourceOffset += this.fieldReaderArray[fieldReaderIndex++]
                                .read(source, sourceOffset, objectInTable);

            if(fieldReaderIndex == fieldReadersInArray){
                fieldReaderIndex = 0; // cycle back to first field reader.
            }
        }

        Object arrayInstance = Array.newInstance(this.typeInTable, this.tempList.size());

        for(int i=0, n=this.tempList.size(); i < n; i++){
            Array.set(arrayInstance, i, this.tempList.get(i));   //todo perhaps use a faster data type than ArrayList
        }

        try {
            this.field.set(destination, arrayInstance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.tempList.clear();

        return tableEndIndex - tableStartIndex; //is this correct? I think so.
    }


    @Override
    public void setNull(Object destination) {

    }
}
