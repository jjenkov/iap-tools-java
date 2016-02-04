package com.jenkov.iap.ion.read;

import java.lang.reflect.Field;

/**
 * A configuration for a single IIonFieldReader (used internally by the IonObjectReader).
 *
 * An IIonObjectReaderConfigurator can read the <code>field</code> field to see what field (field reader)
 * this configuration is for.
 *
 * The fieldName of the field is stored in the <code>fieldName</code> field (but is also accessible
 * via field.getName()).
 *
 * The <code>include</code> field defaults to true, but can be set to false. The IonObjectReader will then not
 * include read values for this field from the ION data, even if that field is present.
 *
 * The <code>alias</code> field can be used if the field has a different fieldName in the ION data than the
 * field has in the Java object.
 */
public class IonFieldReaderConfiguration {
    public Field   field   = null;
    public String fieldName = null;


    public boolean include = true;
    public String  alias   = null;


}
