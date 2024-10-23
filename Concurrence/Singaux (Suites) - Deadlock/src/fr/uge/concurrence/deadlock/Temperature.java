package fr.uge.concurrence.deadlock;

public class Temperature {

	private final Object lock = new Object();
	private int count;
	private int sum;
	private final int nbRooms;

	public Temperature(int rooms) {
		if (rooms < 1)
			throw new IllegalArgumentException();
		this.nbRooms = rooms;
	}

	public void add(int temperature) {
		synchronized (lock) {
			if (count == nbRooms)
				throw new IllegalStateException();
			sum += temperature;
			count++;
			if (count == nbRooms)
				lock.notifyAll();
		}
	}

	public double averageTemperatures() throws InterruptedException {
		synchronized (lock) {
			while (count != nbRooms) {
				lock.wait();
			}
			var avg = 1. * sum / count;
			count = 0;
			sum = 0;
			return avg;
		}
	}

}
