package fr.uge.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerEcho {
	static private class Context {
		private final SelectionKey key;
		private final SocketChannel sc;
		private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		private boolean closed = false;

		private Context(SelectionKey key) {
			this.key = key;
			this.sc = (SocketChannel) key.channel();
		}

		/**
		 * Update the interestOps of the key looking only at values of the boolean
		 * closed and the ByteBuffer buffer.
		 *
		 * The convention is that buff is in write-mode.
		 */
		private void updateInterestOps() {
			if (!closed && buffer.hasRemaining()) {
				key.interestOpsOr(SelectionKey.OP_READ);
			} else {
				key.interestOpsAnd(~SelectionKey.OP_READ);
			}

			if (buffer.position() > 0) {
				key.interestOpsOr(SelectionKey.OP_WRITE);
			} else {
				key.interestOpsAnd(~SelectionKey.OP_WRITE);
			}
		}

		/**
		 * Performs the read action on sc
		 *
		 * The convention is that buffer is in write-mode before calling doRead and is
		 * in write-mode after calling doRead
		 *
		 * @throws IOException
		 */
		private void doRead() throws IOException {
      int bytesRead = sc.read(buffer);
      if (bytesRead == -1) {
          closed = true;
      }
      updateInterestOps();
		}

		/**
		 * Performs the write action on sc
		 *
		 * The convention is that buffer is in write-mode before calling doWrite and is
		 * in write-mode after calling doWrite
		 *
		 * @throws IOException
		 */
		private void doWrite() throws IOException {
      buffer.flip();
      sc.write(buffer);
      buffer.compact();
      updateInterestOps();
		}

		private void silentlyClose() {
			try {
				sc.close();
			} catch (IOException e) {
				// ignore exception
			}
		}
	}

	private static final int BUFFER_SIZE = 1_024;
	private static final Logger logger = Logger.getLogger(ServerEcho.class.getName());

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;

	public ServerEcho(int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
	}

	public void launch() throws IOException {
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (!Thread.interrupted()) {
			Helpers.printKeys(selector); // for debug
			System.out.println("Starting select");
			try {
				selector.select(this::treatKey);
			} catch (UncheckedIOException tunneled) {
				throw tunneled.getCause();
			}
			System.out.println("Select finished");
		}
	}

	private void treatKey(SelectionKey key) {
		Helpers.printSelectedKey(key); // for debug
		try {
			if (key.isValid() && key.isAcceptable()) {
				doAccept(key);
			}
		} catch (IOException ioe) {
			// lambda call in select requires to tunnel IOException
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
    var serverChannel = (ServerSocketChannel) key.channel();
    var clientChannel = serverChannel.accept();
    if (clientChannel == null) {
        return;
    }
    clientChannel.configureBlocking(false);
    var clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
    clientKey.attach(new Context(clientKey));
	}

	private void silentlyClose(SelectionKey key) {
		var sc = (Channel) key.channel();
		try {
			sc.close();
		} catch (IOException e) {
			// ignore exception
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		if (args.length != 1) {
			usage();
			return;
		}
		new ServerEcho(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerEcho port");
	}
}