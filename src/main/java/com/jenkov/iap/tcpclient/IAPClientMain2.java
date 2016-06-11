package com.jenkov.iap.tcpclient;

import java.io.IOException;

/**
 * Created by jjenkov on 27-10-2015.
 */
public class IAPClientMain2 {


    public static void main(String[] args) throws IOException {
        System.out.println("Running IAP Client 2 - Using IapTcpClient");

        //int iterationsPerStatusMessage = 1000_000;
        int iterationsPerStatusMessage = 10_000;
        if(args.length > 0){
            iterationsPerStatusMessage = Integer.parseInt(args[0]);
        }
        System.out.println("iterationsPerStatusMessage = " + iterationsPerStatusMessage);

        int messageBatchSize = 1;
        if(args.length > 1){
            messageBatchSize = Integer.parseInt(args[1]);
        }
        System.out.println("messageBatchSize = " + messageBatchSize);

        IAPClient2 iapClient = new IAPClient2(iterationsPerStatusMessage, messageBatchSize);

        iapClient.run();

    }

}
