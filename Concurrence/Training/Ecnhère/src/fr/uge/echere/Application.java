package fr.uge.echere;

public class Application {

	public static void main(String[] agrs) {
		var enchere = new Bid();
		for (int i = 1; i <= 5; i++) {
			var index = i;
			Thread.ofPlatform().start(() -> {
				for (;;) {
					try {
						Thread.sleep(1_000);
						var res = enchere.propose(index);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
