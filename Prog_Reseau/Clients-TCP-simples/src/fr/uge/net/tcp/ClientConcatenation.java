package fr.uge.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

// use :
// java -jar ServerConcatenationTCP.jar 7777
// java fr.uge.net.tcp.ClientConcatenation localhost 7777

public class ClientConcatenation {

	private static final Logger logger = Logger.getLogger(ClientConcatenation.class.getName());
	private static final Charset UTF8 = Charset.forName("UTF8");
	private static final int BUFFER_SIZE = 1024;

	public static void usage() {
		System.out.println("Usage : ClientConcatenation host port ");
	}

	public static List<String> scan() {
		List<String> lines = new ArrayList<String>();
		logger.info("Start of scan... ");
		try (var scanner = new Scanner(System.in)) {
			while (true) {
				var line = scanner.nextLine();
				if (line.isEmpty()) {
					break;
				}
				lines.add(line);
			}
		}
		logger.info("...End of scan");
		return lines;
	}
	
	public static String launch(SocketChannel sc, List<String> lines) throws IOException {
		var buffer = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);

		buffer.putInt(lines.size());

		for (var line : lines) {

			var chain = UTF8.encode(line);
			int size = chain.limit();
			System.out.println(size);
			if (size + Integer.BYTES > BUFFER_SIZE) {
				buffer.flip();
				
				var tmp = ByteBuffer.allocate(size + Integer.BYTES + buffer.limit()).order(ByteOrder.BIG_ENDIAN);
				tmp.put(buffer);
				tmp.putInt(size);
				tmp.put(chain);
				tmp.flip();
				sc.write(tmp);
				buffer.clear();
				continue;

			} else if (buffer.remaining() < size + Integer.BYTES) {

				buffer.flip();
				sc.write(buffer);
				buffer.clear();

			}

			buffer.putInt(size);
			buffer.put(chain);
		}

		buffer.flip();
		sc.write(buffer);
		sc.shutdownOutput();
		logger.info("Sent every line");

		buffer.clear();
		var response = new StringBuilder();
		while (true) {
			if (!readFully(sc, buffer)) {
				break;
			}
			if (!buffer.hasRemaining()) {
				buffer.flip();
				response.append(UTF8.decode(buffer).toString());
				buffer.clear();
			}
		}
		
		if (buffer.hasRemaining()) {
	    buffer.flip();
	    response.append(UTF8.decode(buffer).toString());
	}

		return response.toString();

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
		if (args.length != 2) {
			usage();
			return;
		}

		List<String> lines = scan();

		var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		try (var sc = SocketChannel.open(server)) {
			System.out.println("Server Response:\n" + launch(sc, lines));
		}
	}

}
