package fr.uge.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Optional;

public class HTTPClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java HTTPClient <server> <resource>");
            return;
        }

        String server = args[0];
        String resource = args[1];

        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(server, 80));

            String request = "GET " + resource + " HTTP/1.1\r\n" +
                             "Host: " + server + "\r\n" +
                             "\r\n";

            ByteBuffer requestBuffer = Charset.forName("ASCII").encode(request);
            sc.write(requestBuffer);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            HTTPReader reader = new HTTPReader(sc, buffer);

            String statusLine = reader.readLineCRLF();
            System.out.println("Status Line: " + statusLine);

            HTTPHeader header = reader.readHeader();
            System.out.println("Headers: " + header);

            int contentLength = header.getContentLength();
            if (contentLength == -1) {
                System.out.println("Le serveur n'a pas fourni de Content-Length.");
                sc.close();
                return;
            }

            ByteBuffer contentBuffer = reader.readBytes(contentLength);
            contentBuffer.flip();
            String content = Charset.forName("UTF-8").decode(contentBuffer).toString();

            Optional<String> contentType = header.getContentType();
            if (contentType.isPresent() && contentType.get().contains("text/html")) {
                System.out.println("Contenu HTML reçu:");
                System.out.println(content);
            } else {
                System.out.println("Le contenu n'est pas du HTML.");
            }

            sc.close();

        } catch (IOException e) {
            System.err.println("Erreur lors de la connexion ou de la lecture: " + e.getMessage());
        }
    }
}
