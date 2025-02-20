package fr.uge.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnDemandConcurrentLongSumServer {

	private static final Logger logger = Logger.getLogger(OnDemandConcurrentLongSumServer.class.getName());
	private final ServerSocketChannel serverSocketChannel;

	public static void usage() {
		System.out.println("Usage: OnDemandConcurrentLongSumServer port");
	}
	
	public OnDemandConcurrentLongSumServer(int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		logger.info(this.getClass().getName() + " starts on port " + port);
	}

	/**
	 * Iterative server main loop
	 *
	 * @throws IOException
	 */

	public void launch() throws IOException {
		logger.info("Server started");
		while (!Thread.interrupted()) {
			SocketChannel client = serverSocketChannel.accept();
			logger.info("Connection accepted from " + client.getRemoteAddress());
			Thread.ofPlatform().start(() -> {
				try {
					serve(client);
				} catch (IOException ioe) {
					logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
				} finally {
					silentlyClose(client);
				}
			});
		}
	}

	/**
	 * Treat the connection sc applying the protocol. All IOException are thrown
	 *
	 * @param sc
	 * @throws IOException
	 */
	private void serve(SocketChannel sc) throws IOException {
		var longBuffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
		var quantityBuffer = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN);
		var resultBuffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);

		while (sc.isOpen()) {

			quantityBuffer.clear();
			if (!readFully(sc, quantityBuffer)) {
				logger.warning("Client disconnected before sending a new request");
				return;
			}
			int quantity = quantityBuffer.flip().getInt();
			long sum = 0;

			for (int i = 0; i < quantity; i++) {
				longBuffer.clear();
				if (!readFully(sc, longBuffer)) {
					logger.warning("Client disconnected before sending all longs");
					return;
				}
				sum += longBuffer.flip().getLong();
			}

			resultBuffer.clear();
			resultBuffer.putLong(sum);
			resultBuffer.flip();
			sc.write(resultBuffer);

		}

	}

	/**
	 * Close a SocketChannel while ignoring IOExecption
	 *
	 * @param sc
	 */
	private void silentlyClose(Closeable sc) {
		if (sc != null) {
			try {
				sc.close();
			} catch (IOException e) {
				// Do nothing
			}
		}
	}

	static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
		while (buffer.hasRemaining()) {
			if (sc.read(buffer) == -1) {
				logger.info("Input stream closed");
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		if (args.length != 1) {
			usage();
			return;
		}
		var server = new OnDemandConcurrentLongSumServer(Integer.parseInt(args[0]));
		server.launch();
	}
}