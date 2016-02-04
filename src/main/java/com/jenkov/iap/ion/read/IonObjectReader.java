package com.jenkov.iap.ion.read;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * An IonObjectReader instance can read an object (instance) of some class from ION data ("normalize" the object in
 * other words). An IonObjectReader instance is targeted at a single Java class. To read objects of multiple classes,
 * create on IonObjectReader per class you want to read instances of.
 *
 *
 */
public class IonObjectReader {

    private Class typeClass = null;

    private Map<IonKeyFieldKey, IIonFieldReader> fieldReaderMap = new HashMap<>();
    private IonFieldReaderNop nopFieldReader = new IonFieldReaderNop();

    private IonKeyFieldKey currentKeyFieldKey = new IonKeyFieldKey();


    /**
     * Creates an IonObjectReader targeted at the given class.
     * @param typeClass The class this IonObjectReader instance should be able to read instances of, from ION data.
     */
    public IonObjectReader(Class typeClass) {
        this.typeClass = typeClass;

        Field[] fields = this.typeClass.getDeclaredFields();

        for(int i=0; i < fields.length; i++){
            putFieldReader(fields[i].getName(), IonUtil.createFieldReader(fields[i]));
        }
    }


    /**
     * Creates an IonObjectReader targeted at the given class.
     * The IIonObjectReaderConfigurator can configure (modify) this IonObjectReader instance.
     * For instance, the configurator can signal that some fields should not be read, or that different field names
     * are used in the ION data which should be mapped to other field names in the target Java class.
     *
     * @param typeClass The class this IonObjectReader instance should be able to read instances of, from ION data.
     * @param configurator  The configurator that can configure each field reader (one per field in the target class) of this IonObjectReader - even exclude them.
     */
    public IonObjectReader(Class typeClass, IIonObjectReaderConfigurator configurator) {
        this.typeClass = typeClass;

        Field[] fields = this.typeClass.getDeclaredFields();

        IonFieldReaderConfiguration fieldConfiguration = new IonFieldReaderConfiguration();


        for(int i=0; i < fields.length; i++) {
            fieldConfiguration.include = true;
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias = null;

            configurator.configure(fieldConfiguration);

            if (fieldConfiguration.include) {
                if (fieldConfiguration.alias == null) {
                    putFieldReader(fields[i].getName(), IonUtil.createFieldReader(fields[i]));
                } else {
                    putFieldReader(fieldConfiguration.alias, IonUtil.createFieldReader(fields[i]));
                }
            }
        }
    }



    private void putFieldReader(String fieldName, IIonFieldReader fieldReader) {
        try {
            this.fieldReaderMap.put(new IonKeyFieldKey(fieldName.getBytes("UTF-8")), fieldReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public Object read(byte[] source, int sourceOffset){
        this.currentKeyFieldKey.setSource(source);

        Object destination =  instantiateType();

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 4;

        //todo if not object - throw exception

        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(lengthLength == 0){
            return null; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int endIndex = sourceOffset + length;



        while(sourceOffset < endIndex){
            leadByte     = 255 & source[sourceOffset++];
            fieldType    = leadByte >> 4;
            lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

            //todo can this be optimized with a switch statement?

            //expect a key field
            if(fieldType == IonFieldTypes.KEY || fieldType == IonFieldTypes.KEY_SHORT){

                //distinguish between length and lengthLength depending on compact key field or normal key field
                length = 0;
                if(fieldType == IonFieldTypes.KEY_SHORT){
                    length = leadByte & 15;
                } else {
                    for(int i=0; i<lengthLength; i++){
                        length <<= 8;
                        length |= 255 & source[sourceOffset++];
                    }
                }

                this.currentKeyFieldKey.setOffsets(sourceOffset, length);

                IIonFieldReader reader = this.fieldReaderMap.get(this.currentKeyFieldKey);
                if(reader == null){
                    reader = this.nopFieldReader;
                }

                //find beginning of next field value - then call field reader.
                sourceOffset += length;

                //todo check for end of object - if found, call reader.setNull() - no value field following the key field.


                int nextLeadByte  = 255 & source[sourceOffset];
                int nextFieldType = nextLeadByte >> 4;

                if(nextFieldType != IonFieldTypes.KEY && nextFieldType != IonFieldTypes.KEY_SHORT){
                    sourceOffset += reader.read(source, sourceOffset, destination);
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    reader.setNull(destination);
               }
            }

        }

        return destination;
    }

    private Object instantiateType() {
        try {
             return this.typeClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null; //todo remove later when rethrowing exceptions.
    }


}
