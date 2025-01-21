package fr.uge.net.udp;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientUpperCaseUDPFile {
	private final static Charset UTF8 = StandardCharsets.UTF_8;
	private final static int BUFFER_SIZE = 1024;

	private static void usage() {
		System.out.println("Usage : ClientUpperCaseUDPFile in-filename out-filename timeout host port ");
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 5) {
			usage();
			return;
		}

		var inFilename = args[0];
		var outFilename = args[1];
		var timeout = Integer.parseInt(args[2]);
		var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));

		// Read all lines of inFilename opened in UTF-8
		var lines = Files.readAllLines(Path.of(inFilename), UTF8);
		var upperCaseLines = new ArrayList<String>();

		var queueSend = new ArrayBlockingQueue<String>(10);
		var queueReceive = new ArrayBlockingQueue<String>(10);

		try (DatagramChannel dc = DatagramChannel.open()) {
			dc.bind(null);

			var sender = Thread.ofPlatform().start(() -> {
				try {
					for (var line : lines) {
						queueSend.put(line);
						var buffer = ByteBuffer.allocate(BUFFER_SIZE);
						String toSend;
						while ((toSend = queueSend.poll(timeout, TimeUnit.MILLISECONDS)) == null) {
							System.out.println("Timeout Reached");
							System.out.println("Retrying...");
							queueSend.put(line);
						}
						buffer.put(UTF8.encode(toSend));
						buffer.flip();
						dc.send(buffer, server);
						buffer.clear();
						System.out.println("Sent : " + toSend);
					}
				} catch (InterruptedException | IOException e) {
					System.out.println("Error in sender thread");
				}
			});

			var receiver = Thread.ofPlatform().start(() -> {
				try {
					var buffer = ByteBuffer.allocate(BUFFER_SIZE);
					while (!Thread.currentThread().isInterrupted()) {
						if (dc.receive(buffer) != null) {
							buffer.flip();
							var response = UTF8.decode(buffer).toString();
							buffer.clear();
							System.out.println("Received : " + response);
							queueReceive.put(response);
						}
					}
				} catch (IOException | InterruptedException e) {
					System.out.println("Error in receiver thread");
				}
			});

			for (var line : lines) {
				var response = queueReceive.poll(timeout, TimeUnit.MILLISECONDS);
				if (response == null) {
					System.out.println("No response for line -> " + line);
				} else {
					System.out.println("Adding line : " + line + " -> " + response);
					upperCaseLines.add(response);
				}
			}

			receiver.interrupt();
			receiver.join();
			sender.join();
		}
		// Write upperCaseLines to outFilename in UTF-8
		Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
	}
}