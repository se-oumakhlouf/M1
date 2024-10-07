package fr.uge.sed;

import java.util.Locale;

public record LowerCaseTransformer() implements Transformer {

	@Override
	public String transform(String line) {
		return line.toLowerCase(Locale.ROOT);
	}

}
