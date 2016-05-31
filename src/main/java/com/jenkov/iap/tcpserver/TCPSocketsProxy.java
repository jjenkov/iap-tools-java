package com.jenkov.iap.tcpserver;

import com.jenkov.iap.mem.MemoryAllocator;

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

    private TCPSocketPool   tcpObjectPool       = new TCPSocketPool(16 * 1024, 1024);
    private MemoryAllocator readMemoryAllocator = new MemoryAllocator(new byte[36 * 1024 * 1024], new long[10240],
            (allocator) -> new TCPMessage(allocator));

    private Object[]        readTempIAPMessages = new Object[64];


    // ***************************
    // Write oriented variables.
    // ***************************
    private Selector   writeSelector   = null;
    private ByteBuffer writeByteBuffer = null;

    private Set<TCPSocket> emptyToNonEmptySockets = new HashSet<>();
    private Set<TCPSocket> nonEmptyToEmptySockets = new HashSet<>();

    private MemoryAllocator writeMemoryAllocator = new MemoryAllocator(new byte[36 * 1024 * 1024], new long[10240],
            (allocator) -> new TCPMessage(allocator));




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

            //todo pool some of these objects - TCPSocket, TCPMessageWriter etc.
            TCPSocket tcpSocket     = this.tcpObjectPool.getTCPSocket();
            tcpSocket.socketId      = this.nextSocketId++;
            tcpSocket.socketChannel = newSocket;
            tcpSocket.messageReader = this.messageReaderFactory.createMessageReader();
            tcpSocket.messageReader.init(this.readMemoryAllocator);
            tcpSocket.proxy         = this;

            //tcpSocket.messageWriter = new TCPMessageWriter(); //todo get from TypeAllocator ?

            this.socketMap.put(tcpSocket.socketId, tcpSocket);

            SelectionKey key = newSocket.register(readSelector, SelectionKey.OP_READ);
            key.attach(tcpSocket);
        }

        this.newSocketsTemp.clear();
    }

    public int read(Object[] msgDest) throws IOException{
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

                        //todo fix reading exception when connection closed - end of stream reached. Does this error occur anymore?
                        this.readBuffer.clear();
                        int totalBytesRead = tcpSocket.read(this.readBuffer);

                        if(totalBytesRead > 0){
                            this.readBuffer.flip();

                            int messageCount = tcpSocket.messageReader.read(tcpSocket, this.readBuffer, this.readTempIAPMessages, 0);

                            for(int i=0; i<messageCount; i++){
                                TCPMessage tcpMessage = (TCPMessage) this.readTempIAPMessages[i];
                                tcpMessage.socketId    = tcpSocket.socketId;
                                tcpMessage.tcpSocket   = tcpSocket;

                                //todo if more than this.tempMessages.length messages are received, this will result in an IndexOutOfBoundsException.
                                msgDest                 [receivedMessageCount] = tcpMessage;
                                receivedMessageCount++;
                            }
                        }

                        if(tcpSocket.endOfStreamReached){
                            selectionKey.attach(null);

                            //todo clear all waiting messages in both message reader and message writer. In fact, call dispose() on them (not implemented yet).
                            tcpSocket.messageReader = null;
                            //tcpSocket.messageWriter = null;
                            tcpSocket.socketChannel = null;  //todo check if it should be closed? or if it is already closed?

                            selectionKey.cancel();
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


    public void writeToSockets(Object[] outMessages, int messageCount) throws IOException {

        // Take all new messages from outboundMessageQueue
        //takeNewOutboundMessages();
        takeNewOutboundMessagesBatch(outMessages, messageCount);

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        selectAndWrite();
    }


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
                    this.emptyToNonEmptySockets.remove(socket); //necessary?
                }

                keyIterator.remove();
            }

            selectionKeys.clear();
        }
    }


    private void registerNonEmptySockets() throws ClosedChannelException {

        /*
        if(this.emptyToNonEmptySockets.size() > 0){
            System.out.println("Registering non-empty sockets: " + this.emptyToNonEmptySockets.size());
        };
        */

        for(TCPSocket tcpSocket : emptyToNonEmptySockets){
            tcpSocket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, tcpSocket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {

        /*
        if(this.nonEmptyToEmptySockets.size() > 0){
            System.out.println("Canceling socket selector registrations: " + this.nonEmptyToEmptySockets.size());
        };
        */

        //todo could this be optimized if a List was used instead of a Set ?
        if(nonEmptyToEmptySockets.size() == 0) return;

        for(TCPSocket socket : nonEmptyToEmptySockets){
            SelectionKey key = socket.socketChannel.keyFor(this.writeSelector);
            if(key != null){
                key.cancel();  //todo how can key be null?
            }
            socket.isRegisteredWithWriteSelector = false;
        }
        nonEmptyToEmptySockets.clear();
    }


    /**
     * Batch version of takeNewOutboundMessages
     */
    private void takeNewOutboundMessagesBatch(Object[] tempMessages, int messageCount) throws IOException {
        for(int i=0; i<messageCount; i++){

            TCPMessage outMessage =  (TCPMessage) tempMessages[i];

            TCPSocket socket = this.socketMap.get(outMessage.socketId);

            if(socket != null){
                if(socket.isEmpty()){
                    socket.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                } else{
                    socket.enqueue(outMessage);
                }
            }
            //outMessage.free(); //todo wrong time to free the message!
        }
    }

    public TCPMessage getWriteMemoryBlock() {
        return (TCPMessage) this.writeMemoryAllocator.getMemoryBlock();
    }



    public void enqueue(TCPSocket tcpSocket, TCPMessage message) throws IOException {
        if(tcpSocket.isEmpty()){

            //attempt direct write instead of first queueing up the message.
            int bytesWrittenDirect = tcpSocket.writeDirect(this.writeByteBuffer, message);

            if(bytesWrittenDirect == message.writeIndex - message.startIndex){ //if full message written
                message.free();
            } else {  //else queue remainder of message.
                this.nonEmptyToEmptySockets.remove(tcpSocket);

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

}
