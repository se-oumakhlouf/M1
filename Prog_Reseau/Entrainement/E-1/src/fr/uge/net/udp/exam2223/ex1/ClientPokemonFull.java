package fr.uge.net.udp.exam2223.ex1;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientPokemonFull {

	private static final Charset UTF8 = StandardCharsets.UTF_8;
	private static final Logger logger = Logger.getLogger(ClientPokemonFull.class.getName());

	private record Pokemon(String name, Map<String, Integer> characteristics) {
		public Pokemon {
			Objects.requireNonNull(name);
			characteristics = Map.copyOf(characteristics);
		}

		@Override
		public String toString() {
			var stringBuilder = new StringBuilder();
			stringBuilder.append(name);
			for (var entry : characteristics.entrySet()) {
				stringBuilder.append(';').append(entry.getKey()).append(':').append(entry.getValue());
			}
			return stringBuilder.toString();
		}
	}

	private final String inFilename;
	private final String outFilename;
	private final InetSocketAddress server;
	private final DatagramChannel datagramChannel;
	private static final int TIMEOUT = 300;

	public static void usage() {
		System.out.println("Usage : ClientPokemon in-filename out-filename host port ");
	}

	public ClientPokemonFull(String inFilename, String outFilename, InetSocketAddress server) throws IOException {
		this.inFilename = Objects.requireNonNull(inFilename);
		this.outFilename = Objects.requireNonNull(outFilename);
		this.server = server;
		this.datagramChannel = DatagramChannel.open();
	}

	public void launch() throws IOException, InterruptedException {
		try {
			datagramChannel.bind(null);
			// Read all lines of inFilename opened in UTF-8
			var pokemonNames = Files.readAllLines(Path.of(inFilename), UTF8);
			// List of Pokemon to write to the output file
			var pokemons = new ArrayList<Pokemon>();

			var sendBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
			var receiveBuffer = ByteBuffer.allocate(2048).order(ByteOrder.BIG_ENDIAN);

			var queue = new ArrayBlockingQueue<Pokemon>(10);

			var sender = Thread.ofPlatform().start(() -> {
				try {
					int currentPokemon = 0;
					while (!Thread.interrupted() && currentPokemon < pokemonNames.size()) {
						sendBuffer.clear();

						var nameBuffer = UTF8.encode(pokemonNames.get(currentPokemon));
						var size = nameBuffer.limit();
						if (sendBuffer.limit() < Integer.BYTES + size * Byte.BYTES) {
							// ignore le pokemon si le paquet dépasse les 1024 octets
							logger.info("Taille du packet d'envoie > 1024. Taille du packet: " + Integer.BYTES + size * Byte.BYTES);
							continue;
						}

						sendBuffer.putInt(size);
						sendBuffer.put(nameBuffer);

						sendBuffer.flip();
						datagramChannel.send(sendBuffer, server);

						Pokemon pokemon;
						int essais = 0;
						while ((pokemon = queue.poll(TIMEOUT, TimeUnit.MILLISECONDS)) == null) {
							if (essais >= 3) {
								currentPokemon--;
								break;
							}
							sendBuffer.clear();
							sendBuffer.putInt(size);
							sendBuffer.put(nameBuffer);
							sendBuffer.flip();
							datagramChannel.send(sendBuffer, server);
							essais++;
						}
						currentPokemon++;
					}
				} catch (IOException | InterruptedException e) {
					return;
				}

			});

			var receiver = Thread.ofPlatform().start(() -> {
				try {
					while (!Thread.interrupted() && pokemons.size() < pokemonNames.size()) {
						receiveBuffer.clear();
						datagramChannel.receive(receiveBuffer);

						receiveBuffer.flip();

						var nameBuffer = ByteBuffer.allocate(2048);
						Byte octet;

						while (receiveBuffer.hasRemaining() && (octet = receiveBuffer.get()) != 0) {
							nameBuffer.put(octet);
						}
						nameBuffer.flip();
						var nameReceived = UTF8.decode(nameBuffer).toString();

						var newCharacteristics = new HashMap<String, Integer>();

						while (receiveBuffer.hasRemaining()) {

							var storageBuffer = ByteBuffer.allocate(2048);

							while (receiveBuffer.hasRemaining() && ((octet = receiveBuffer.get()) != 0)) {
								storageBuffer.put(octet);
							}

							if (!storageBuffer.hasRemaining()) {
								logger.warning("Nom de caractéristique vide, paquet ignoré.");
								continue;
							}
							storageBuffer.flip();
							var characteristicName = UTF8.decode(storageBuffer).toString();
							System.out.println(characteristicName);

							if (receiveBuffer.remaining() < Integer.BYTES) {
								logger.warning("Pas assez de place pour la valeur de " + characteristicName);
								continue;
							}
							var value = receiveBuffer.getInt();
							newCharacteristics.put(characteristicName, value);

						}
						var pokemon = new Pokemon(nameReceived, newCharacteristics);
						System.out.println(pokemon.toString());
						pokemons.add(pokemon);
						queue.put(pokemon);
					}
				} catch (IOException | InterruptedException e) {
					return;
				}

			});

			receiver.join();
			sender.join();

			if (pokemons.size() == pokemonNames.size()) {
				receiver.interrupt();
				sender.interrupt();
			}

			// Convert the pokemons to strings and write then in the output file
			var lines = pokemons.stream().map(Pokemon::toString).toList();
			Files.write(Paths.get(outFilename), lines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
		} finally {
			datagramChannel.close();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 4) {
			usage();
			return;
		}

		var inFilename = args[0];
		var outFilename = args[1];
		var server = new InetSocketAddress(args[2], Integer.parseInt(args[3]));

		// Create client with the parameters and launch it
		new ClientPokemonFull(inFilename, outFilename, server).launch();
	}
}