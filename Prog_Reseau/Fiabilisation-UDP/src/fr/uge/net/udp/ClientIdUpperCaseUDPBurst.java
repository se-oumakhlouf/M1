package fr.uge.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ClientIdUpperCaseUDPBurst {

	private static Logger logger = Logger.getLogger(ClientIdUpperCaseUDPBurst.class.getName());
	private static final Charset UTF8 = StandardCharsets.UTF_8;
	private static final int BUFFER_SIZE = 1024;
	private final List<String> lines;
	private final int nbLines;
	private final String[] upperCaseLines; //
	private final int timeout;
	private final String outFilename;
	private final InetSocketAddress serverAddress;
	private final DatagramChannel dc;
	private final AnswersLog answersLog; // Thread-safe structure keeping track of missing responses

	public static void usage() {
		System.out.println("Usage : ClientIdUpperCaseUDPBurst in-filename out-filename timeout host port ");
	}

	public ClientIdUpperCaseUDPBurst(List<String> lines, int timeout, InetSocketAddress serverAddress, String outFilename)
			throws IOException {
		this.lines = lines;
		this.nbLines = lines.size();
		this.timeout = timeout;
		this.outFilename = outFilename;
		this.serverAddress = serverAddress;
		this.dc = DatagramChannel.open();
		dc.bind(null);
		this.upperCaseLines = new String[nbLines];
		this.answersLog = new AnswersLog(nbLines);
	}

	private void senderThreadRun() {

		// body of the sender thread

		try {
			var sender = ByteBuffer.allocate(BUFFER_SIZE);
			sender.order(ByteOrder.BIG_ENDIAN);
			while (answersLog.allReceived() == false) {
				for (int i = 0; i < nbLines; i++) {
					if (answersLog.isReceived(i) == false) {
						sender.clear();
						sender.putLong((long) i);
						sender.put(UTF8.encode(lines.get(i)));
						sender.flip();
						dc.send(sender, serverAddress);
					}
				}
				Thread.sleep(timeout);
			}
		} catch (IOException | InterruptedException e) {
			logger.info("Sender Thread -> interrupted || finished");
		}

	}

	public void launch() throws IOException {
		Thread.ofPlatform().start(this::senderThreadRun);

		// body of the receiver thread

		try {
			var receiver = ByteBuffer.allocate(BUFFER_SIZE);
			receiver.order(ByteOrder.BIG_ENDIAN);
			while (answersLog.allReceived() == false) {
				receiver.clear();
				dc.receive(receiver);
				receiver.flip();
				var id = receiver.getLong();
				var response = UTF8.decode(receiver).toString();
				answersLog.markReceived((int) id);
				upperCaseLines[(int) id] = response;
			}
		} finally {
			Files.write(Paths.get(outFilename), Arrays.asList(upperCaseLines), UTF8, StandardOpenOption.CREATE,
					StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			logger.info("Text is fully decoded");
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 5) {
			usage();
			return;
		}

		String inFilename = args[0];
		String outFilename = args[1];
		int timeout = Integer.valueOf(args[2]);
		String host = args[3];
		int port = Integer.valueOf(args[4]);
		InetSocketAddress serverAddress = new InetSocketAddress(host, port);

		// Read all lines of inFilename opened in UTF-8
		List<String> lines = Files.readAllLines(Paths.get(inFilename), UTF8);
		// Create client with the parameters and launch it
		ClientIdUpperCaseUDPBurst client = new ClientIdUpperCaseUDPBurst(lines, timeout, serverAddress, outFilename);
		client.launch();
	}

	private static class AnswersLog {

		// TODO Thread-safe class handling the information about missing lines
		private final boolean[] receivedResponses;
		private final int totalResponses;
		private final ReentrantLock lock = new ReentrantLock();

		public AnswersLog(int totalResponses) {
			this.totalResponses = totalResponses;
			this.receivedResponses = new boolean[this.totalResponses];
		}

		public void markReceived(int index) {
			lock.lock();
			try {
				receivedResponses[index] = true;
			} finally {
				lock.unlock();
			}
		}

		public boolean isReceived(int index) {
			lock.lock();
			try {
				return receivedResponses[index];
			} finally {
				lock.unlock();
			}
		}

		public boolean allReceived() {
			lock.lock();
			try {
				for (var received : receivedResponses) {
					if (received == false) {
						return false;
					}
				}
				return true;
			} finally {
				lock.unlock();
			}
		}
	}

}
