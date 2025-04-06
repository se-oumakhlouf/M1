package fr.uge.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerChat {
    private static final int BUFFER_SIZE = 1024;
    private static final Logger logger = Logger.getLogger(ServerChat.class.getName());

    static private class Context {
        private final SelectionKey key;
        private final SocketChannel sc;
        private final ByteBuffer bufferIn = ByteBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer bufferOut = ByteBuffer.allocate(BUFFER_SIZE);
        private final ArrayDeque<Message> queue = new ArrayDeque<>();
        private final ServerChat server;
        private final MessageReader messageReader = new MessageReader();
        private boolean closed = false;

        private Context(ServerChat server, SelectionKey key) {
            this.key = key;
            this.sc = (SocketChannel) key.channel();
            this.server = server;
        }

        private void processIn() {
            while (true) {
                Reader.ProcessStatus status = messageReader.process(bufferIn);
                switch (status) {
                    case DONE:
                        Message msg = messageReader.get();
                        server.broadcast(msg);
                        messageReader.reset();
                        break;
                    case REFILL:
                        return;
                    case ERROR:
                        silentlyClose();
                        return;
                }
            }
        }

        public void queueMessage(Message msg) {
            queue.add(msg);
            processOut();
            updateInterestOps();
        }

        private void processOut() {
            while (!queue.isEmpty()) {
                Message msg = queue.peek();
                ByteBuffer encoded = msgToBuffer(msg);
                
                if (bufferOut.remaining() < encoded.remaining()) {
                    return;
                }
                
                bufferOut.put(encoded);
                queue.remove();
            }
        }

        private ByteBuffer msgToBuffer(Message msg) {
            ByteBuffer login = StandardCharsets.UTF_8.encode(msg.login());
            ByteBuffer text = StandardCharsets.UTF_8.encode(msg.text());
            
            ByteBuffer buffer = ByteBuffer.allocate(4 + login.remaining() + 4 + text.remaining());
            buffer.putInt(login.remaining()).put(login)
                  .putInt(text.remaining()).put(text);
            buffer.flip();
            return buffer;
        }

        private void updateInterestOps() {
            int interestOps = 0;
            if (!closed && bufferIn.hasRemaining()) {
                interestOps |= SelectionKey.OP_READ;
            }
            if (bufferOut.position() > 0 || !queue.isEmpty()) {
                interestOps |= SelectionKey.OP_WRITE;
            }
            if (interestOps == 0) {
                silentlyClose();
                return;
            }
            key.interestOps(interestOps);
        }

        private void silentlyClose() {
            try {
                sc.close();
            } catch (IOException e) {
                // ignore exception
            }
        }

        private void doRead() throws IOException {
            if (sc.read(bufferIn) == -1) {
                closed = true;
            }
            processIn();
            updateInterestOps();
        }

        private void doWrite() throws IOException {
            bufferOut.flip();
            sc.write(bufferOut);
            bufferOut.compact();
            processOut();
            updateInterestOps();
        }
    }

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public ServerChat(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        selector = Selector.open();
    }

    public void launch() throws IOException {
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (!Thread.interrupted()) {
            try {
                selector.select(this::treatKey);
            } catch (UncheckedIOException tunneled) {
                throw tunneled.getCause();
            }
        }
    }

    private void treatKey(SelectionKey key) {
        try {
            if (key.isValid() && key.isAcceptable()) {
                doAccept(key);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        
        try {
            if (key.isValid() && key.isWritable()) {
                ((Context) key.attachment()).doWrite();
            }
            if (key.isValid() && key.isReadable()) {
                ((Context) key.attachment()).doRead();
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Connection closed with client due to IOException", e);
            silentlyClose(key);
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        if (sc == null) {
            return;
        }
        sc.configureBlocking(false);
        SelectionKey clientKey = sc.register(selector, SelectionKey.OP_READ);
        clientKey.attach(new Context(this, clientKey));
    }

    private void silentlyClose(SelectionKey key) {
        Channel sc = (Channel) key.channel();
        try {
            sc.close();
        } catch (IOException e) {
            // ignore exception
        }
    }

    private void broadcast(Message msg) {
        selector.keys().forEach(key -> {
            Channel channel = key.channel();
            if (channel != serverSocketChannel && key.attachment() != null) {
                Context ctx = (Context) key.attachment();
                ctx.queueMessage(msg);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: ServerChat port");
            return;
        }
        new ServerChat(Integer.parseInt(args[0])).launch();
    }
}