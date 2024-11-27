package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheapestPooledCancel {

	private final String item;

	public CheapestPooledCancel(String item) {
		this.item = item;
	}

	/**
	 * @return the cheapest price for item if it is sold
	 * @throws InterruptedException
	 */
	public Optional<Answer> retrieve() throws InterruptedException {

		var sites = Request.getAllSites();
		var executorService = Executors.newFixedThreadPool(sites.size());
		var scheduler = Executors.newScheduledThreadPool(sites.size());
		var callables = new ArrayList<Callable<Answer>>();

		for (var site : sites) {
			var request = new RequestWithCancel(site, item);

			callables.add(() -> {
				var answer = request.request().get();
				return answer;
			});

			scheduler.schedule(() -> request.cancel(), 2, TimeUnit.SECONDS);
		}

		var futures = executorService.invokeAll(callables);
		var answers = new ArrayList<Answer>();

		for (var future : futures) {
			switch (future.state()) {
				case RUNNING -> throw new AssertionError("Sould not be there");
				case SUCCESS -> answers.add(future.resultNow());
				case FAILED -> System.out.println(future.exceptionNow());
				case CANCELLED -> System.out.println("Cancelled");
			}
		}
		executorService.shutdownNow();
		scheduler.shutdown();
		return answers.stream().min(Comparator.comparingInt(Answer::price));
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new CheapestPooledCancel("pikachu");
		var answer = agregator.retrieve();
		System.out.println("Cheapest (cancel) : " + answer); // Optional[pikachu@ebay.fr : 891]
	}

}
