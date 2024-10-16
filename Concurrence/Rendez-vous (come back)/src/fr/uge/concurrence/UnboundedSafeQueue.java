package fr.uge.concurrence;

import java.util.ArrayDeque;
import java.util.Objects;

public class UnboundedSafeQueue<V> {

	private final ArrayDeque<V> waitingQueue = new ArrayDeque<V>();;
	private final Object lock = new Object();

	public void add(V value) {
		Objects.requireNonNull(value);
		synchronized (lock) {
			waitingQueue.addLast(value);
			lock.notify();
		}
	}

	public V take() throws InterruptedException {
		synchronized (lock) {
			while (waitingQueue.isEmpty()) {
				lock.wait();
			}
			return waitingQueue.removeFirst();
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
