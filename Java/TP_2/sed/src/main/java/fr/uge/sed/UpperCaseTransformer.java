package fr.uge.sed;

import java.util.Locale;

public record UpperCaseTransformer() implements Transformer {

	@Override
	public String transform(String line) {
		return line.toUpperCase(Locale.ROOT);
	}

}
