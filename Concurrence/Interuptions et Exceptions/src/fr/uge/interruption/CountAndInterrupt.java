package fr.uge.interruption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CountAndInterrupt {

	public static void main(String[] args) throws InterruptedException, NumberFormatException {

		Map<Long, Thread> threads = new HashMap<>();

		for (int i = 0; i < 4; i++) {
			Thread thread = Thread.ofPlatform().start(() -> {
				long id = Thread.currentThread().threadId();
				int count = 0;
				for (; !Thread.currentThread().isInterrupted();) {
					count++;
					try {
						Thread.sleep(1_000);
					} catch (InterruptedException e) {
						System.out.println("Thread " + id + " interrupted");
						return;
					}
					System.out.println("Thread " + id + " counter = " + count);
				}
			});
			threads.put(thread.threadId(), thread);
		}

		System.out.println("enter a thread id:");
		try (var input = new InputStreamReader(System.in); var reader = new BufferedReader(input)) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					long threadId = Long.parseLong(line);
					Thread thread = threads.get(threadId);
					if (thread != null) {
						thread.interrupt();
						threads.remove(threadId);
					} else {
						System.out.println("No thread with ID : " + threadId);
					}
				} catch (NumberFormatException e) {
					System.out.println("Enter a valid thread ID");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Exiting -> Interrupting all threads");
		for (var thread : threads.values()) {
			thread.interrupt();
		}
		
	}
}
