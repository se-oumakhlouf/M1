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
		var cur = new CurrentMaximum();

		for (var j = 0; j < nbThreads; j++) {
			threads[j] = Thread.ofPlatform().start(() -> {
				for (;;) {
					checkedSleep(1000);
					var value = ThreadLocalRandom.current().nextInt(CurrentMaximum.MAX_VALUE);
					System.out.println(Thread.currentThread() + " a tiré " + value);
					cur.propose(value);
				}
			});
		}

		for (int i = 0; i < 10; i++) {
			checkedSleep(1000);
			System.out.println("Max courant : " + cur.currentMax().orElse(-1));
		}

		for (var thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new AssertionError();
			}
		}
		// System.out.println("Max final : ");
	}
}