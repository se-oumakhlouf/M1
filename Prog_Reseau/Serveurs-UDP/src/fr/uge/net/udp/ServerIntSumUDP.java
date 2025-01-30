package fr.uge.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
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

	private final HashMap<ClientSession, Long> dataSum = new HashMap<>();
	private final HashMap<ClientSession, Set<Integer>> dataIdPosOper = new HashMap<>();

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
				var clientAddress = (InetSocketAddress) dc.receive(buffer);
				buffer.flip();

				if (buffer.remaining() < Byte.BYTES + Long.BYTES + Integer.BYTES * 3) {
					logger.warning("Received invalid packet from " + clientAddress + ",  Packet size : " + buffer.remaining());
					continue;
				}

				// read request -> byte, sessionID, idPosOper, totalOper, opValue
				var op = buffer.get();
				var session = new ClientSession(clientAddress, buffer.getLong());
				var idPosOper = buffer.getInt();
				var totalOper = buffer.getInt();
				var opValue = buffer.getInt();
				logger.info(
						"Received package from client " + session.client + " -> " + idPosOper + ", " + totalOper + ", " + opValue);

				if (op == 1) {

					// _ indicates that the variable in the lambda expression is not used (-> no
					// warnings)
					var sessionSet = dataSum.computeIfAbsent(session, _ -> 0L);
					var idPosSet = dataIdPosOper.computeIfAbsent(session, _ -> new HashSet<Integer>());
					if (idPosSet.add(idPosOper) == true) {
						sessionSet += opValue;
					}
					if (idPosSet.size() == totalOper) {
						// RES package (RES = 3) -> byte, sessionID, sum
						buffer.clear();
						if (buffer.remaining() < Byte.BYTES + Long.BYTES * 2) {
							logger.warning("Package RES too long");
							continue;
						}
						buffer.put((byte) 3);
						buffer.putLong(session.sessionID);
						var sum = dataSum.get(session);
						buffer.putLong(sum);
						buffer.flip();
						dc.send(buffer, clientAddress);
						logger.info("Sent package RES (= 3) -> " + 3 + ", " + session.sessionID + ", " + sum + " to client -> "
								+ session.client);
						continue;
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
				dc.send(buffer, clientAddress);
				logger.info("Sent package ACK (= 2) -> " + 2 + ", " + session.sessionID + ", " + idPosOper + " to client -> "
						+ session.client);

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
		} catch (ClosedByInterruptException e) {
			logger.info("Server was interrupted and stopped.");
			return;
		} catch (AsynchronousCloseException e) {
			logger.info("Server channel was closed asynchronously.");
			return;
		} catch (ClosedChannelException e) {
			logger.warning("The server channel was closed unexpectedly.");
			return;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Server stopped due to en I/O Exception.", e);
			return;
		}
	}

}
