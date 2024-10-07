package fr.uge.sed;

public record StarTransformer(int stars) implements Transformer{
	
	public StarTransformer(int stars) {
		this.stars = stars;
	}

	@Override
	public String transform(String line) {
		return line.replace("*", "*".repeat(stars));
	}
}
