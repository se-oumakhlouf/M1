package fr.uge.concurrence;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

public class RandomNumberGenerator {
	private final AtomicLong seed;

	public RandomNumberGenerator(long seed) {
		if (seed == 0) {
			throw new IllegalArgumentException("seed == 0");
		}
		this.seed = new AtomicLong(seed);
	}

//	public long next() { // Marsaglia's XorShift
//		for (;;) {
//			var current = seed.get();
//			var value = current ^ (current >>> 12);
//			value ^= value << 25;
//			value ^= current >>> 27;
//			if (seed.compareAndSet(current, value)) {
//				return value * 2685821657736338717L;
//			}
//		}
//	}

	public long next() {
		return seed.updateAndGet(current -> {
			var value = current ^ (current >>> 12);
			value ^= value << 25;
			value ^= current >>> 27;
			return value;
		}) * 2685821657736338717L;

	}

	public static void main(String[] args) throws InterruptedException {
		var set0 = new HashSet<Long>();
		var set1 = new HashSet<Long>();
		var set2 = new HashSet<Long>();
		var rng0 = new RandomNumberGenerator(1);
		var rng = new RandomNumberGenerator(1);

		for (int i = 0; i < 10_000; i++) {
			set0.add(rng0.next());
		}

		var thread = Thread.ofPlatform().start(() -> {
			for (var i = 0; i < 5_000; i++) {
				set1.add(rng.next());
			}
		});

		for (var i = 0; i < 5_000; i++) {
//       System.out.println(rng.next());
			set2.add(rng.next());
		}
		thread.join();

		System.out.println("set1: " + set1.size() + ", set2: " + set2.size());
		set1.addAll(set2);
		System.out.println("union (should be 10000): " + set1.size());

		System.out.println("inter (should be true): " + set0.containsAll(set1));
		set0.removeAll(set1);
		System.out.println("inter (should be 0): " + set0.size());
	}
}