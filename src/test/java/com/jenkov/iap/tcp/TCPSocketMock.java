package com.jenkov.iap.tcp;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 10-06-2016.
 */
public class TCPSocketMock extends TCPSocket {

    public List<DataBlock> dataBlocks = new ArrayList<>();
    public int dataBlockIndex = 0;

    public TCPSocketMock(TCPSocketPool tcpSocketPool) {
        super(tcpSocketPool);
    }


    @Override
    protected int doRead(ByteBuffer destinationBuffer) throws IOException {
        if(this.dataBlockIndex < this.dataBlocks.size()){
            DataBlock nextDataBlock = this.dataBlocks.get(this.dataBlockIndex);
            this.dataBlockIndex++;

            destinationBuffer.put(nextDataBlock.data, nextDataBlock.offset, nextDataBlock.length);

            return nextDataBlock.length;
        }

        return 0;
    }

}
