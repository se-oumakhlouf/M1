package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fr.uge.concurrence.PokeAPI;
import fr.uge.concurrence.PokeAPI.Pokemon;
import fr.uge.concurrence.PokeAPI.Pokeball;

public class PokemonFactory {

	public static void main(String[] args) throws InterruptedException {

		var capturedQueue = new ArrayBlockingQueue<Pokemon>(20);
		var commonQueue = new ArrayBlockingQueue<Pokemon>(20);
		var rareQueue = new ArrayBlockingQueue<Pokemon>(20);
		var pokeballQueues = new HashMap<Integer, BlockingQueue<Pokeball>>();

		for (int i = 0; i <= 10; i++) {
			pokeballQueues.put(i, new ArrayBlockingQueue<>(20));
		}

		for (int i = 0; i < 5; i++) {
			Thread.ofPlatform().start(() -> {
				while (true) {
					try {
						Pokemon pokemon = PokeAPI.capture();
						System.out.println("Capture-Thread-" + Thread.currentThread().threadId() + " -> Captured: " + pokemon);
						capturedQueue.put(pokemon);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			});
		}

		Thread.ofPlatform().start(() -> {
			while (true) {
				try {
					Pokemon pokemon = capturedQueue.take();
					if (pokemon.rarity() < 5) {
						System.out.println("Sorting-Thread-Common -> Sorted: " + pokemon);
						commonQueue.put(pokemon);
					} else {
						System.out.println("Sorting-Thread-Rare -> Sorted: " + pokemon);
						rareQueue.put(pokemon);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		Thread.ofPlatform().start(() -> {
			while (true) {
				try {
					Pokemon pokemon = commonQueue.take();
					Pokeball pokeball = PokeAPI.trap(pokemon);
					System.out.println("Trap-Thread-Common -> Trapped: " + pokeball);
					pokeballQueues.get(pokeball.value()).put(pokeball);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		Thread.ofPlatform().start(() -> {
			while (true) {
				try {
					Pokemon pokemon = rareQueue.take();
					Pokeball pokeball = PokeAPI.trap(pokemon);
					System.out.println("Trap-Thread-Rare -> Trapped: " + pokeball);
					pokeballQueues.get(pokeball.value()).put(pokeball);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		for (int value = 0; value <= 10; value++) {
			int i = value;
			Thread.ofPlatform().start(() -> {
				List<Pokeball> crateContents = new ArrayList<>();
				while (true) {
					try {
						Pokeball pokeball = pokeballQueues.get(i).take();
						crateContents.add(pokeball);
						if (crateContents.size() == 6) {
							var crate = PokeAPI.box(crateContents);
							System.out.println("Crate-Thread-" + i + " -> Created: " + crate);
							crateContents.clear();
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			});
			Thread.ofPlatform().start(() -> {
				List<Pokeball> crateContents = new ArrayList<>();
				while (true) {
					try {
						Pokeball pokeball = pokeballQueues.get(i).take();
						crateContents.add(pokeball);
						if (crateContents.size() == 6) {
							var crate = PokeAPI.box(crateContents);
							System.out.println("Crate-Thread-" + i + " -> Created: " + crate);
							crateContents.clear();
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			});
		}
	}
}
