package fr.uge.interruption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CountAndInterrupt {

	public static void main(String[] args) throws InterruptedException, NumberFormatException {

		for (int i = 0; i < 5; i++) {
			var thread = Thread.ofPlatform().start(() -> {
				int count = 0;

				for (;;) {
					count++;
					try {
						Thread.sleep(1_000);
					} catch (InterruptedException e) {
						return;
					}
					System.out.println(Thread.currentThread().threadId() + " counter = " + count);
				}
				
			});
		}
		
	  System.out.println("enter a thread id:");
	  try (var input = new InputStreamReader(System.in);
	         var reader = new BufferedReader(input)) {
	      String line;
	      while ((line = reader.readLine()) != null) {
	        var threadId = Integer.parseInt(line);
//	        System.out.println("Thread id : " + threadId);
//	        System.out.println(Thread.currentThread().getName()); // thread main
	        if (Thread.currentThread().threadId() == threadId) {
	        	System.out.println("equal");
	        	Thread.currentThread().interrupt();
	        }
	      }
	    } catch (IOException e) {
	    	System.out.println("here");
				return;
			}

	}
}
