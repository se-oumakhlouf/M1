package fr.uge.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class FixedPrestartedLongSumServerWithTimeout {

	private static final Logger logger = Logger.getLogger(FixedPrestartedLongSumServerWithTimeout.class.getName());
	private final ServerSocketChannel serverSocketChannel;
	private final int maxClient;
	private final int timeout;
	private final Map<Thread, ThreadData> threadDataMap = new HashMap<>();
	private volatile boolean isAcceptingClients = true;
	private volatile boolean isRunning = true;

	public FixedPrestartedLongSumServerWithTimeout(int port, int maxConcurrentThread, int timeout) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		logger.info(this.getClass().getName() + " starts on port " + port);
		maxClient = maxConcurrentThread;
		this.timeout = timeout;
	}

	public static void usage() {
		System.out.println("Usage: FixedPrestartedLongSumServerWithTimeout port maxThread timeout");
	}

	public static void commands() {
		System.out.println("INFO\t\t -> show the number of clients connected to the server");
		System.out.println("SHUTDOWN\t\t -> wait for the current clients to end, then close the server");
		System.out.println("SHUTDOWNNOW\t\t	-> shutdown the server immediatly. Connection with all clients will be closed");
	}

	class ThreadData {
		private SocketChannel sc;
		private long lastActive = System.currentTimeMillis();
		private final ReentrantLock lock = new ReentrantLock();

		@Override
		public String toString() {
			var scString = sc == null ? "null" : "not null";
			return "{" + scString + ", " + lastActive + "}";
		}

		void setSocketChannel(SocketChannel client) {
			lock.lock();
			try {
				sc = client;
				tick();
			} finally {
				lock.unlock();
			}
		}

		void tick() {
			lock.lock();
			try {
				lastActive = System.currentTimeMillis();
			} finally {
				lock.unlock();
			}
		}

		void closeIfInactive(int timeout) {
			lock.lock();
			try {
				if (sc == null) {
					return;
				}
				var now = System.currentTimeMillis();
				var diff = now - lastActive;
				if (diff >= timeout) {
					System.out.println("inactif");
					close();
				}
			} finally {
				lock.unlock();
			}

		}

		void close() {
			lock.lock();
			try {
				if (sc == null) {
					return;
				}
				if (sc.isOpen()) {
					sc.close();
				}
				sc = null;
			} catch (IOException e) {
				//
			} finally {
				lock.unlock();
			}
		}

	}

	/**
	 * Iterative server main loop
	 *
	 * @throws IOException
	 */

	public void launch() throws IOException {
		logger.info("Server started");

		var threads = new ArrayList<Thread>();

		for (int i = 0; i < maxClient; i++) {

			var thread = Thread.ofPlatform().start(() -> {
				var threadData = new ThreadData();
				threadDataMap.put(Thread.currentThread(), threadData);

				while (isRunning) {
					if (!isAcceptingClients) {
						continue;
					}
					try {
						SocketChannel client = serverSocketChannel.accept();
						if (isAcceptingClients == false) {
							System.out.println("Accepté alors que fermé");
							// il y a donc maxClient clients qui vont être accepté dès le début
						}
						threadData.setSocketChannel(client);
						System.out.println("Map: " + threadDataMap);
						logger.info("Connection accepted from " + client.getRemoteAddress());
						serve(client, threadData);
						silentlyClose(client);
					} catch (IOException ioe) {
						logger.severe("Connection terminated with client by IOException");
						if (!isRunning) {
							break;
						}
					}
				}

			});

			threads.add(thread);

		}

		Thread timeoutThread = Thread.ofPlatform().start(() -> {
			while (isRunning) {
				try {
					Thread.sleep(timeout);
					for (var value : threadDataMap.values()) {
						value.closeIfInactive(timeout);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		Thread consoleThread = Thread.ofPlatform().start(() -> {
			try (var scanner = new Scanner(System.in)) {
				while (isRunning) {
					if (!scanner.hasNextLine()) {
						continue;
					}
					var line = scanner.nextLine();
					switch (line) {
						case "INFO":
							var activeClients = threadDataMap.values().stream().filter(threadData -> threadData.sc != null).count();
							logger.info("Active clients: " + activeClients);
							break;
						case "SHUTDOWN":
							logger.info("Server waiting for active clients to close");
							isAcceptingClients = false;
							break;
						case "SHUTDOWNNOW":
							logger.info("Server is closing now");
							isRunning = false;
							isAcceptingClients = false;
							for (var value : threadDataMap.values()) {
								value.close();
							}
							silentlyClose(serverSocketChannel);
							return;
						default:
							logger.info("Unknown command: " + line);
							commands();
							break;
					}
				}
			}
		});

		try {
			for (var thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (!isAcceptingClients) {
				silentlyClose(serverSocketChannel);
			}
			timeoutThread.interrupt();
			consoleThread.interrupt();
		}

	}

	/**
	 * Treat the connection sc applying the protocol. All IOException are thrown
	 *
	 * @param sc
	 * @throws IOException
	 */
	private void serve(SocketChannel sc, ThreadData threadData) throws IOException {
		var longBuffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
		var quantityBuffer = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN);
		var resultBuffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
		while (sc.isOpen()) {

			quantityBuffer.clear();
			if (!readFully(sc, quantityBuffer)) {
				logger.warning("Client disconnected before sending a new request");
				return;
			}
			threadData.tick();
			int quantity = quantityBuffer.flip().getInt();
			long sum = 0;

			for (int i = 0; i < quantity; i++) {
				longBuffer.clear();
				if (!readFully(sc, longBuffer)) {
					logger.warning("Client disconnected before sending all longs");
					return;
				}
				threadData.tick();
				sum += longBuffer.flip().getLong();
			}

			resultBuffer.clear();
			resultBuffer.putLong(sum);
			resultBuffer.flip();
			sc.write(resultBuffer);
			threadData.tick();

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
				if (sc != null) {
					sc.close();
					sc = null;
				}
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
		if (args.length != 3) {
			usage();
			return;
		}
		var server = new FixedPrestartedLongSumServerWithTimeout(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]));
		server.launch();
	}
}