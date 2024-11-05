package fr.uge.test;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		var thread1 = Thread.ofPlatform().start(() -> {
			System.out.println("Thread 1");
		});
		
		var thread2 = Thread.ofPlatform().start(() -> {
			System.out.println("Thread 2");
		});
		
		var thread3 = Thread.ofPlatform().start(() -> {
			System.out.println("Thread 3");
		});
		
		var thread4 = Thread.ofPlatform().start(() -> {
			System.out.println("Thread 4");
		});

		
		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();

	    System.out.println("Thread main");
	}
}
