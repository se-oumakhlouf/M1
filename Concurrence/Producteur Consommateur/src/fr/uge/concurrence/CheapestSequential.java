package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class CheapestSequential {

	private final String item;
	private final int timeoutMilliPerRequest;

	public CheapestSequential(String item, int timeoutMilliPerRequest) {
		this.item = item;
		this.timeoutMilliPerRequest = timeoutMilliPerRequest;
	}

	/**
	 * @return the cheapest price for item if it is sold
	 */
	public Optional<Answer> retrieve() throws InterruptedException {
		ArrayList<Optional<Answer>> answers = new ArrayList<>();
		for (var site : Request.getAllSites()) {
			var request = new Request(site, item);
			Optional<Answer> answer = request.request(timeoutMilliPerRequest);
			if (answer.isPresent()) {
				answers.add(answer);
			}
		}
		return answers.stream().map(Optional::get).min(Comparator.comparingInt(Answer::price));
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new CheapestSequential("pikachu", 2_000);
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[pikachu@ebay.fr : 891]
	}
}