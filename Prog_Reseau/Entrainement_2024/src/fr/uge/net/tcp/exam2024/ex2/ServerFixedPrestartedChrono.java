package fr.uge.net.tcp.exam2024.ex2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ServerFixedPrestartedChrono {
	private static final Logger logger = Logger
			.getLogger(fr.uge.net.tcp.exam2024.ex2.ServerFixedPrestartedChrono.class.getName());
	private final ServerSocketChannel serverSocketChannel;
	private final int nbClients;

	private volatile long globalSum = 0;
	private volatile long globalCount = 0;

	public ServerFixedPrestartedChrono(int port, int nbClients) throws IOException {
		this.serverSocketChannel = ServerSocketChannel.open();
		this.nbClients = nbClients;
		serverSocketChannel.bind(new InetSocketAddress(port));
		logger.info(this.getClass().getName() + " starts on port " + port);
	}

	public void launch() throws IOException {
		var threads = new ArrayList<Thread>();
		for (int i = 0; i < nbClients; i++) {
			var thread = Thread.ofPlatform().start(() -> {
				while (!Thread.interrupted()) {
					try {
						SocketChannel client = serverSocketChannel.accept();
						logger.info("Connection accepted from " + client.getRemoteAddress());
						serve(client);
					} catch (IOException ioe) {
						logger.warning("Error handling client : " + ioe.getCause());
					}
				}
			});
			threads.add(thread);
		}

		for (var t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}
		serverSocketChannel.close();
	}

	private void serve(SocketChannel client) throws IOException {
		var buffer = ByteBuffer.allocate(Long.BYTES);
		var response = ByteBuffer.allocate(Long.BYTES * 3);

		long clientCount = 0;
		long clientSum = 0;

		while (true) {
			buffer.clear();
			response.clear();
			while (buffer.hasRemaining()) {
				if (client.read(buffer) == -1) {
					logger.info("Client closed the connection");
					return;
				}
			}
			buffer.flip();
			
			var clientTime = buffer.getLong();
			var now = System.currentTimeMillis();
			var travelTime = now - clientTime;

			globalCount++;
			globalSum += travelTime;

			clientCount++;
			clientSum += travelTime;

			response.putLong(travelTime);
			response.putLong(clientSum / clientCount);
			response.putLong(globalSum / globalCount);
			response.flip();
			client.write(response);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("usage: java ServerFixedPrestartedChrono port nbClients");
			return;
		}
		var server = new ServerFixedPrestartedChrono(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.launch();
	}
}