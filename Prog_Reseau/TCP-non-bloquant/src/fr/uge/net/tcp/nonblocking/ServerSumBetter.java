package fr.uge.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// launch : 
// java fr/../ServerSumBetter 7777
// java -jar ClientSumChrono.jar localhost 7777
// chrono : 3000ms - 4000 ms

public class ServerSumBetter {
	static private class Context {
		private final SelectionKey key;
		private final SocketChannel sc;
		private final ByteBuffer bufferIn = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);
		private final ByteBuffer bufferOut = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);
		private boolean closed = false;

		private Context(SelectionKey key) {
			this.key = key;
			this.sc = (SocketChannel) key.channel();
		}

		/**
		 * Process the content of bufferIn into bufferOut
		 *
		 * The convention is that both buffers are in write-mode before the call to
		 * process and after the call
		 *
		 */

		private void process() {
			if (bufferIn.hasRemaining()) {
				return;
			}
			bufferIn.flip();
			int sum = bufferIn.getInt() + bufferIn.getInt();
			bufferOut.putInt(sum);
			bufferIn.clear();
		}

		/**
		 * Update the interestOps of the key looking only at values of the boolean
		 * closed and of both ByteBuffers.
		 *
		 * The convention is that both buffers are in write-mode before the call to
		 * updateInterestOps and after the call. Also it is assumed that process has
		 * been be called just before updateInterestOps.
		 */

		private void updateInterestOps() {
			if (closed) {
				bufferOut.flip();
				if (bufferOut.hasRemaining()) {
					key.interestOps(SelectionKey.OP_WRITE);
					bufferOut.compact();
					return;
				}
				silentlyClose();
				return;
			}
			
			if (bufferIn.hasRemaining()) {
				key.interestOpsOr(SelectionKey.OP_READ);
			}
			if (bufferOut.hasRemaining()) {
				key.interestOpsOr(SelectionKey.OP_WRITE);
			}
		}

		private void silentlyClose() {
			try {
				sc.close();
			} catch (IOException e) {
				// ignore exception
			}
		}

		/**
		 * Performs the read action on sc
		 *
		 * The convention is that both buffers are in write-mode before the call to
		 * doRead and after the call
		 *
		 * @throws IOException
		 */

		private void doRead() throws IOException {
			int bytesRead = sc.read(bufferIn);
			System.out.println("\t\tbytesRead: " + bytesRead);
			if (bytesRead == -1) {
				closed = true;
			}
			process();
			updateInterestOps();
		}

		/**
		 * Performs the write action on sc
		 *
		 * The convention is that both buffers are in write-mode before the call to
		 * doWrite and after the call
		 *
		 * @throws IOException
		 */

		private void doWrite() throws IOException {
			bufferOut.flip();
			sc.write(bufferOut);
			if (bufferOut.hasRemaining()) {
				logger.warning("Not everything got sent");
				bufferOut.compact();
				return;
			}
			bufferOut.clear();
			updateInterestOps();
		}

	}

	private static final int BUFFER_SIZE = 2 * Integer.BYTES;
	private static final Logger logger = Logger.getLogger(ServerSumBetter.class.getName());

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;

	public ServerSumBetter(int port) throws IOException {
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
		Channel sc = (Channel) key.channel();
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
		new ServerSumBetter(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerSumBetter port");
	}
}