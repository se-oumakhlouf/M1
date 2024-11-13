package fr.uge.interruption;

import java.util.HashMap;

public class ThePriceIsRight {

	private final int candidats;
	private final int justePrix;
	private final HashMap<Integer, Integer> mapPrix;
	private int inscriptions;
	private int minProposition;
	private final Object lock = new Object();

	public ThePriceIsRight(int price, int participants) {
		if (price <= 0 || participants <= 0) {
			throw new IllegalArgumentException("price < 0 || participants < 0");
		}
		this.candidats = participants;
		this.justePrix = price;
		mapPrix = new HashMap<>();
	}

	public boolean propose(int prix) throws InterruptedException {
		synchronized (lock) {
			if (inscriptions < inscriptions) {
				mapPrix.put(candidats, prix);
				inscriptions++;
				if (minProposition > prix) {
					this.minProposition = prix;
				}
			} else {
				lock.notifyAll();
			}
			while (inscriptions < candidats) {
				lock.wait();
			}
			return false;
		}

	}
}
