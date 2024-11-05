package fr.uge.test;

public class Quizz6 {
	private int alice;
	private int bob;
	private final Object lock = new Object();

	public String bob(int value) {
		synchronized (lock) {
			bob = value;
			return alice + " " + bob;
		}
	}

	public String aliceAndBob(int value1, int value2) {
		synchronized (lock) {
			alice = value1;
			bob = value2;
			return alice + " " + bob;
		}
	}

	public String two() {
		synchronized (lock) {
			return alice + " " + bob;
		}
	}

	public static void main(String[] args) {
		var quizz = new Quizz6();
		Thread.ofPlatform().start(() -> System.out.println("1) " + quizz.bob(1)));
		Thread.ofPlatform().start(() -> System.out.println("2) " + quizz.alice + " " + quizz.bob));
		Thread.ofPlatform().start(() -> System.out.println("3) " + quizz.aliceAndBob(3, 4)));
		Thread.ofPlatform().start(() -> System.out.println("4) " + quizz.two()));
	}
}
