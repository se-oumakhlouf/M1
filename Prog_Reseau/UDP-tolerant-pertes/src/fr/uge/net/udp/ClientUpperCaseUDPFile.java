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

		// TODO

		var queue = new ArrayBlockingQueue<String>(10);

		try (DatagramChannel dc = DatagramChannel.open()) {

			dc.bind(null);

			for (var line : lines) {

				Thread.ofPlatform().start(() -> {
					try {
						var buffer = UTF8.encode(line);
						dc.send(buffer, server);
						String msg;
						while ((msg = queue.poll(timeout, TimeUnit.MILLISECONDS)) == null) {
							System.out.println("Time out");
							System.out.println("Sending request...");
							buffer = UTF8.encode(line);
							dc.send(buffer, server);
						}
						System.out.println(msg);
					} catch (IOException | InterruptedException e) {
						return;
					}
				});

				Thread.ofPlatform().start(() -> {
					try {
						var receiver = ByteBuffer.allocate(BUFFER_SIZE);
						var sender = (InetSocketAddress) dc.receive(receiver);
						System.out.println("Received " + receiver.position() + " bytes from " + sender);
						receiver.flip();
						var upperCase = UTF8.decode(receiver).toString();
						queue.put(upperCase);
						upperCaseLines.add(upperCase);
					} catch (InterruptedException | IOException e) {
						return;
					}
				});
				
			}
		
		}

		// Write upperCaseLines to outFilename in UTF-8
		Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
	}
}