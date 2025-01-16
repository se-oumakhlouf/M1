package fr.uge.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NetcatUDP {
	public static final int BUFFER_SIZE = 1024;

	private static void usage() {
		System.out.println("Usage : NetcatUDP host port charset");
	}
	
	// pour un client et un serveur avec des jeux de caractères différents, on peut obtenir des ?
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			usage();
			return;
		}

		var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		var cs = Charset.forName(args[2]);
		var buffer = ByteBuffer.allocate(BUFFER_SIZE);

		try (var scanner = new Scanner(System.in);) {
			while (scanner.hasNextLine()) {
				var line = scanner.nextLine();
				try (DatagramChannel dc = DatagramChannel.open()) {
					dc.bind(null);
					buffer = cs.encode(line);
					dc.send(buffer, server);
					var receiver = ByteBuffer.allocate(BUFFER_SIZE);
					var sender = (InetSocketAddress) dc.receive(receiver);
					System.out.println("Received " + receiver.capacity() + " bytes from " + sender);
					receiver.flip();
					var decode = cs.decode(receiver);
					System.out.println(decode.toString());
				}
			}
		}
	}

}