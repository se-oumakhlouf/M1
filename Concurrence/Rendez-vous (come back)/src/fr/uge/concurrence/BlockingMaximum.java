package fr.uge.concurrence;

public class BlockingMaximum {

	final static int MAX_VALUE = 10_000;
	private int max = -1;

	private final Object lock = new Object();
	
	// Met à jour la valeur de max en cas de nouveau max et notify
	public void UpdateValue(int newValue) {
		synchronized (lock) {
			if (newValue > max) {
				max = newValue;
				lock.notify();
			}
		}
	}
	
	// En attente tant que max n'a pas eu sa première valeur > 8000
	// Affiche un message lorsque max > 8000 pour la première fois
	public int currentMax() throws InterruptedException {
		synchronized (lock) {
			while (max < 8000) {
				lock.wait();
				if (max > 8000) {
					System.out.println("Un thread a tiré une valeur supérieur à 8000 : " + this.max);
				}
			}
			return max;
		}
	}
}
