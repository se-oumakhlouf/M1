package fr.uge.concurrence;

import java.util.HashMap;
import java.util.Objects;


// Faire test de VoteTest


public class Vote {

	private final HashMap<String, Integer> votes = new HashMap<>();
	private final int participants;
	private int countVotes = 0;
	private final Object lock = new Object();

	public Vote(int participants) {
		if (participants < 0) {
			throw new IllegalArgumentException("Nombre de votants < 0 : " + participants);
		}
		this.participants = participants;
	}

	private String computeWinner() {
		var score = -1;
		String winner = null;
		for (var e : votes.entrySet()) {
			var key = e.getKey();
			var value = e.getValue();
			if (value > score || (value == score && key.compareTo(winner) < 0)) {
				winner = key;
				score = value;
			}
		}
		return winner;
	}

	private void updateVotes() {
		synchronized (lock) {
			if (countVotes == participants)
				lock.notifyAll();
		}
	}

	public String vote(String voted) throws InterruptedException {
		Objects.requireNonNull(voted);
		synchronized (lock) {
			countVotes++;
			while (countVotes < participants) {
				lock.wait();
			}
			votes.compute(voted, (key, val) -> val == null ? 1 : val + 1);
			updateVotes();
			return computeWinner();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var vote = new Vote(4);
		Thread.ofPlatform().start(() -> {
			try {
				Thread.sleep(2_000);
				System.out.println("The winner is " + vote.vote("un"));
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		});
		Thread.ofPlatform().start(() -> {
			try {
				Thread.sleep(1_500);
				System.out.println("The winner is " + vote.vote("zero"));
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		});
		Thread.ofPlatform().start(() -> {
			try {
				Thread.sleep(1_000);
				System.out.println("The winner is " + vote.vote("un"));
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		});
		System.out.println("The winner is " + vote.vote("zero"));
	}

}
