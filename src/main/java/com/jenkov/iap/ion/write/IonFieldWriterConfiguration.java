package com.jenkov.iap.ion.write;

import java.lang.reflect.Field;

/**
 * A configuration for a single IIonFieldWriter (used internally by the IonObjectWriter).
 *
 * An IIonObjectWriterConfigurator can read the <code>field</code> field to see what field (field writer)
 * this configuration is for.
 *
 * The fieldName of the field is stored in the <code>fieldName</code> field (but is also accessible
 * via field.getName()).
 *
 * The <code>include</code> field defaults to true, but can be set to false. The IonObjectWriter will then not
 * include a field writer for this field, meaning that field will not be included in the written ION data.
 *
 * The <code>alias</code> field can be used to give the field a different fieldName in the written ION data than the
 * field has in the Java object.
 */
public class IonFieldWriterConfiguration {
    public Field   field    = null;
    public String fieldName = null;


    public boolean include = true;
    public String  alias   = null;


}
