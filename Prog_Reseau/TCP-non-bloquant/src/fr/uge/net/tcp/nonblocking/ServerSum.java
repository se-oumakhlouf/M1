package fr.uge.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.rmi.UnexpectedException;
import java.util.logging.Logger;

// launch :
// java fr.uge.net.tcp.nonblocking ServerSum 7777
// java -jar ClientSum.jar localhost 7777

public class ServerSum {

	private static final int BUFFER_SIZE = 2 * Integer.BYTES;
	private static final Logger logger = Logger.getLogger(ServerSum.class.getName());

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;

	public ServerSum(int port) throws IOException {
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
			} catch (UnexpectedException tunnel) {
				tunnel.getCause();
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
			if (key.isValid() && key.isWritable()) {
				doWrite(key);
			}
			if (key.isValid() && key.isReadable()) {
				doRead(key);
			}
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	private void doAccept(SelectionKey key) throws IOException {
		var serverChannel = (ServerSocketChannel) key.channel();
		var clientChannel = serverChannel.accept();
		if (clientChannel == null) {
			logger.warning("Can not accept this client. Selector gave a bad hint.");
			return;
		}
		clientChannel.configureBlocking(false);
		var buffer = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);
		clientChannel.register(selector, SelectionKey.OP_READ, buffer);
	}

	private void doRead(SelectionKey key) throws IOException {
		var clientChannel = (SocketChannel) key.channel();
		var buffer = (ByteBuffer) key.attachment();

		int bytesRead = clientChannel.read(buffer);
		if (bytesRead == -1) {
			silentlyClose(key);
			return;
		}

		if (buffer.hasRemaining()) {
			logger.info("Missing Integers, buffer is not full. Remaining: " + buffer.remaining());
			return;
		}

		buffer.flip();
		int sum = buffer.getInt() + buffer.getInt();

		buffer.clear();
		buffer.putInt(sum);
		buffer.flip();

		key.interestOps(SelectionKey.OP_WRITE);
	}

	private void doWrite(SelectionKey key) throws IOException {
		var clientChannel = (SocketChannel) key.channel();
		var buffer = (ByteBuffer) key.attachment();
		clientChannel.write(buffer);
		if (buffer.hasRemaining()) {
			logger.warning("Not everything got sent");
			return;
		}
		buffer.clear();
		key.interestOps(SelectionKey.OP_READ);
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
		new ServerSum(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerSumOneShot port");
	}
}