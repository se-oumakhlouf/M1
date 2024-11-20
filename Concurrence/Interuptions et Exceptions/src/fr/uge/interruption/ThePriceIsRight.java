package fr.uge.interruption;

import java.util.HashMap;

public class ThePriceIsRight {

	private final int candidats;
	private final int justePrix;
	private final HashMap<Thread, Integer> mapPrix; // Linked
	private int inscriptions;
	private final Object lock = new Object();

	public ThePriceIsRight(int price, int participants) {
			if (price <= 0 || participants <= 0) {
				throw new IllegalArgumentException("price < 0 || participants < 0");
			}
			this.candidats = participants;
			this.justePrix = price;
			mapPrix = new HashMap<>();
	}

	public boolean propose(int prix) {
		synchronized (lock) {
			if (inscriptions < candidats) {
				var dist = distance(prix);
				mapPrix.putIfAbsent(Thread.currentThread(), dist); // faux
				inscriptions++;
				if (inscriptions == candidats) {
					lock.notifyAll();
				}
			} else {
				return false;
			}
			while (inscriptions < candidats) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					mapPrix.remove(Thread.currentThread());
					inscriptions = candidats + 1; // flag boolean
					lock.notifyAll();
					return false;
				}
			}
			var threadRightPrice = mapMin();
			if (Thread.currentThread().equals(threadRightPrice)) {
				return true;
			}
			return false;
		}

	}

	private int distance(int price) {
		return Math.abs(price - justePrix);
	}

	
	// stream
	private Thread mapMin() {
		synchronized (lock) {
			Thread thread = null;
			var min = Integer.MAX_VALUE;
			for (var entry : mapPrix.entrySet()) {
				var price = entry.getValue();
				if (min > price) {
					min = price;
					thread = entry.getKey();
				}
			}
			return thread;
		}
	}

}
