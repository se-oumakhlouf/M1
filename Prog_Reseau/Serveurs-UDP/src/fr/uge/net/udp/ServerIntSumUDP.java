package fr.uge.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class ServerIntSumUDP {

	private final record ClientSession(InetSocketAddress client, long session) {

		private ClientSession {
			Objects.requireNonNull(client);
		}
	}

	private static final Logger logger = Logger.getLogger(ServerIntSumUDP.class.getName());
	private final static Charset UTF8 = StandardCharsets.UTF_8;
	private static final int BUFFER_SIZE = 1024;

	private final DatagramChannel dc;
	private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	private final HashMap<ClientSession, Set<Integer>> data = new HashMap<>();

	public ServerIntSumUDP(int port) throws IOException {
		dc = DatagramChannel.open();
		dc.bind(new InetSocketAddress(port));
		logger.info("ServerBetterUpperCaseUDP started on port " + port);
	}

	public static void usage() {
		System.out.println("Usage : ServerIntSumUDP port");
	}

	public void serve() throws IOException {
		try {
			for (;;) {
				// 1) receive request from client
				buffer.clear();
				var port = (InetSocketAddress) dc.receive(buffer);
				buffer.flip();

				// 2) read id
				var op = buffer.get();
				
				if (op == 1) {
					var session = new ClientSession(port, buffer.getLong());
					var idPosOper = buffer.getInt();
					var totalOper = buffer.getInt();
					var opValue = buffer.getInt();
					
					data.computeIfAbsent(session, set -> new HashSet<Integer>()).add(idPosOper);
					
				} else {
					logger.warning("OP is not correct, should be 1, received " + op);
				}

				// 3) decode msg in request String upperCaseMsg = msg.toUpperCase();
				var msg = UTF8.decode(buffer).toString();

				// 4) create packet with id, upperCaseMsg in UTF-8
				buffer.clear();
//				buffer.putLong(id);
				var upperCase = UTF8.encode(msg.toUpperCase());
				buffer.put(upperCase);

				// 5) send the packet to client
				buffer.flip();
				dc.send(buffer, port);

			}
		} finally {
			dc.close();
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			usage();
			return;
		}

		var port = Integer.parseInt(args[0]);

		if (!(port >= 1024) & port <= 65535) {
			logger.severe("The port number must be between 1024 and 65535");
			return;
		}
	}

}
