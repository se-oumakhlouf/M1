package fr.uge.sed;

import java.util.Objects;

public record TransformerIndexTuple(Transformer transformer, int index) {
	
	public TransformerIndexTuple {
		Objects.requireNonNull(transformer);
		if (index < 0) {
			throw new IllegalArgumentException("Negative index");
		}
	}
}
