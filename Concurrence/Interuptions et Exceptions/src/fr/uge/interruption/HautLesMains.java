package fr.uge.interruption;

import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

public class HautLesMains {

//  public static void main(String[] args) throws InterruptedException {
//    var thread = Thread.ofPlatform().start(() -> {
//      for (var i = 1;; i++) {
//        try {
//          Thread.sleep(1_000);
//          System.out.println("Thread slept " + i + " seconds.");
//        } catch (InterruptedException e) {
//          return;
//        }
//      }
//    });
//    Thread.sleep(5_100); // ne lève jamais d'exception car on interrompt jamais le Thread Main
//    thread.interrupt();
//  }

	public static boolean isPrime(long candidate) throws InterruptedException {
		if (candidate <= 1) {
			return false;
		}
		for (var i = 2L; i <= Math.sqrt(candidate); i++) {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			if (candidate % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static OptionalLong findPrime() throws InterruptedException {
		var generator = ThreadLocalRandom.current();
		for (;;) {
			var candidate = generator.nextLong();
			if (isPrime(candidate)) {
				return OptionalLong.of(candidate);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var thread = Thread.ofPlatform().start(() -> {
			try {
				System.out.println("Found a random prime : " + findPrime().orElseThrow());
			} catch (InterruptedException e) {
				System.out.println("STOP");
			}
		});
		Thread.sleep(5_000);
		thread.interrupt();
	}
}
