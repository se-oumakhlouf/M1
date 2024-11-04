package fr.uge.fifo;

public class Main {

	public static void main(String[] args) {
    var fifo = new Fifo<String>(10);
    fifo.offer("foo");
    fifo.poll();
    fifo.offer("1");
    fifo.offer("2");
    fifo.poll();
    fifo.offer("3");
    System.out.println(fifo);

	}

}
