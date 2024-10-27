package fr.uge.concurrence.reentrantlock;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedSafeQueue<V> {
	private final ArrayDeque<V> fifo = new ArrayDeque<>();
	private final int capacity;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition conditionAdd = lock.newCondition();
	private final Condition conditionTake = lock.newCondition();

	public BoundedSafeQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException();
		}
		this.capacity = capacity;
	}

	public void add(V value) throws InterruptedException {
		lock.lock();
		try {
			while (fifo.size() == capacity) {
				conditionAdd.await();
			}
			fifo.add(value);
			conditionTake.signal();
		} finally {
			lock.unlock();
		}
	}

	public V take() throws InterruptedException {
		lock.lock();
		try {
			while (fifo.isEmpty()) {
				conditionTake.await();
			}
			conditionAdd.signal();
			return fifo.remove();
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		int capacity = 3;
		var queue = new BoundedSafeQueue<>(capacity);
		for (int i = 0; i < capacity; i++) {
				Thread.ofPlatform().start(() -> {
					try {
						Thread.sleep(2_000);
						queue.add(Thread.currentThread().getName());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
		}
		while (true) {
			System.out.println("First element : " + queue.take());
		}
	}

}
