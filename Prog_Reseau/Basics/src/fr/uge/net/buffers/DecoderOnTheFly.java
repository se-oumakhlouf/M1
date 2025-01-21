package fr.uge.net.buffers;

import static java.nio.file.StandardOpenOption.READ;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;

public class DecoderOnTheFly {

	private final int inputBufferCapacity;
	private final int outputBufferCapacity;
	private final CharsetDecoder charsetDecoder;

	private final ByteBuffer byteBuffer;
	private final CharBuffer charBuffer;

	private static void usage() {
		System.out.println("Usage: DecoderOnTheFly charset filename inputBufferCapacity");
	}

	public DecoderOnTheFly(Charset charset, int inputBufferCapacity) {
		if (inputBufferCapacity < Math.ceil(charset.newEncoder().maxBytesPerChar())) {
			throw new IllegalArgumentException(
					"The input buffer must be able to contain at least largest encoded character for this charset");
		}
		this.charsetDecoder = charset.newDecoder();
		this.inputBufferCapacity = inputBufferCapacity;
		// largest size needed for the output buffer, for this we need maxCharsPerByte
		this.outputBufferCapacity = (int) Math.ceil(inputBufferCapacity * charsetDecoder.maxCharsPerByte());
		byteBuffer = ByteBuffer.allocate(inputBufferCapacity);
		charBuffer = CharBuffer.allocate(outputBufferCapacity);
	}

	public String stringFromFile(Path path) throws IOException {
		var builder = new StringBuilder();
		
		try (var channel = FileChannel.open(path, READ)) {
			while (channel.read(byteBuffer) != -1) {
				
				byteBuffer.flip();
				var coderResult = charsetDecoder.decode(byteBuffer, charBuffer, false);
				
				if (coderResult.isMalformed()) {
					throw new IllegalArgumentException("MALFORMED INPUT");
				} else if (coderResult.isUnmappable()) {
					throw new IllegalArgumentException("UNMAPPABLE CHARACTER");
				} else {
					// rien à faire pour underflow et overflow
				}
				
				charBuffer.flip();
				System.out.println(charBuffer);
				builder.append(charBuffer);
				charBuffer.clear();
				byteBuffer.compact();
			}
			
			byteBuffer.flip();
			charsetDecoder.decode(byteBuffer, charBuffer, true);
			charsetDecoder.flush(charBuffer);
			charBuffer.flip();
			System.out.println(charBuffer);
			builder.append(charBuffer);
			
		}
		return builder.toString();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			usage();
			return;
		}
		var charset = Charset.forName(args[0]);
		var inputBufferCapacity = Integer.parseInt(args[2]);
		var decoderOnTheFly = new DecoderOnTheFly(charset, inputBufferCapacity);

		var path = Path.of(args[1]);
		System.out.println(decoderOnTheFly.stringFromFile(path));
	}
}