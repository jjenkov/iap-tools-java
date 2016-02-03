package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An IonObjectWriter instance can write an object (instance) of some class to ION data ("ionize" the object in other words).
 * An IonObjectWriter is targeted at a single class. To serialize objects of multiple classes, create one IonObjectWriter
 * per class.
 *
 */
public class IonObjectWriter {

    public Class   typeClass = null;
    public IIonFieldWriter[] fieldWriters = null;

    /**
     * Creates an IonObjectWriter targeted at the class passed as parameter to this constructor.
     *
     * @param typeClass The class this IonObjectWriter should be able to write instances of (to ION).
     */
    public IonObjectWriter(Class typeClass) {
        this.typeClass = typeClass;

        Field[] fields = this.typeClass.getDeclaredFields();
        this.fieldWriters = new IIonFieldWriter[fields.length];

        for(int i=0; i < fields.length; i++){
            fieldWriters[i] = IonUtil.createFieldWriter(fields[i]);
        }
    }


    /**
     * Creates an IonObjectWriter targeted at the class passed as parameter to this constructor.
     * The IIonObjectWriterConfigurator can configure (modify) this IonObjectWriter instance. For instance,
     * it can signal that some fields should not be included when writing the object, or modify what field
     * name is to be used when writing the object.
     *
     * @param typeClass    The class this IonObjectWriter should be able to write instances of (to ION).
     * @param configurator The configurator that can configure each field writer (one per field of the target class) in this IonWriter - even exclude them.
     */
    public IonObjectWriter(Class typeClass, IIonObjectWriterConfigurator configurator){
        this.typeClass = typeClass;

        Field[] fields = this.typeClass.getDeclaredFields();

        List<IIonFieldWriter> fieldWriterTemp = new ArrayList<IIonFieldWriter>();

        IonFieldWriterConfiguration fieldConfiguration = new IonFieldWriterConfiguration();

        for(int i=0; i < fields.length; i++){
            fieldConfiguration.include = true;
            fieldConfiguration.name = fields[i].getName();

            configurator.configure(fieldConfiguration);

            if(fieldConfiguration.include){
                if(fieldConfiguration.alias == null){
                    fieldWriterTemp.add(IonUtil.createFieldWriter(fields[i]));
                } else {
                    fieldWriterTemp.add(IonUtil.createFieldWriter(fields[i], fieldConfiguration.alias));
                }
            }
        }

        this.fieldWriters = new IIonFieldWriter[fieldWriterTemp.size()];

        for(int i=0, n=fieldWriterTemp.size(); i < n; i++){
            this.fieldWriters[i] = fieldWriterTemp.get(i);
        }
    }


    public int writeObject(Object src, int maxLengthLength, byte[] destination, int destinationOffset){
        destination[destinationOffset++] = (byte) (255 & ((IonFieldTypes.OBJECT << 4) | maxLengthLength));

        int lengthOffset   = destinationOffset; //store length start offset for later use
        destinationOffset += maxLengthLength;


        for(int i=0; i<fieldWriters.length; i++){
            if(fieldWriters[i] != null){
                destinationOffset += fieldWriters[i].writeKeyAndValueFields(src, destination, destinationOffset, maxLengthLength);
            }
        }

        int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

        switch(maxLengthLength){
            case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
            case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
            case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
            case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
        }

        return 1 + maxLengthLength + fullFieldLength;
    }




}
