package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterObject implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;


    public Field[] fields    = null;

    public IIonFieldWriter[] fieldWriters = null;


    public IonFieldWriterObject(Field field, String alias, IIonObjectWriterConfigurator configurator) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(alias);

        //generate field writers for this IonFieldWriterObject instance - fields in the class of this field.
        Field[] fields = field.getType().getDeclaredFields();

        List<IIonFieldWriter> fieldWritersTemp = new ArrayList<>();

        IonFieldWriterConfiguration fieldConfiguration = new IonFieldWriterConfiguration();

        for(int i=0; i < fields.length; i++){
            //fieldWriters[i] = IonUtil.createFieldWriter(fields[i], configurator);
            fieldConfiguration.field = fields[i];
            fieldConfiguration.include = true;
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias = null;

            configurator.configure(fieldConfiguration);

            if(fieldConfiguration.include){
                if(fieldConfiguration.alias == null){
                    fieldWritersTemp.add(IonUtil.createFieldWriter(fields[i], configurator));
                } else {
                    fieldWritersTemp.add(IonUtil.createFieldWriter(fields[i], fieldConfiguration.alias, configurator));
                }
            }
        }

        this.fieldWriters = new IIonFieldWriter[fieldWritersTemp.size()];

        for(int i=0, n=fieldWritersTemp.size(); i < n; i++){
            this.fieldWriters[i] = fieldWritersTemp.get(i);
        }


    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        int valueLength = writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);

        return this.keyField.length + valueLength;
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        destination[destinationOffset++] = (byte) (255 & ((IonFieldTypes.OBJECT << 4) | maxLengthLength));

        int lengthOffset   = destinationOffset; //store length start offset for later use
        destinationOffset += maxLengthLength;

        try {
            Object fieldValue = this.field.get(sourceObject);

            for(int i=0; i<fieldWriters.length; i++){
                if(fieldWriters[i] != null){
                    destinationOffset += fieldWriters[i].writeKeyAndValueFields(fieldValue, destination, destinationOffset, maxLengthLength);
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //todo should never happen, as we set all Field instances to accessible.
        }

        return 0;
    }

}
