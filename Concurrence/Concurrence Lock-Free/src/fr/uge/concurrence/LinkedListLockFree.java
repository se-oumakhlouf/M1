package fr.uge.concurrence;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LinkedListLockFree<E> {

	private AtomicReference<Link<E>> head; // mettre final et donc modifier le reste

	private record Link<E>(E value, AtomicReference<Link<E>> next) {
		private Link {
			Objects.requireNonNull(value);
		}
	}

	public void addFirst(E value) {
		Objects.requireNonNull(value);
		head = new AtomicReference<>(new Link<>(value, head)); // compareAndSet
	}

	public void forEach(Consumer<? super E> consumer) {
		Objects.requireNonNull(consumer);
		for (var current = pollFirst(); current != null; current = pollFirst()) {
			consumer.accept(current);
		}
	}
	
  public E pollFirst() {
    if (head == null) {
        return null;
    }
    var current = head.get();
    var value = current.value;
    head = current.next;
    return value;
}  

	public static void main(String[] args) {
		var list = new LinkedList<String>();
		list.addFirst("Noel");
		list.addFirst("papa");
		list.addFirst("petit");
		list.forEach(System.out::println);
	}

}
