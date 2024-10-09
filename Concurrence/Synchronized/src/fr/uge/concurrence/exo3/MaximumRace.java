package fr.uge.concurrence.exo3;

import java.util.concurrent.ThreadLocalRandom;

public class MaximumRace {

	private static void checkedSleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new AssertionError();
		}
	}

	public static void main(String[] args) {
		var nbThreads = 4;
		var threads = new Thread[nbThreads];

		for (var j = 0; j < nbThreads; j++) {
			threads[j] = Thread.ofPlatform().start(() -> {
				for (var i = 0; i < 10; i++) {
					checkedSleep(1000);
					var value = ThreadLocalRandom.current().nextInt(CurrentMaximum.MAX_VALUE);
					System.out.println(Thread.currentThread() + " a tiré " + value);
				}
			});
		}

		for (int i = 0; i < 10; i++) {
			checkedSleep(1000);
			System.out.println("Max courant : " CurrentMaximum.currentMax());
		}

		for (var thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new AssertionError();
			}
		}
		System.out.println("Max final : ");
	}
}