package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheapestPooledWithGlobalTimeout {
	
	private final String item;
	private final int timeoutMilliPerRequest;
	private final int timeoutMilliGlobal;
	private final int poolSize;

	public CheapestPooledWithGlobalTimeout(String item, int timeoutMilliPerRequest, int timeoutMilliGlocal, int poolSize) {
		if (timeoutMilliGlocal < 1 || timeoutMilliPerRequest < 1 || poolSize < 1) {
			throw new IllegalArgumentException();
		}
		this.item = item;
		this.timeoutMilliPerRequest = timeoutMilliPerRequest;
		this.timeoutMilliGlobal = timeoutMilliGlocal;
		this.poolSize = poolSize;
	}
	
	/**
	 * @return the cheapest price for item if it is sold
	 * @throws InterruptedException 
	 */
	public Optional<Answer> retrieve() throws InterruptedException {
		
		var executorService = Executors.newFixedThreadPool(poolSize);
		var callables = new ArrayList<Callable<Answer>>();
		
		for (var site : Request.getAllSites()) {
			callables.add(() -> {
				var request = new Request(site, item);
				return request.request(timeoutMilliPerRequest).get();
				});
		}
		
		var futures = executorService.invokeAll(callables, timeoutMilliGlobal, TimeUnit.MILLISECONDS);
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
		var agregator = new CheapestPooledWithGlobalTimeout("pikachu", 2_000, 5_000, 10);
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[pikachu@ebay.fr : 891]
	}

}
