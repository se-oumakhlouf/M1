package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		return switch (command.charAt(0)) {
			case 'l' -> line -> line.toLowerCase(Locale.ROOT) ;
			case 'u' -> line -> line.toUpperCase(Locale.ROOT);
			case '*' -> line -> line.replace("*", "*".repeat(command.charAt(1) - '0'));
			default -> throw new IllegalArgumentException("Unexpected value: " + command.charAt(0));
		};
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;
		String newLine;

		while ((line = reader.readLine()) != null) {
			newLine = transformer.transform(line);
			writer.write(newLine);
			writer.write("\n");
		}
		reader.close();
		return;
	}

}
