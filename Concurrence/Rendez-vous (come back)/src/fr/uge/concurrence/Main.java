package fr.uge.concurrence;

public class Main {

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