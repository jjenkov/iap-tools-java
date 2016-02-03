package com.jenkov.iap.ion.write;

import com.jenkov.iap.ion.IonFieldTypes;
import com.jenkov.iap.ion.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterArrayLong implements IIonFieldWriter {

    private static int MAX_ELEMENT_FIELD_LENGTH = 9;    //an ION long field can max be 9 bytes long
    private static int COMPLEX_TYPE_ID_SHORT_FIELD_LENGTH = 2;    //an ION long field can max be 9 bytes long

    protected Field  field    = null;
    protected byte[] keyField = null;

    public IonFieldWriterArrayLong(Field field, String alias) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(field);
    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            long[] value = (long[]) field.get(sourceObject);

            if(value == null) {
                dest[destOffset++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4))); //byte array which is null
                return 1;
            }

            int elementCount = value.length;
            int elementCountLengthLength = IonUtil.lengthOfInt64Value(elementCount);
            int maxPossibleFieldLength =
                    COMPLEX_TYPE_ID_SHORT_FIELD_LENGTH +          // 2 bytes for complex type id field
                         1 + elementCountLengthLength +           // +1 for lead byte of element count field (int64-positive)
                    (elementCount * MAX_ELEMENT_FIELD_LENGTH);

            int arrayLengthLength = IonUtil.lengthOfInt64Value(maxPossibleFieldLength);

            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4) | arrayLengthLength) );
            int lengthStartOffset = destOffset;

            //reserve arrayLengthLength bytes for later - when we know the real length (in bytes) of this ION array field
            destOffset += arrayLengthLength;

            //write element count
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.EXTENDED << 4) | elementCountLengthLength) );
            dest[destOffset++] = (byte) IonFieldTypes.ELEMENT_COUNT;

            for(int i=(elementCountLengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (elementCount >> i));
            }

            //write elements
            for(int i=0; i < elementCount; i++){
                long elementVal = value[i];

                int ionFieldType = IonFieldTypes.INT_POS;
                if(elementVal < 0){
                    ionFieldType = IonFieldTypes.INT_NEG;
                    elementVal  = -elementVal;
                }

                int length = IonUtil.lengthOfInt64Value(elementVal);

                dest[destOffset++] = (byte) (255 & ((ionFieldType << 4) | length));

                for(int j=(length-1)*8; j >= 0; j-=8){
                    dest[destOffset++] = (byte) (255 & (elementVal >> j));
                }
            }

            //write total length of array
            int arrayByteLength = destOffset - (lengthStartOffset + arrayLengthLength);
            for(int i=(arrayLengthLength-1)*8; i >= 0; i-=8){
                dest[lengthStartOffset++] = (byte) (255 & (arrayByteLength >> i));
            }

            return 1 + arrayLengthLength + arrayByteLength; //total length of a UTF-8 field

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
