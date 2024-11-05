package fr.uge.concurrence.reentrantlock;

public class Counter {

	private int count;
	private final Sync<Integer> sync = new Sync<>();

	public int count() throws InterruptedException {
		return sync.safe(() -> count++);

	}

	public boolean isSomeOneCounting() throws InterruptedException {
		return sync.inSafe();
	}

	public static void main(String args[]) {
		var counter = new Counter();

		for (int i = 0; i < 2; i++) {
			Thread.ofPlatform().start(() -> {
				for (;;) {
					try {
						Thread.sleep(2000);
						var tmp = counter.count();
						System.out.println(Thread.currentThread().getName() + " : " + tmp);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

		Thread.ofPlatform().start(() -> {
			for (;;) {
				try {
					Thread.sleep(2000);
					System.out.println("Someone is counting : " + counter.isSomeOneCounting());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
