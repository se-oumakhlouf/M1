package fr.uge.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HTTPReader {

	private final Charset ASCII_CHARSET = Charset.forName("ASCII");
	private final SocketChannel sc;
	private final ByteBuffer buffer;

	public HTTPReader(SocketChannel sc, ByteBuffer buffer) {
		this.sc = sc;
		this.buffer = buffer;
	}

	/**
	 * @return The ASCII string terminated by CRLF without the CRLF
	 *         <p>
	 *         The method assume that buffer is in write mode and leaves it in write
	 *         mode The method process the data from the buffer and if necessary
	 *         will read more data from the socket.
	 * @throws IOException HTTPException if the connection is closed before a line
	 *                     could be read
	 */
	public String readLineCRLF() throws IOException {
		buffer.flip();
		var foundCR = false;
		var builder = new StringBuilder();
		while (true) {

			while (buffer.hasRemaining()) {

				char c = (char) buffer.get();

				if (foundCR == true) {
					if (c == '\n') {
						buffer.compact();
						return builder.toString();
					}
					builder.append('\r');
					foundCR = false;
				}

				if (c == '\r') {
					foundCR = true;
				} else {
					builder.append(c);
				}

			}

			buffer.clear();
			var bytesRead = sc.read(buffer);
			if (bytesRead == -1) {
				throw new HTTPException("Connexion fermé avant une lecture complète d'une ligne");
			}

			buffer.flip();
		}
	}

	/**
	 * @return The HTTPHeader object corresponding to the header read
	 * @throws IOException HTTPException if the connection is closed before a header
	 *                     could be read or if the header is ill-formed
	 */
	public HTTPHeader readHeader() throws IOException {
		Map<String, String> headers = new HashMap<>();

		String statusLine = readLineCRLF();
		if (statusLine == null) {
			throw new HTTPException("Connexion fermé avant de lire la ligne de status");
		}

		String line;
		while (!(line = readLineCRLF()).isEmpty()) {

			int colonIndex = line.indexOf(":");
			if (colonIndex == -1) {
				throw new HTTPException("Header malformé: " + line);
			}
			String key = line.substring(0, colonIndex);
			String value = line.substring(colonIndex + 1);
			headers.merge(key, value, (currentValue, newValue) -> currentValue + "; " + newValue);
		}

		return HTTPHeader.create(statusLine, headers);
	}

	/**
	 * @param size
	 * @return a ByteBuffer in write mode containing size bytes read on the socket
	 *         <p>
	 *         The method assume that buffer is in write mode and leaves it in write
	 *         mode The method process the data from the buffer and if necessary
	 *         will read more data from the socket.
	 * @throws IOException HTTPException is the connection is closed before all
	 *                     bytes could be read
	 */
	public ByteBuffer readBytes(int size) throws IOException {
		ByteBuffer destBuffer = ByteBuffer.allocate(size);
		while (destBuffer.hasRemaining()) {

			buffer.flip();
			while (buffer.hasRemaining() && destBuffer.hasRemaining()) {
				destBuffer.put(buffer.get());
			}

			if (!buffer.hasRemaining()) {
				buffer.clear();
				int bytesRead = sc.read(buffer);
				if (bytesRead == -1) {
					throw new HTTPException("Connexion perdue avant la lecture de " + size + " octets.");
				}
			}

		}
		buffer.compact();
		return destBuffer;
	}

	/**
	 * @return a ByteBuffer in write-mode containing a content read in chunks mode
	 * @throws IOException HTTPException if the connection is closed before the end
	 *                     of the chunks if chunks are ill-formed
	 */

	public ByteBuffer readChunks() throws IOException {
		// TODO
		return null;
	}

	public static void main(String[] args) throws IOException {
		var charsetASCII = Charset.forName("ASCII");
		var request = "GET / HTTP/1.1\r\n" + "Host: www.w3.org\r\n" + "\r\n";
		var sc = SocketChannel.open();
		sc.connect(new InetSocketAddress("www.w3.org", 80));
		sc.write(charsetASCII.encode(request));
		var buffer = ByteBuffer.allocate(50);
		var reader = new HTTPReader(sc, buffer);
		System.out.println(reader.readLineCRLF());
		System.out.println(reader.readLineCRLF());
		System.out.println(reader.readLineCRLF());
		sc.close();

		buffer = ByteBuffer.allocate(50);
		sc = SocketChannel.open();
		sc.connect(new InetSocketAddress("www.w3.org", 80));
		reader = new HTTPReader(sc, buffer);
		sc.write(charsetASCII.encode(request));
		System.out.println(reader.readHeader());
		sc.close();

		buffer = ByteBuffer.allocate(50);
		sc = SocketChannel.open();
		sc.connect(new InetSocketAddress("igm.univ-mlv.fr", 80));
		request = "GET /coursprogreseau/ HTTP/1.1\r\n" + "Host: igm.univ-mlv.fr\r\n" + "\r\n";
		reader = new HTTPReader(sc, buffer);
		sc.write(charsetASCII.encode(request));
		var header = reader.readHeader();
		System.out.println(header);
		var content = reader.readBytes(header.getContentLength());
		content.flip();
		System.out.println(header.getCharset().orElse(Charset.forName("UTF8")).decode(content));
		sc.close();

		buffer = ByteBuffer.allocate(50);
		request = "GET / HTTP/1.1\r\n" + "Host: www.u-pem.fr\r\n" + "\r\n";
		sc = SocketChannel.open();
		sc.connect(new InetSocketAddress("www.u-pem.fr", 80));
		reader = new HTTPReader(sc, buffer);
		sc.write(charsetASCII.encode(request));
		header = reader.readHeader();
		System.out.println(header);
		content = reader.readChunks();
		content.flip();
		System.out.println(header.getCharset().orElse(Charset.forName("UTF8")).decode(content));
		sc.close();
	}
}