package fr.uge.json;

import static java.util.Objects.requireNonNull;

@JSONProperty(value = "Alien")
public record Alien(int age, String planet) {
	public Alien {
		if (age < 0) {
			throw new IllegalArgumentException("negative age");
		}
		requireNonNull(planet);
	}
}