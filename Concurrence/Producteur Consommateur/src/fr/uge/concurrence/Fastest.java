package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class Fastest {

	private final String item;
	private final int timeoutMilliPerRequest;

	public Fastest(String item, int timeoutMilliPerRequest) {
		this.item = item;
		this.timeoutMilliPerRequest = timeoutMilliPerRequest;
	}

	/**
	 * @return the cheapest price for item if it is sold
	 */
	public Optional<Answer> retrieve() throws InterruptedException {
		var queue = new ArrayBlockingQueue<Optional<Answer>>(10);
		var threads = new ArrayList<Thread>();
		var sites = Request.getAllSites();
		for (var site : sites) {
			var thread = Thread.ofPlatform().start(() -> {
				var request = new Request(site, item);
				try {
					var answer = request.request(timeoutMilliPerRequest);
					queue.put(answer);
				} catch (InterruptedException e) {
					return;
				}
			});
			threads.add(thread);
		}

		for (int i = 0; i < sites.size(); i++) {
			var elem = queue.take();
			if (elem.isPresent()) {
				System.out.println("An answer has been found.\nExiting all Threads");
				for (var thread : threads) {
					thread.interrupt();
				}
				return elem;
			}
		}
		return Optional.empty();
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new Fastest("pikachu", 3_000);
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[pikachu@ebay.fr : 891]
	}
}