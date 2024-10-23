package fr.uge.concurrence.deadlock;

import java.util.stream.IntStream;

public class ExchangerReuse<E> {

	private String first = null;
	private String second = null;
	private State state = State.START;
	private final Object lock = new Object();

	enum State {
		START, MIDDLE, END
	}

	public String exchange(String string) throws InterruptedException {
		synchronized (lock) {

			while (state == State.END) {
				lock.wait();
			}
			if (state == State.START) {
				first = string;
				state = State.MIDDLE;
				while (state != State.END) {
					lock.wait();
				}
				state = State.START;
				return second;
			}

			if (state == State.MIDDLE) {
				second = string;
				state = State.END;
				lock.notifyAll();
			}
			return first;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var exchanger = new ExchangerReuse<String>();
		IntStream.range(0, 10).forEach(i -> {
			Thread.ofPlatform().start(() -> {
				try {
					System.out.println("thread " + i + " received from " + exchanger.exchange("thread " + i));
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			});
		});
	}
}
