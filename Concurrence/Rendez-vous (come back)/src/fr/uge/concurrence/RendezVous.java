package fr.uge.concurrence;

import java.util.Objects;

public class RendezVous<V> {
	private V value;
	private final Object lock = new Object();

	private V get() throws InterruptedException {
		synchronized (lock) {
			while(value == null) {
				lock.wait();
			}
		}
		return value;
	}

	private void set(V value) {
		Objects.requireNonNull(value);
		synchronized (lock) {
			this.value = value;
			lock.notify();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
    var rdv = new RendezVous<String>();
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(20_000);
        rdv.set("Message");
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println(rdv.get());
  }
}
