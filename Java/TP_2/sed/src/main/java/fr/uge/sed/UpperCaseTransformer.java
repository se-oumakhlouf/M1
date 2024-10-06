package fr.uge.sed;

import java.util.Locale;

final class UpperCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toUpperCase(Locale.ROOT);
	}

}
