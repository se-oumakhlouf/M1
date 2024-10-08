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
		Transformer transformer = line -> line;
		int index = 0;
		while (index < command.length()) {
			TransformerIndexTuple parsed = parse(command, transformer, index);
			transformer = parsed.transformer();
			index = parsed.index();
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
		return;
	}

	public static TransformerIndexTuple parse(String command, Transformer transformer, int index) {
		var currentCommand = command.charAt(index);
		Transformer newTransformer;

		switch (currentCommand) {
			case 'l' -> newTransformer = line -> line.toLowerCase(Locale.ROOT);
			case 'u' -> newTransformer = line -> line.toUpperCase(Locale.ROOT);
			case '*' -> {
				newTransformer = line -> line.replace("*", "*".repeat(command.charAt(index + 1) - '0'));
				return new TransformerIndexTuple(line -> newTransformer.transform(transformer.transform(line)), index + 2);
			}
			default -> throw new IllegalArgumentException("Unknow command : " + currentCommand);
		}
		return new TransformerIndexTuple(line -> newTransformer.transform(transformer.transform(line)), index + 1);
	}

}
