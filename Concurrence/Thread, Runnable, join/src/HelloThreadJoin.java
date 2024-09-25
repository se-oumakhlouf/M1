import java.util.ArrayList;

public class HelloThreadJoin {

	public static void main(String[] args) throws InterruptedException {
		
		var threadList = new ArrayList<Thread>();
		
		for (var i = 0; i < 4; i++) {
			var count = i;
			Runnable runnable = () -> {
				for (int j = 1; j <= 5000; j++) {
					System.out.println("hello " + count + " " + j);
				}
			};
			
			var thread = Thread.ofPlatform().start(runnable);
			threadList.add(thread);
		}
		for (int i = 0; i < threadList.size(); i++) {
			threadList.get(i).join();
		}
		System.out.println("Le thread a fini son Runnable");
	}

}
