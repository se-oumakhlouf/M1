package fr.uge.concurrence.reentrantlock;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundedSafeQueue<V> {
	
	private final ArrayDeque<V> fifo = new ArrayDeque<>();
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	
	private void add(V value) {
		lock.lock();
		try {
			Objects.requireNonNull(value);
			fifo.add(value);
			condition.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public V take() throws InterruptedException {
		lock.lock();
		try {
			while (fifo.isEmpty()) {
				condition.await();
			}
			return fifo.remove();
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		var queue = new UnboundedSafeQueue<>();
		for (int i = 0; i < 3; i++) {
				Thread.ofPlatform().start(() -> {
					try {
						Thread.sleep(2_000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					queue.add(Thread.currentThread().getName());
				});
		}
		while (true) {
			System.out.println("First element : " + queue.take());
		}
	}
}
