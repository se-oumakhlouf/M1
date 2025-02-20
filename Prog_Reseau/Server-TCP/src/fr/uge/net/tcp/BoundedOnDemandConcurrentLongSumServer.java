package fr.uge.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoundedOnDemandConcurrentLongSumServer {

	private static final Logger logger = Logger.getLogger(BoundedOnDemandConcurrentLongSumServer.class.getName());
	private final ServerSocketChannel serverSocketChannel;

	private final Semaphore semaphore;

	public BoundedOnDemandConcurrentLongSumServer(int port, int maxConcurrentThread) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		logger.info(this.getClass().getName() + " starts on port " + port);
		semaphore = new Semaphore(maxConcurrentThread);
	}

	public static void usage() {
		System.out.println("Usage: BoundedOnDemandConcurrentLongSumServer port maxConcurrentThread");
	}

	/**
	 * Iterative server main loop
	 *
	 * @throws IOException
	 */

	public void launch() throws IOException {
		logger.info("Server started");
		try {

			while (!Thread.interrupted()) {

				SocketChannel client = serverSocketChannel.accept();
				logger.info("Connection accepted from " + client.getRemoteAddress());

				try {
					semaphore.acquire();

					Thread.ofPlatform().start(() -> {
						try {
							serve(client);
						} catch (IOException ioe) {
							logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
						} finally {
							semaphore.release();
							silentlyClose(client);
						}
					});

				} catch (InterruptedException e) {
					logger.info("Server interrupted");
					Thread.currentThread().interrupt();
					break;
				}

			}

		} finally {
			silentlyClose(serverSocketChannel);
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
		if (args.length != 2) {
			usage();
			return;
		}
		var server = new BoundedOnDemandConcurrentLongSumServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.launch();
	}
}