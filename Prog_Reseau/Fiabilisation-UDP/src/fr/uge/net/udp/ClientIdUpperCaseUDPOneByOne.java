package fr.uge.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;

public class ClientIdUpperCaseUDPOneByOne {

	private static Logger logger = Logger.getLogger(ClientIdUpperCaseUDPOneByOne.class.getName());
	private static final Charset UTF8 = StandardCharsets.UTF_8;
	private static final int BUFFER_SIZE = 1024;

	private record Response(long id, String message) {
	};

	private final String inFilename;
	private final String outFilename;
	private final long timeout;
	private final InetSocketAddress server;
	private final DatagramChannel dc;
	private final SynchronousQueue<Response> queue = new SynchronousQueue<>();

	public static void usage() {
		System.out.println("Usage : ClientIdUpperCaseUDPOneByOne in-filename out-filename timeout host port ");
	}

	public ClientIdUpperCaseUDPOneByOne(String inFilename, String outFilename, long timeout, InetSocketAddress server)
			throws IOException {
		this.inFilename = Objects.requireNonNull(inFilename);
		this.outFilename = Objects.requireNonNull(outFilename);
		this.timeout = timeout;
		this.server = server;
		this.dc = DatagramChannel.open();
		dc.bind(null);
	}

	private void listenerThreadRun() {
		try {
			var listener = ByteBuffer.allocate(BUFFER_SIZE);
			listener.order(ByteOrder.BIG_ENDIAN);
			while (!Thread.currentThread().isInterrupted()) {
				listener.clear();
				logger.info("\treceiving");
				var sender = dc.receive(listener);
				if (sender == null) {
					logger.info("No packet received");
					continue;
				}
				listener.flip();
				var id = listener.getLong();
				var message = UTF8.decode(listener).toString();
				if (message.isEmpty()) {
					logger.info("Received empty message for id -> " + id + ". Skipping...");
					continue;
				}
				queue.put(new Response(id, message));
				System.out.println("id = " + id + ", message = " + message);
			}
		} catch (IOException | InterruptedException e) {
			logger.info("Listener Thread interrupted or finished");
		}
	}

	public void launch() throws IOException, InterruptedException {
		try {

			var listenerThread = Thread.ofPlatform().priority(Thread.MAX_PRIORITY).start(this::listenerThreadRun);

			// Read all lines of inFilename opened in UTF-8
			var lines = Files.readAllLines(Path.of(inFilename), UTF8);

			var upperCaseLines = new ArrayList<String>();
			int index = 0;
			for (var line : lines) {
				var message = UTF8.encode(line);
				if (message.remaining() + Long.BYTES > BUFFER_SIZE) {
					throw new IllegalArgumentException("Message too large to fit in buffer -> " + line);
				}
				var sender = ByteBuffer.allocate(BUFFER_SIZE);
				sender.order(ByteOrder.BIG_ENDIAN);
				sender.putLong(index);
				sender.put(message);
				sender.flip();
				logger.info("first send of line -> " + line + ", index = " + index);
				dc.send(sender, server);
				Response response = null;
				while ((response = queue.poll(timeout, TimeUnit.MILLISECONDS)) == null || response.id != index
						|| response.message.isBlank()) {
					logger.info("Timeout or mismatched response. Retrying...");
					if (response == null) {
						logger.info("response was null");
					} else {
						logger.info("message -> " + response.message + ", id -> " + response.id + ", index -> " + index);
					}
					sender = ByteBuffer.allocate(BUFFER_SIZE);
					sender.order(ByteOrder.BIG_ENDIAN);
					sender.putLong(index);
					sender.put(UTF8.encode(line));
					sender.flip();
					dc.send(sender, server);
				}
				upperCaseLines.add(response.message);
				sender.clear();
				index++;
				System.out.println("Finished a line\n");
			}

			listenerThread.interrupt();
			Files.write(Paths.get(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
		} finally {
			dc.close();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 5) {
			usage();
			return;
		}

		var inFilename = args[0];
		var outFilename = args[1];
		var timeout = Long.parseLong(args[2]);
		var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));

		// Create client with the parameters and launch it
		new ClientIdUpperCaseUDPOneByOne(inFilename, outFilename, timeout, server).launch();
	}
}