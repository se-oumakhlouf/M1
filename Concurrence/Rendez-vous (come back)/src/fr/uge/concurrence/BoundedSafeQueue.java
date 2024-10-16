package fr.uge.concurrence;

import java.util.ArrayDeque;
import java.util.Objects;

public class BoundedSafeQueue<V> {

	private final ArrayDeque<V> waitingQueue = new ArrayDeque<V>();;
	private final int capacity;
	
	public BoundedSafeQueue(int maxSize) {
		if (maxSize <= 0) throw new IllegalArgumentException();
		this.capacity = maxSize;
	}

	public void put(V value) throws InterruptedException {
		Objects.requireNonNull(value);
		synchronized (waitingQueue) {
			while (waitingQueue.size() == capacity) {
				waitingQueue.wait();
			}
			waitingQueue.addLast(value);
			waitingQueue.notifyAll();
		}
	}

	public V take() throws InterruptedException {
		synchronized (waitingQueue) {
			while (waitingQueue.isEmpty()) {
				waitingQueue.wait();
			}
			waitingQueue.notifyAll();
			return waitingQueue.removeFirst();
		}

	}


	public static void main(String[] args) throws InterruptedException {
		var queue = new UnboundedSafeQueue<>();
		for (int i = 0; i < 10; i++) {
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
