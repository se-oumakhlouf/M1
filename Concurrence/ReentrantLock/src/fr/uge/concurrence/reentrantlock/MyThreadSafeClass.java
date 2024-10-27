package fr.uge.concurrence.reentrantlock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadSafeClass {
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition conditionSum = lock.newCondition();
	private long sum = 0;
	private int count = 0;

	public long submit() {
		return 1_000_000_000L + ThreadLocalRandom.current().nextLong(1_000_000_000L);
	}

	public void addValue(long value) throws InterruptedException {
		lock.lock();
		try {
			if (count < 10) {
				count++;
				sum += value;
			}
			if (count == 10)
				conditionSum.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public long returnSum() throws InterruptedException {
		lock.lock();
		try {
			while (count < 10) {
				conditionSum.await();
			}
			return this.sum;
		} finally {
			lock.unlock();
		}
	}

}
