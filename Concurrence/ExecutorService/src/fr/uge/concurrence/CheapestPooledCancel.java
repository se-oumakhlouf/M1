package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

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
		
		var nbSites = Request.getAllSites().size();
		var executorService = Executors.newFixedThreadPool(nbSites);
		var callables = new ArrayList<Callable<Answer>>();
		
		for (var site : Request.getAllSites()) {
			callables.add(() -> {
				var request = new RequestWithCancel(site, item);
				var answer = request.request().get();
				request.cancel();
				return answer;
				});
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
		return answers.stream().min(Comparator.comparingInt(Answer::price));
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new CheapestPooledCancel("pikachu");
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[pikachu@ebay.fr : 891]
	}

}
