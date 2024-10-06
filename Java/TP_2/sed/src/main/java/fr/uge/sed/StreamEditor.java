package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public final class StreamEditor {
	
	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		return switch (command.charAt(0)) {
			case 'u' -> new UpperCaseTransformer();
			case 'l' -> new LowerCaseTransformer();
			case '*' -> new StarTransformer(Integer.parseInt(command.substring(1)));
			default -> throw new IllegalArgumentException("Unexpected value: " + command.charAt(0));
		};
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				switch (transformer) {
					case UpperCaseTransformer upper -> line = upper.transform(line);
					case LowerCaseTransformer lower -> line = lower.transform(line);
					case StarTransformer star -> line = star.transform(line);
					default -> throw new IllegalArgumentException("Unexpected value: " + transformer);
				}
				writer.write(line);
				writer.write("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

}
