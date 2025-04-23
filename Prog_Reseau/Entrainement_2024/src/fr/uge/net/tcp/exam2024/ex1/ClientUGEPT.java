package fr.uge.net.tcp.exam2024.ex1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientUGEPT {
	private static final Logger logger = Logger.getLogger(fr.uge.net.tcp.exam2024.ex1.ClientUGEPT.class.getName());

	private final InetSocketAddress serverAddress;
	private final SocketChannel socketChannel;

	public ClientUGEPT(InetSocketAddress serverAddress) throws IOException {
		this.serverAddress = Objects.requireNonNull(serverAddress);
		this.socketChannel = SocketChannel.open(serverAddress);
	}

	public List<String> performRequest(String prompt) throws IOException {
		var message = StandardCharsets.UTF_8.encode(prompt);
		int size = message.limit();
		var sendBuffer = ByteBuffer.allocate(size + Integer.BYTES);
		sendBuffer.putInt(size);
		sendBuffer.put(message);
		sendBuffer.flip();
		socketChannel.write(sendBuffer);

		var readBuffer = ByteBuffer.allocate(1024);
		var messageBuffer = ByteBuffer.allocate(1023);
		var messageList = new ArrayList<String>();

		while (true) {
			readBuffer.clear();
			int bytesRead = socketChannel.read(readBuffer);
			if (bytesRead == -1) {
				logger.info("Server close the connection");
				return null;
			}
			readBuffer.flip();
			while (readBuffer.hasRemaining()) {
				byte b = readBuffer.get();
				if (b != 10) {
					if (messageBuffer.position() >= 1023) {
						return null;
					}
					messageBuffer.put(b);
				} else {
					messageBuffer.flip();
					var response = StandardCharsets.UTF_8.decode(messageBuffer).toString();
					messageBuffer.clear();
					if ("STOP".equals(response)) {
						return messageList;
					}
					messageList.add(response);
				}
			}

		}
	}

	public void launch() throws IOException {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("What is your prompt ?");
			while (scanner.hasNextLine()) {
				var prompt = scanner.nextLine();
				var answers = performRequest(prompt);
				if (answers == null) {
					System.out.println("Wrong server's response, exiting program");
					return;
				}
				System.out.println(answers);
				System.out.println("What is your prompt ?");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("usage: java fr.uge.tcp.exam2024.ex1.ClientUGEPT host port");
			return;
		}
		var serverAddress = new InetSocketAddress(args[0], Integer.valueOf(args[1]));
		new ClientUGEPT(serverAddress).launch();
	}
}