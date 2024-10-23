package fr.uge.concurrence.deadlock;

public class Exchanger {

	private String first = null;
	private String second = null;
	private boolean initFirst = false;
	private boolean initSecond = false;
	private final Object lock = new Object();

	public String swap(String string) throws InterruptedException {
		synchronized (lock) {
			if (!initFirst) {
				first = string;
				initFirst = true;
				while (!initSecond) {
					lock.wait();
				}
				return second;
			}

			if (!initSecond) {
				second = string;
				initSecond = true;
				lock.notify();
			}
			return first;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var exchanger = new Exchanger();
		Thread.ofPlatform().start(() -> {
			try {
				System.out.println("thread 1 " + exchanger.swap("foo1"));
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		});
		System.out.println("main " + exchanger.swap(null));
	}
}
