package fr.uge.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class ClientEOS {

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	public static final int BUFFER_SIZE = 1024;
	public static final Logger logger = Logger.getLogger(ClientEOS.class.getName());

	/**
	 * This method: - connect to server - writes the bytes corresponding to request
	 * in UTF8 - closes the write-channel to the server - stores the bufferSize
	 * first bytes of server response - return the corresponding string in UTF8
	 *
	 * @param request
	 * @param server
	 * @param bufferSize
	 * @return the UTF8 string corresponding to bufferSize first bytes of server
	 *         response
	 * @throws IOException
	 */

	public static String getFixedSizeResponse(String request, SocketAddress server, int bufferSize) throws IOException {
		SocketChannel sc = SocketChannel.open();
		sc.connect(server);

		logger.info("request:\n" + request);

		var buffer = ByteBuffer.allocate(BUFFER_SIZE);
		var msg = UTF8_CHARSET.encode(request);

		buffer.put(msg);
		buffer.flip();

		sc.write(buffer);
		sc.shutdownOutput();

		var reception = ByteBuffer.allocate(bufferSize);
		int read = 0;
		int all = 0;

		do {
			read = sc.read(reception);
			all += read;
		} while (all < 512 && read != -1 && read != 0);

		logger.info("received\n");
		reception.flip();

		return UTF8_CHARSET.decode(reception).toString();
	}

	/**
	 * This method: - connect to server - writes the bytes corresponding to request
	 * in UTF8 - closes the write-channel to the server - reads and stores all bytes
	 * from server until read-channel is closed - return the corresponding string in
	 * UTF8
	 *
	 * @param request
	 * @param server
	 * @return the UTF8 string corresponding the full response of the server
	 * @throws IOException
	 */

	public static String getUnboundedResponse(String request, SocketAddress server) throws IOException {
		var sc = SocketChannel.open();
		sc.connect(server);

		logger.info("request:\n" + request);

		var buffer = ByteBuffer.allocate(BUFFER_SIZE);
		var msg = UTF8_CHARSET.encode(request);
		buffer.put(msg);
		buffer.flip();
		sc.write(buffer);
		sc.shutdownOutput();

		var receiver = ByteBuffer.allocate(BUFFER_SIZE);
		var storage = ByteBuffer.allocate(BUFFER_SIZE);
		int failure = 2;
		int read = 0;
		
//		while (true) {
//			if (!readFully(sc, storage)) {
//				logger.info("Server close the connection too soon");
//				return "fail";
//			} else {
//				if (!storage.hasRemaining()) {
//					var tmp = ByteBuffer.allocate(BUFFER_SIZE * failure);
//					failure += 2;
//					storage.flip();
//					tmp.put(storage);
//					storage = tmp;
//				}
//			}
//			sortir de la boucle
//		}

		while ((read = sc.read(receiver)) != -1) {
			if (read == 0) {
				if (!storage.hasRemaining()) {
					var tmp = ByteBuffer.allocate(BUFFER_SIZE * failure);
					failure += 2;
					storage.flip();
					tmp.put(storage);
					storage = tmp.duplicate();

				}
				receiver.flip();
				storage.put(receiver);
				receiver.clear();
			}
		}
		logger.info("fully received");
		storage.flip();
		sc.shutdownInput();
		sc.close();
		return UTF8_CHARSET.decode(storage).toString();
	}

	/**
	 * Fill the workspace of the Bytebuffer with bytes read from sc.
	 *
	 * @param sc
	 * @param buffer
	 * @return false if read returned -1 at some point and true otherwise
	 * @throws IOException
	 */
	static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
		while (buffer.hasRemaining()) {
			if (sc.read(buffer) == -1) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		var google = new InetSocketAddress("www.google.fr", 80);
//		System.out.println(getFixedSizeResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google, 512));
		System.out.println(getUnboundedResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google));
	}
}