package fr.uge.concurrence;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PokeAPI {

    private static List<String> NAMES = List.of("Pikachu", "Bulbizarre", "SalamÃ¨che", "Ronflex");
    public static int MAX_RARITY = 5;
    public static int POKEBALL_CAPACITY = 6;
    public static int POKEBALL_MAX_VALUE = 10;

    public record Pokemon(String name, int rarity) {
        public Pokemon {
            if (rarity < 0 || rarity > MAX_RARITY) {
                throw new IllegalArgumentException();
            }
        }
    };

    public record Pokeball(Pokemon pokemon, int value) {
        public Pokeball {
            Objects.requireNonNull(pokemon);
            if (value < 0 || value > POKEBALL_MAX_VALUE) {
                throw new IllegalArgumentException();
            }
        }
    }

    public record Crate(List<Pokeball> content) {
        public Crate {
            if (content.size() > POKEBALL_CAPACITY) {
                throw new IllegalArgumentException("Cannot create a crate with more than 10 pokeballs");
            }
            content = List.copyOf(content);
            if (!content.isEmpty()) {
                var value = content.get(0).value;
                if (content.stream().anyMatch(pokemon -> pokemon.value != value)) {
                    throw new IllegalArgumentException("Cannot create a crate with pokeballs of different values");
                }
            }
        }
    }

    /**
     * Simulate the capture of a Pokemon
     */

    public static Pokemon capture() throws InterruptedException {
        var rng = ThreadLocalRandom.current();
        Thread.sleep(rng.nextInt(0, 1_000));
        var name = NAMES.get(rng.nextInt(0, NAMES.size()));
        var rarity = rng.nextInt(0, MAX_RARITY + 1);
        return new Pokemon(name, rarity);
    }

    /**
     * Create a Pokeball containing a Pokemon
     */
    public static Pokeball trap(Pokemon pokemon) throws InterruptedException {
        var rng = ThreadLocalRandom.current();
        Thread.sleep(rng.nextInt(0, 1_000));
        var value = 10 - pokemon.rarity * rng.nextInt(0, 2);
        return new Pokeball(pokemon, value);
    }

    /**
     * Create a Crate containing the given pokeballs. There must be between 1 and 5
     * pokeballs All pokeballs must have the same value.
     */
    public static Crate box(List<Pokeball> pokeballs) throws InterruptedException {
        var rng = ThreadLocalRandom.current();
        Thread.sleep(rng.nextInt(0, 1_000));
        return new Crate(pokeballs);
    }
}