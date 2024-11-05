package fr.uge.test;

public class Quizz3 {
	private int alice;
	private int bob;
	private final Object lock = new Object();

	public void go() {
		Thread.ofPlatform().start(() -> {
			synchronized (lock) {
				alice = 1;
				bob = 2;
			}
		});

		while (true) {
			synchronized (lock) {
				if (alice == 1) {
					break;
				}
			}
		}

		synchronized (lock) {
			System.out.println(alice + " " + bob);
		}
	}

	public static void main(String[] args) {
		var quizz = new Quizz3();
		quizz.go();
	}
}
