package fr.uge.interruption;

public class MainTest {

	public static void main(String[] args) throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      System.out.println("1 : " + tpir.propose(110));
    });
    Thread.sleep(1000);
    System.out.println("2 : " + tpir.propose(107));
    thread.join();

	}

}
