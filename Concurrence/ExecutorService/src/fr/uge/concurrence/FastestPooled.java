package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FastestPooled {
	
	private final String item;
	private final int timeoutMilliPerRequest;

	public FastestPooled(String item, int timeoutMilliPerRequest) {
		this.item = item;
		this.timeoutMilliPerRequest = timeoutMilliPerRequest;

	}
	
	/**
	 * @return the cheapest price for item if it is sold
	 * @throws InterruptedException 
	 */
	public Optional<Answer> retrieve() throws InterruptedException {
		
		var executorService = Executors.newFixedThreadPool(Request.getAllSites().size());
		var callables = new ArrayList<Callable<Answer>>();
		
		for (var site : Request.getAllSites()) {
			callables.add(() -> {
				var request = new Request(site, item);
				return request.request(timeoutMilliPerRequest).get();
				});
		}
		
			try {
				var first = Optional.of(executorService.invokeAny(callables));
				executorService.shutdown();
				return first;
			} catch (ExecutionException e) {
				return Optional.empty();
			}
	}

	public static void main(String[] args) throws InterruptedException {
		var agregator = new FastestPooled("pikachu", 2_000);
		var answer = agregator.retrieve();
		System.out.println(answer); // Optional[pikachu@tombeducamion.fr : 919]
	}

}
