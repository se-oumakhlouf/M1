package fr.uge.cyclic;

import java.util.ArrayList;
import java.util.Objects;

public class CyclicExchanger<T> {

	private final Object lock = new Object();
	private final ArrayList<T> values = new ArrayList<>();
	private final int nbParticipants;
	private int count;

	public CyclicExchanger(int nbParticipants) {
		if (nbParticipants < 0) {
			throw new IllegalArgumentException();
		}
		this.nbParticipants = nbParticipants;
	}

	public T exchange(T value) throws InterruptedException {
		synchronized (lock) {
			Objects.requireNonNull(value);
			int index = 0;
			if (count < nbParticipants) {
				index = count;
				values.add(value);
				count++;
			} else if (count > nbParticipants) {
				throw new IllegalStateException();
			}
			while (count < nbParticipants) {
				lock.wait();
			}
			lock.notifyAll();
			T res = values.get((index + 1) % nbParticipants);
			return res;
		}
	}

	public static void main(String[] args) {
		var exchanger = new CyclicExchanger<Integer>(5);

		for (int i = 0; i < 5; i++) {
			var index = i;
			Thread.ofPlatform().start(() -> {
				try {
					System.out.println("thread " + index + " envoie -> " + index);
					Thread.sleep(index);
					System.out.println("\tthread " + index + " recoit -> " + exchanger.exchange(index));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}

}
