package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private final Transformer t;

	static final class Parser {
		private final String commands;
		private int index;

		Parser(String commands) {
			this.commands = commands;
		}

		boolean canParse() {
			return index < commands.length();
		}

		Transformer parse(Transformer t) {

			Transformer newTransformer;
			var currentCommand = commands.charAt(index);

			switch (currentCommand) {
				case 'l' -> newTransformer = line -> line.toLowerCase(Locale.ROOT);
				case 'u' -> newTransformer = line -> line.toUpperCase(Locale.ROOT);
				case '*' -> {
					if (index + 1 >= commands.length())
						throw new IllegalArgumentException("Missing number after '*'");

					int repeatCount = commands.charAt(index + 1) - '0';
					newTransformer = line -> line.replace("*", "*".repeat(repeatCount));
					index += 2;
					return line -> newTransformer.transform(t.transform(line));
				}
				default -> throw new IllegalArgumentException("Unknow command : " + currentCommand);
			}
			index++;
			return line -> newTransformer.transform(t.transform(line));
		}
	}

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String commands) {
		Objects.requireNonNull(commands);
		Transformer transformer = line -> line;

		var parser = new Parser(commands);
		while (parser.canParse()) {
			transformer = parser.parse(transformer);
		}

		return transformer;
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
	}

}
