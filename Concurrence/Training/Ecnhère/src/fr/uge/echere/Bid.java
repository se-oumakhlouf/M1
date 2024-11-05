package fr.uge.echere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Bid {

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition minCond = lock.newCondition();
	private final ArrayList<Integer> bids = new ArrayList<>(5);
	private int min;
	private int indexThread;

	public Bid() {
		synchronized (lock) {
			for (int i = 0; i < 5; i++) {
				bids.add(0);
			}
			this.min = Integer.MAX_VALUE;
		}
	}

	public int propose(int value) throws InterruptedException {
		lock.lock();
		try {
			indexThread = value;
			if (bids.contains(0)) {
				bids.set(indexThread - 1, value);
				System.out.println("Thread " + indexThread + " proposes " + value);
				return value;
			}
			System.out.println(bids);

			while (value > min) {
				minCond.await();
			}
			if (value <= Collections.min(bids)) {
				value = valueIsNewMin(value);
				minCond.signalAll();
				bids.set(indexThread - 1, value);
			}
			if (bids.contains(value)) {
				value = valueAlreadyExists(value);
			}
			bids.set(indexThread - 1, value);
			min = Collections.min(bids);
			System.out.println("Thread " + indexThread + " proposes " + value);
			return value;
		} finally {
			lock.unlock();
		}
	}

	public int valueAlreadyExists(int value) {
		lock.lock();
		try {
			if (bids.contains(value)) {
				System.out.println("Thread " + indexThread + " proposed value " + value + " was rejected");
			}
			while (bids.contains(value)) {
				value++;
			}
			return value;
		} finally {
			lock.unlock();
		}
	}

	public int valueIsNewMin(int value) throws InterruptedException {
		lock.lock();
		try {
			System.out.println(
					"Thread " + indexThread + " was unblocked because its proposed value " + value + " is now the smallest.");
			return moyenneBids();
		} finally {
			lock.unlock();
		}
	}

	private int moyenneBids() {
		lock.lock();
		try {
			return bids.stream().mapToInt(b -> b).sum() / bids.size();
		} finally {
			lock.unlock();
		}
	}

}
