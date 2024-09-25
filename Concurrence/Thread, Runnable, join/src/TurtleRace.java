
public class TurtleRace {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("On your mark");
		Thread.sleep(30_000);
		System.out.println("Go!");
		int[] times = { 25_000, 10_000, 20_000, 5_000, 50_000, 60_000 };

		var count = 0;
		for (var time : times) {
			int turtleIndex = count++;
			Runnable runnable = () -> {
				try {
					Thread.sleep(time);
					System.out.println("Turtle " + turtleIndex + " has finished");
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			};

			Thread.ofPlatform().start(runnable);
		}
	}

}

// Une fois les threads créés et démarrés, le thread main se termine
// La JVM s'éteint lorsque tous les threads se sont terminés
