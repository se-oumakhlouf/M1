package fr.uge.interruption;

import java.util.HashMap;

public class ThePriceIsRight {

	private final int candidats;
	private final int justePrix;
	private final HashMap<String, Integer> mapPrix;
	private int inscriptions;
	private int minDistance;
	private final Object lock = new Object();

	public ThePriceIsRight(int price, int participants) {
		synchronized (lock) {
			if (price <= 0 || participants <= 0) {
				throw new IllegalArgumentException("price < 0 || participants < 0");
			}
			this.candidats = participants;
			this.justePrix = price;
			mapPrix = new HashMap<>();
			minDistance = Integer.MAX_VALUE;
		}
	}

	public boolean propose(int prix) {
		synchronized (lock) {
			inscriptions++;
			if (inscriptions > candidats) {
				return false;
			}
			if (inscriptions <= candidats) {
				var dist = distance(prix);
				mapPrix.putIfAbsent(Thread.currentThread().getName(), dist);
				minDistance = minDistance > dist ? dist : minDistance;
			}
			if (inscriptions == candidats) {
				lock.notifyAll();
			}
			while (inscriptions < candidats) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					var name = Thread.currentThread().getName();
					mapPrix.remove(name);
					inscriptions = candidats + 1;
					lock.notifyAll();
					return false;
				}
			}
			var threadRightPrice = mapMin();
			if (Thread.currentThread().getName().equals(threadRightPrice)) {
				return true;
			}
			return false;
		}

	}

	private int distance(int price) {
		return Math.abs(price - justePrix);
	}

	private String mapMin() {
		synchronized (lock) {
			String thread = null;
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
