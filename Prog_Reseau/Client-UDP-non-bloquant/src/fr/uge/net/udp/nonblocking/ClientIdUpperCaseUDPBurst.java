package fr.uge.net.udp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;

// use :
//$ java -jar ServerIdUpperCaseUDP.jar 4545 UTF-8
//$ java -jar UDPProxy.jar 7777 localhost 4545 -no-swap 
//$ java fr.uge.net.udp.nonblocking.ClientIdUpperCaseUDPBurst in.txt out.txt 300 localhost 7777

public class ClientIdUpperCaseUDPBurst {

	private static Logger logger = Logger.getLogger(ClientIdUpperCaseUDPBurst.class.getName());
	private static final Charset UTF8 = Charset.forName("UTF8");
	private static final int BUFFER_SIZE = 1024;

	private enum State {
		SENDING, RECEIVING, FINISHED
	};

	private final List<String> lines;
	private final List<String> upperCaseLines = new ArrayList<>();
	private final long timeout;
	private final InetSocketAddress serverAddress;
	private final DatagramChannel dc;
	private final Selector selector;
	private final SelectionKey uniqueKey;

	private final ByteBuffer receiver = ByteBuffer.allocate(BUFFER_SIZE);
	private long lastSend = 0;
	private int totalResponses = 0;
	private final String[] isReceived;

	private State state;

	private static void usage() {
		System.out.println("Usage : ClientIdUpperCaseUDPOneByOne in-filename out-filename timeout host port ");
	}

	private ClientIdUpperCaseUDPBurst(List<String> lines, long timeout, InetSocketAddress serverAddress,
			DatagramChannel dc, Selector selector, SelectionKey uniqueKey) {
		this.lines = lines;
		this.timeout = timeout;
		this.serverAddress = serverAddress;
		this.dc = dc;
		this.selector = selector;
		this.uniqueKey = uniqueKey;
		this.state = State.SENDING;
		this.receiver.order(ByteOrder.BIG_ENDIAN);
		this.isReceived = new String[lines.size()];
	}

	public static ClientIdUpperCaseUDPBurst create(String inFilename, long timeout, InetSocketAddress serverAddress)
			throws IOException {
		Objects.requireNonNull(inFilename);
		Objects.requireNonNull(serverAddress);
		Objects.checkIndex(timeout, Long.MAX_VALUE);

		// Read all lines of inFilename opened in UTF-8
		var lines = Files.readAllLines(Path.of(inFilename), UTF8);
		var dc = DatagramChannel.open();
		dc.configureBlocking(false);
		dc.bind(null);
		var selector = Selector.open();
		var uniqueKey = dc.register(selector, SelectionKey.OP_WRITE);
		return new ClientIdUpperCaseUDPBurst(lines, timeout, serverAddress, dc, selector, uniqueKey);
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
		var upperCaseLines = create(inFilename, timeout, server).launch();

		Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
	}

	private List<String> launch() throws IOException, InterruptedException {
		try {
			while (!isFinished()) {
				try {
					selector.select(this::treatKey, updateInterestOps());
				} catch (UncheckedIOException tunneled) {
					throw tunneled.getCause();
				}
			}
			return upperCaseLines;
		} finally {
			dc.close();
		}
	}

	private void treatKey(SelectionKey key) {
		try {
			if (key.isValid() && key.isWritable()) {
				doWrite();
			}
			if (key.isValid() && key.isReadable()) {
				doRead();
			}
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	/**
	 * Updates the interestOps on key based on state of the context
	 *
	 * @return the timeout for the next select (0 means no timeout)
	 */

	private long updateInterestOps() {
		long now = System.currentTimeMillis();
		long delay = now - lastSend;

		int ops = 0;
		switch (state) {
			case SENDING -> {
				ops = SelectionKey.OP_WRITE;
			}
			case RECEIVING -> {
				ops = SelectionKey.OP_READ;
				uniqueKey.interestOps(ops);
				if (delay >= timeout) {
					state = State.SENDING;
					ops = SelectionKey.OP_WRITE;
					uniqueKey.interestOps(ops);
				} else {
					return timeout - delay;
				}
			}
			case FINISHED -> {
				ops = 0;
			}
		}
		uniqueKey.interestOps(ops);
		return 0;
	}

	private boolean isFinished() {
		return state == State.FINISHED;
	}

	/**
	 * Performs the receptions of packets
	 *
	 * @throws IOException
	 */

	private void doRead() throws IOException {
		while (totalResponses != lines.size()) {
			receiver.clear();
			var sender = dc.receive(receiver);

			if (sender == null) {
				state = State.SENDING;
				break;
			}

			receiver.flip();

			int id = (int) receiver.getLong();
			if (isReceived[id] != null) {
				continue;
			}

			var upperCase = UTF8.decode(receiver).toString();
			isReceived[id] = upperCase; // keep the order
			totalResponses++;
			state = State.SENDING;
		}
		if (totalResponses != lines.size()) {
			return;
		}
		state = State.FINISHED;
		for (var line : isReceived) {
			upperCaseLines.add(line);
		}
		logger.info("All packages have been received.\nEnding...");

	}

	/**
	 * Tries to send the packets
	 *
	 * @throws IOException
	 */

	private void doWrite() throws IOException {
		var sender = ByteBuffer.allocate(BUFFER_SIZE);
		sender.order(ByteOrder.BIG_ENDIAN);
		// ne pas faire de boucle de send
		// juste envoyer la ligne courante
		// et passer à RECEIVING lorsque currentId = lines.size()
		for (int i = 0; i < lines.size(); i++) {

			if (isReceived[i] != null) {
				continue;
			}

			sender.clear();

			sender.putLong((long) i);
			sender.put(UTF8.encode(lines.get(i)));

			sender.flip();
			dc.send(sender, serverAddress);

			if (sender.hasRemaining()) {
				logger.warning("failed to send");
				continue;
			}

			lastSend = System.currentTimeMillis();
		}

		if (totalResponses == lines.size()) {
			state = State.FINISHED;
		} else {
			state = State.RECEIVING;
		}

	}
}