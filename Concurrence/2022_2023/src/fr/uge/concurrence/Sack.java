package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Sack {
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition sackFull = lock.newCondition();
	private final Condition sackEmpty = lock.newCondition();
	private final ArrayList<Integer> sack = new ArrayList<Integer>();
	private final int maxWeight;
	private int sumWeight;
	private int waitingWeights;
	
	public Sack(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	public void putGift(int weight) throws InterruptedException {
		lock.lock();
		try {
			while (maxWeight < sumWeight + weight) {
				sackFull.await();
			}
			sack.add(weight);
			sumWeight += weight;
			sackEmpty.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public int takeGift() throws InterruptedException {
		lock.lock();
		try {
			while (sack.isEmpty()) {
				sackEmpty.await();
			}
			var weightLast = sack.removeLast();
			sumWeight -= weightLast;
			sackFull.signalAll();
			return weightLast;
		} finally {
			lock.unlock();
		}
	}
	
	public List<Integer> takeGiftUntil(int weight) throws InterruptedException {
		lock.lock();
		try {
			List<Integer> takenGifts = new ArrayList<Integer>();
			waitingWeights += weight;
			var currentWeight = 0;
			while (currentWeight < weight) {
				int gift = takeGift();
				takenGifts.add(gift);
				currentWeight += gift;
			}
			waitingWeights -= weight;
			return takenGifts;
		} finally {
			lock.unlock();
		}
	}
	
	public int weightNeeded() {
		lock.lock();
		try {
			return waitingWeights;
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] agrs) {
		var sack = new Sack(500);
		
		// question 2
		Thread.ofPlatform().start(() -> {
			while (true) {
				try {
					Thread.sleep(100);
					sack.putGift(10);
					sack.putGift(5);
				} catch (InterruptedException e) {
					return;
				}
				
			}
		});
		
		// question 2
		Thread.ofPlatform().start(() -> {
			while (true) {
				try {
					Thread.sleep(200);
					int weight = sack.takeGift();
					System.out.println("Took a gift -> weight  = " + weight);
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		
		// question 4 
		Thread.ofPlatform().start(() -> {
			for (;;) {
				try {
					Thread.sleep(1000);
					var gifts = sack.takeGiftUntil(1000);
					int sum = gifts.stream().mapToInt(Integer::intValue).sum();
					System.out.println("Gifts sum -> " + sum);
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		
		// question 4
		Thread.ofPlatform().start(() -> {
			for(;;) {
				try {
					Thread.sleep(500);
					System.out.println("Weight needed -> " + sack.weightNeeded());
				} catch (InterruptedException e) {
					return;
				}
				
			}
		});
	}
}
