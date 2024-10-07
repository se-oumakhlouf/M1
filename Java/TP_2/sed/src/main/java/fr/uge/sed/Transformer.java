package fr.uge.sed;

@FunctionalInterface
public interface Transformer {
	String transform(String line);
}
