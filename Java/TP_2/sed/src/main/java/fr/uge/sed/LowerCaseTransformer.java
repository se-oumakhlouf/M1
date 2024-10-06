package fr.uge.sed;

import java.util.Locale;

final class LowerCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toLowerCase(Locale.ROOT);
	}
	
}
