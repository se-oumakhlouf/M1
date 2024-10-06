package fr.uge.sed;

final class StarTransformer implements Transformer {
	
	private int stars;

	public StarTransformer(int stars) {
		if (stars > 10 || stars < 0) {
			throw new IllegalArgumentException("0 < stars < 10");
		}
		this.stars = stars;
	}
	
	public final String transform(String line) {
		return line.replace("*", "*".repeat(stars));
	}
}
