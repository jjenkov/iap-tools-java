package com.jenkov.iap.ion.read;

/**
 * An IIonObjectReaderConfigurator can configure the individual field readers of an IonObjectReader. An implementation
 * of this interface is passed to the constructor of the IonObjectReader. The IonObjectReader then calls the implementation
 * of this interface to obtain configuration for each field in the class the IonObjectReader is targeted at.
 *
 */
public interface IIonObjectReaderConfigurator {

    public void configure(IonFieldReaderConfiguration config);

}
