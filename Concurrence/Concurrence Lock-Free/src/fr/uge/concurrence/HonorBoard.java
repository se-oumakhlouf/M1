package fr.uge.concurrence;

public class HonorBoard {
	private volatile FullName fullName = new FullName(null, null);

	record FullName(String firstName, String lastName) {
		@Override
		public String toString() {
			return firstName + " " + lastName;
		}
	}

	public void set(String firstName, String lastName) {
		this.fullName = new FullName(firstName, lastName);
	}

	@Override
	public String toString() {
		return fullName.toString();
	}

	public static void main(String[] args) {
		var board = new HonorBoard();
		Thread.ofPlatform().start(() -> {
			for (;;) {
				board.set("Mickey", "Mouse");
			}
		});

		Thread.ofPlatform().start(() -> {
			for (;;) {
				board.set("Donald", "Duck");
			}
		});

		Thread.ofPlatform().start(() -> {
			for (;;) {
				System.out.println(board);
			}
		});
	}
}

/* Il est toujours possible d'avoir Mickey Duck ou Donald Mouse */