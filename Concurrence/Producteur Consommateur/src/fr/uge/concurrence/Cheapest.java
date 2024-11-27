package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class Cheapest {

	private final String item;
	private final int timeoutMilliPerRequest;

	public Cheapest(String item, int timeoutMilliPerRequest) {
		this.item = item;
		this.timeoutMilliPerRequest = timeoutMilliPerRequest;
	}

	/**
	 * @return the cheapest price for item if it is sold
	 */
	public Optional<Answer> retrieve() throws InterruptedException {
		var queue = new ArrayBlockingQueue<Optional<Answer>>(10);
		var sites = Request.getAllSites();
		var answers = new ArrayList<Answer>();
		for (var site : sites) {
			Thread.ofPlatform().start(() -> {
				var request = new Request(site, item);
				try {
					var answer = request.request(timeoutMilliPerRequest);
					queue.put(answer);
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			});
		}

		for (int i = 0; i < sites.size(); i++) {
			var elem = queue.take();
			if (elem.isPresent()) {
				answers.add(elem.get());
			}
		}

		return answers.stream().min(Comparator.comparingInt(Answer::price));
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new Cheapest("pikachu", 3_000);
		var answer = agregator.retrieve();
		System.out.println("Cheapest : " + answer); // Optional[pikachu@ebay.fr : 891]
	}
}