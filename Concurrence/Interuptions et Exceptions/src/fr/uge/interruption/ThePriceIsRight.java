package fr.uge.interruption;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ThePriceIsRight {

	private final int candidats;
	private final int justePrix;
	private final LinkedHashMap<Thread, Integer> mapPrix = new LinkedHashMap<>();
	private boolean end;
	private int inscriptions;
	private final Object lock = new Object();

	public ThePriceIsRight(int price, int participants) {
		if (price <= 0 || participants <= 0) {
			throw new IllegalArgumentException("price < 0 || participants < 0");
		}
		this.candidats = participants;
		this.justePrix = price;
	}

	public boolean propose(int prix) {
		synchronized (lock) {
			if (end) {
				return false;
			}
			if (inscriptions < candidats) {
				mapPrix.put(Thread.currentThread(), distance(prix));
				inscriptions++;
				if (inscriptions == candidats) {
					lock.notifyAll();
				}
			} else {
				return false;
			}
			while (inscriptions < candidats && !end) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					mapPrix.remove(Thread.currentThread());
					end = true;
					lock.notifyAll();
					return false;
				}
			}
			var minEntry = mapMin();
			return minEntry != null && Thread.currentThread().equals(minEntry.getKey());
		}
	}

	private int distance(int price) {
		return Math.abs(price - justePrix);
	}

	private Entry<Thread, Integer> mapMin() {
		synchronized (lock) {
			return mapPrix.entrySet().stream().min(Map.Entry.comparingByValue()).orElse(null);
		}
	}

}