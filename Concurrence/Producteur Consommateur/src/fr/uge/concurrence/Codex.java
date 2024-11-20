package fr.uge.concurrence;

import java.util.concurrent.ArrayBlockingQueue;

public class Codex {

	public static void main(String[] args) {

		var archiveQueue = new ArrayBlockingQueue<String>(10);
		var decodeQueue = new ArrayBlockingQueue<String>(10);

		for (int i = 0; i < 3; i++) {
			Thread.ofPlatform().start(() -> {
				for (;;) {
					try {
						var message = CodeAPI.receive();
						System.out.println(message);
						decodeQueue.put(message);
					} catch (InterruptedException e) {
						throw new AssertionError();
					}
				}
			});
		}

		for (int i = 0; i < 2; i++) {
			Thread.ofPlatform().start(() -> {
				for (;;) {
					try {
						var message = CodeAPI.decode(decodeQueue.take());
						System.out.println(message);
						archiveQueue.put(message);
					} catch (InterruptedException e) {
						throw new AssertionError();
					}
				}
			});
		}

		Thread.ofPlatform().start(() -> {
			for (;;) {
				try {
					System.out.println(archiveQueue.take());
				} catch (InterruptedException e) {
					throw new AssertionError();
				}
			}
		});

	}

}
