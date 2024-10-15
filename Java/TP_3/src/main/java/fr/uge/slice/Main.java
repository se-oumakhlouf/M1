package fr.uge.slice;

public class Main {
	public static void main(String[] args) {
		var array = new String[]{ "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 4);
    slice.replaceAll(x -> "*" + x + "*");
    System.out.println(slice.get(0));  // *bar*
    System.out.println(slice.get(1));  // *baz*
    System.out.println(slice.get(2));  // *whizz*
    System.out.println(slice);
    
    slice.replaceAll(null);

	}
}