package fr.uge.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientUpperCaseUDPTimeout {
	public static final int BUFFER_SIZE = 1024;

	private static void usage() {
		System.out.println("Usage : NetcatUDP host port charset");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			usage();
			return;
		}

		var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		var cs = Charset.forName(args[2]);

		var queue = new ArrayBlockingQueue<String>(10);

		try (DatagramChannel dc = DatagramChannel.open()) {
			dc.bind(null);

			try (var scanner = new Scanner(System.in)) {
				while (scanner.hasNext()) {
					var line = scanner.nextLine();

					Thread.ofPlatform().start(() -> {
						try {
							var bufferSend = ByteBuffer.allocate(BUFFER_SIZE);
							bufferSend = cs.encode(line);
							dc.send(bufferSend, server);
							String msg = queue.poll(1, TimeUnit.SECONDS);
							if (msg == null) {
								System.out.println("Le serveur n'a pas repondu");
							} else {
								System.out.println(msg);
							}
						} catch (InterruptedException | IOException e) {
							return;
						}
					});

					Thread.ofPlatform().start(() -> {
						try {
							var receiver = ByteBuffer.allocate(BUFFER_SIZE);
							var sender = (InetSocketAddress) dc.receive(receiver);
							System.out.println("Received " + receiver.position() + " bytes from " + sender);
							receiver.flip();
							var decode = cs.decode(receiver);
							queue.put(decode.toString());
						} catch (IOException | InterruptedException e) {
							return;
						}
					});

				}
			}
		}
	}
}