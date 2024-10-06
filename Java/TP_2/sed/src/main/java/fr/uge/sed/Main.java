package fr.uge.sed;

public class Main {

	public static void main(String[] args) {
		String test = "*foo**\n";
		test = test.replace("*", "*".repeat(2));
		System.out.println(test);
	}

}
