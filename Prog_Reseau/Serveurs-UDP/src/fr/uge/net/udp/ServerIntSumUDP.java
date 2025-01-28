package fr.uge.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerIntSumUDP {

	private final record ClientSession(InetSocketAddress client, long sessionID) {

		private ClientSession {
			Objects.requireNonNull(client);
		}
	}

	private static final Logger logger = Logger.getLogger(ServerIntSumUDP.class.getName());
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
		buffer.order(ByteOrder.BIG_ENDIAN);
		try {
			for (;;) {
				// receive request from client
				buffer.clear();
				var port = (InetSocketAddress) dc.receive(buffer);
				buffer.flip();

				// read request -> byte, sessionID, idPosOper, totalOper, opValue
				var op = buffer.get();
				var session = new ClientSession(port, buffer.getLong());
				var idPosOper = buffer.getInt();
				var totalOper = buffer.getInt();
				var opValue = buffer.getInt();
				logger.info("Received package from client " + session.client);

				if (op == 1) {

					// _ indicates that the variable in the lambda expression is not used (-> no
					// warnings)
					var sessionSet = data.computeIfAbsent(session, _ -> new HashSet<Integer>());
					sessionSet.add(opValue);

					if (sessionSet.size() == totalOper - 1) {
						// RES package (RES = 3) -> byte, sessionID, sum
						buffer.clear();
						buffer.put((byte) 3);
						buffer.putLong(session.sessionID);
						var sum = sessionSet.stream().mapToInt(Integer::valueOf).sum();
						buffer.putInt(sum);
						buffer.flip();
						dc.send(buffer, port);
						logger.info("Sent package RES (= 3) -> " + 3 + ", " + session.sessionID + ", " + sum + " to client -> " + session.client);
						break;
					}

				} else {
					logger.warning("OP is not correct, should be 1, received " + op);
				}

				// ACK package (ACK = 2) -> byte, sessionID, idPosOper
				buffer.clear();
				buffer.put((byte) 2);
				buffer.putLong(session.sessionID);
				buffer.putInt(idPosOper);
				buffer.flip();
				dc.send(buffer, port);
				logger.info("Sent package ACK (= 2) -> " + 2 + ", " + session.sessionID + ", " + idPosOper + " to client -> " + session.client + " on port " + port);

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
		
		try {
			new ServerIntSumUDP(port).serve();
		} catch (BindException e) {
			logger.severe("Server could not bind on " + port + "\nAnother server is probably running on this port.");
			return;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Server stopped due to en I/O Exception.", e);
			return;
		}
	}

}
