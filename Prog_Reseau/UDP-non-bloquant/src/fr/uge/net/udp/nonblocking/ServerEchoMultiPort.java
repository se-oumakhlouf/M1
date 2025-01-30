package fr.uge.net.udp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Logger;

// launch:
// java fr.uge.udp.nonblocking.ServerEchoMultiPort 7000 7100
// java -jar ClientTestEchoMultiPort.jar localhost 7000 7100

// expected: No error Found

public class ServerEchoMultiPort {
	private static final Logger logger = Logger.getLogger(ServerEchoMultiPort.class.getName());

	private final Selector selector;

	private final static int BUFFER_SIZE = 1024;
	private int port;

	private static class Context {
		private InetSocketAddress sender;
		private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	}

	public ServerEchoMultiPort(int firstPort, int lastPort) throws IOException {
		selector = Selector.open();

		for (int port = firstPort; port <= lastPort; port++) {
			DatagramChannel dc = DatagramChannel.open();
			dc.bind(new InetSocketAddress(port));
			dc.configureBlocking(false);
			dc.register(selector, SelectionKey.OP_READ, new Context());
			logger.info("Listening on port " + port);
		}
	}

	public void serve() throws IOException {
		logger.info("ServerEchoMultiPort started on port " + port);
		while (!Thread.interrupted()) {
			try {
				selector.select(this::treakKey);
			} catch (UncheckedIOException tunnel) {
				tunnel.getCause();
			}
		}
	}

	private void treakKey(SelectionKey key) {
		try {
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

	private void doRead(SelectionKey key) throws IOException {
		var channel = (DatagramChannel) key.channel();
		var context = (Context) key.attachment();
		context.buffer.clear();
		context.sender = (InetSocketAddress) channel.receive(context.buffer);
		if (context.sender == null) {
			logger.warning("failed to receive");
			return;
		}
		context.buffer.flip();
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private void doWrite(SelectionKey key) throws IOException {
		var channel = (DatagramChannel) key.channel();
		var context = (Context) key.attachment();
		channel.send(context.buffer, context.sender);
		if (context.buffer.hasRemaining()) {
			logger.warning("failed to send");
			return;
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	public static void usage() {
		System.out.println("Usage : ServerEchoMultiPort portmin portmax");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			usage();
			return;
		}

		int start = Integer.parseInt(args[0]);
		int end = Integer.parseInt(args[1]);

		new ServerEchoMultiPort(start, end).serve();
	}

}
