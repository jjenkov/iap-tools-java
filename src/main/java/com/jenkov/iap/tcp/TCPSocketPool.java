package com.jenkov.iap.tcp;

/**
 * Created by jjenkov on 01-02-2016.
 */
public class TCPSocketPool {

    private TCPSocket[] pooledSockets = null;
    private int pooledSocketCount = 0;

    public TCPSocketPool(int maxPooledSockets) {
        this.pooledSockets  = new TCPSocket[maxPooledSockets];
    }


    public TCPSocket getTCPSocket(){
        if(this.pooledSocketCount > 0){
            this.pooledSocketCount--;
            return this.pooledSockets[this.pooledSocketCount];
        }
        return new TCPSocket(this);
    }


    public void free(TCPSocket socket){
        //pool message if space
        if(this.pooledSocketCount < this.pooledSockets.length){
            this.pooledSockets[this.pooledSocketCount] = socket;
            this.pooledSocketCount++;
        }
    }


}
