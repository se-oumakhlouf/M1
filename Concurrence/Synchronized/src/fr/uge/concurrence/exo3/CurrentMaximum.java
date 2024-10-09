package fr.uge.concurrence.exo3;

import java.util.Optional;

public class CurrentMaximum {
	
	final static int MAX_VALUE = 10000;
	private static int max_courant = -1;
	private final Object lock = new Object();
	
	public final int valPropose(int value) {
		synchronized(lock) {
			if (value > max_courant) max_courant = value;
			return value;
		}
	}
	
	public static Optional<Integer> currentMax() {
		return Optional.of(max_courant);
	}

}
