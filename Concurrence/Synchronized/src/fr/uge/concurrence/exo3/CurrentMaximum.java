package fr.uge.concurrence.exo3;

import java.util.Objects;
import java.util.Optional;

public class CurrentMaximum {
	
	
	final static int MAX_VALUE = 10000;
	private static int max_courant;
	private final Object lock = new Object();
	
	public CurrentMaximum() {
		synchronized(lock) {
			max_courant = -1;
		}
	}
	
	public void propose(int value) {
		Objects.checkIndex(value, MAX_VALUE);
		synchronized (lock) {
			if (max_courant == -1 || value > max_courant) {
				max_courant = value;
			}
		}
	}
	
	public Optional<Integer> currentMax() {
		synchronized (lock) {
			if (max_courant == -1) return Optional.empty();
			return Optional.of(max_courant);
		}
	}

}
