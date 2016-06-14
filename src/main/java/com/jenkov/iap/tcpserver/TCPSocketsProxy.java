package com.jenkov.iap.tcpserver;

import com.jenkov.iap.mem.MemoryAllocator;
import com.jenkov.iap.mem.MemoryBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jjenkov on 06-05-2016.
 */
public class TCPSocketsProxy {

    // **************************
    // Get new sockets oriented variables.
    // **************************
    private BlockingQueue<SocketChannel> socketQueue    = null;
    private Map<Long, TCPSocket>         socketMap      = new HashMap<>(); //todo replace with faster Long, Object map.
    private List<SocketChannel>          newSocketsTemp = new ArrayList<SocketChannel>();

    private long nextSocketId = 1;



    // **************************
    // Read oriented variables.
    // **************************
    private IMessageReaderFactory messageReaderFactory = null;

    private Selector        readSelector = null;
    private ByteBuffer      readBuffer   = null;

    private TCPSocketPool   tcpObjectPool       = new TCPSocketPool(1024);
    private MemoryAllocator readMemoryAllocator = new MemoryAllocator(new byte[36 * 1024 * 1024], new long[10240],
            (allocator) -> new TCPMessage(allocator));



    // ***************************
    // Write oriented variables.
    // ***************************
    private Selector   writeSelector   = null;
    private ByteBuffer writeByteBuffer = null;

    private List<TCPSocket> nonEmptyToEmptySockets = new ArrayList<>();

    private MemoryAllocator writeMemoryAllocator = new MemoryAllocator(new byte[36 * 1024 * 1024], new long[10240],
            (allocator) -> new TCPMessage(allocator));


    // ***************************
    // TCP Socket close oriented variables.
    // ***************************
    private List<TCPSocket> socketsToBeClosed = new ArrayList<>();



    public TCPSocketsProxy(BlockingQueue<SocketChannel> socketQueue, IMessageReaderFactory messageReaderFactory) throws IOException {
        this.socketQueue          = socketQueue;
        this.messageReaderFactory = messageReaderFactory;

        this.readSelector         = Selector.open();
        this.readBuffer           = ByteBuffer.allocate(1024 * 1024);

        this.writeSelector        = Selector.open();
        this.writeByteBuffer      = ByteBuffer.allocate(1024 * 1024);
    }



    public void checkForNewInboundSockets() throws IOException {

        socketQueue.drainTo(this.newSocketsTemp);

        /*
        if(this.newSocketsTemp.size() > 0){
            System.out.println("New sockets: " + this.newSocketsTemp.size());
        }
        */

        for(int i=0; i<this.newSocketsTemp.size(); i++){
            SocketChannel newSocket = this.newSocketsTemp.get(i);

            newSocket.configureBlocking(false);

            //todo pool some of these objects - IAPMessageReader etc.
            TCPSocket tcpSocket     = this.tcpObjectPool.getTCPSocket();
            tcpSocket.socketId      = this.nextSocketId++;
            tcpSocket.socketChannel = newSocket;
            tcpSocket.messageReader = this.messageReaderFactory.createMessageReader();
            tcpSocket.messageReader.init(this.readMemoryAllocator);

            this.socketMap.put(tcpSocket.socketId, tcpSocket);

            SelectionKey key = newSocket.register(readSelector, SelectionKey.OP_READ);
            key.attach(tcpSocket);

            tcpSocket.readSelectorSelectionKey = key;
            tcpSocket.isRegisteredWithReadSelector = true;
        }

        this.newSocketsTemp.clear();
    }

    public int read(MemoryBlock[] msgDest) throws IOException{
        int selected = 0;

        selected = this.readSelector.selectNow();

        int receivedMessageCount = 0;
        if(selected > 0){
            Iterator<SelectionKey> iterator = this.readSelector.selectedKeys().iterator();

            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();

                if(selectionKey.channel().isOpen()){
                    try{
                        TCPSocket tcpSocket = (TCPSocket) selectionKey.attachment();
                        //todo Add some kind of flow control so that one TCPSocket cannot flood the system with messages.

                        receivedMessageCount += tcpSocket.readMessages(this.readBuffer, msgDest, receivedMessageCount);


                        if(tcpSocket.endOfStreamReached || tcpSocket.state != 0){
                            tcpSocket.readSelectorSelectionKey.cancel();
                            tcpSocket.isRegisteredWithReadSelector = false;

                            this.socketsToBeClosed.add(tcpSocket);

                        }


                    } catch(IOException e){
                        System.out.println("Error reading from socket channel");
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Socket closed!");
                }

                //remove selection key if handled.
                iterator.remove();

                //todo check if we should take one more iteration here, or of the messages[] array is already full!
            }
        }

        return receivedMessageCount;
    }

    /*
     *  Write methods below
     */


    public void writeToSockets() throws IOException {
        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        //registerNonEmptySockets();

        // Select from the Selector.
        selectAndWrite();
    }


    private void selectAndWrite() throws IOException {
        int writeReady = this.writeSelector.selectNow();

        if(writeReady > 0){
            Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator   = selectionKeys.iterator();

            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();

                TCPSocket socket = (TCPSocket) key.attachment();

                socket.writeQueued(this.writeByteBuffer);

                if(socket.isEmpty()){
                    this.nonEmptyToEmptySockets.add(socket);
                    //this.emptyToNonEmptySockets.remove(socket); //necessary?
                }

                keyIterator.remove();
            }

            selectionKeys.clear();
        }
    }



    private void cancelEmptySockets() {
        /*
        if(this.nonEmptyToEmptySockets.size() > 0){
            System.out.println("Canceling socket selector registrations: " + this.nonEmptyToEmptySockets.size());
        };
        */

        //todo could this be optimized if a List was used instead of a Set ?
        if(nonEmptyToEmptySockets.size() == 0) return;


        for(int i=0, n=nonEmptyToEmptySockets.size(); i<n; i++){
            TCPSocket tcpSocket = nonEmptyToEmptySockets.get(i);
            if(tcpSocket.isEmpty()){
                SelectionKey key = tcpSocket.socketChannel.keyFor(this.writeSelector);
                if(key != null){
                    key.cancel();  //todo how can key be null?
                }
                tcpSocket.isRegisteredWithWriteSelector = false;
            }
        }

        nonEmptyToEmptySockets.clear();
    }



    public TCPMessage getWriteMemoryBlock() {
        return (TCPMessage) this.writeMemoryAllocator.getMemoryBlock();
    }

    public TCPSocket getTCPSocket(long socketId) {
        return this.socketMap.get(socketId);
    }

    public void enqueue(TCPSocket tcpSocket, TCPMessage message) throws IOException {
        if(tcpSocket.isEmpty()){
            //attempt to write message immediately instead of first queueing up the message.
            int bytesWrittenDirect = tcpSocket.writeDirect(this.writeByteBuffer, message);

            if(bytesWrittenDirect == message.writeIndex - message.startIndex){ //if full message written
                message.free();
            } else {  //else queue remainder of message.
                if(!tcpSocket.isRegisteredWithWriteSelector){
                    tcpSocket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, tcpSocket);
                    tcpSocket.isRegisteredWithWriteSelector = true;
                }

                tcpSocket.enqueueRest(message, bytesWrittenDirect);
            }

        } else {
            tcpSocket.enqueue(message);
        }

    }


    public List<TCPSocket> getSocketsToBeClosed() {
        return socketsToBeClosed;
    }

    public void cleanupSockets() {
        for(int i=0, n=this.socketsToBeClosed.size(); i < n; i++){
            TCPSocket tcpSocket = this.socketsToBeClosed.get(i);

            //System.out.println("Closing TCPSocket");
            try {
                tcpSocket.closeAndFree();
            } catch (IOException e) {
                System.out.println("Error closing TCPSocket:");
                e.printStackTrace();
            }
        }
        this.socketsToBeClosed.clear();

    }

}
