package com.jenkov.iap.tcpserver;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class IAPMessageReaderFactory implements IMessageReaderFactory {

    @Override
    public IMessageReader createMessageReader() {
        return new IAPMessageReader();
    }
}
