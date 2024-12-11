package fr.uge.concurrence;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LinkedListLockFree<E> {

	private record Link<E>(E value, Link<E> next) {}

	private final AtomicReference<Link<E>> head = new AtomicReference<>();

	public void addFirst(E value) {
		Objects.requireNonNull(value);
		for (;;) {
			var oldHead = head.get();
			var newHead = new Link<>(value, oldHead);
			if (head.compareAndSet(oldHead, newHead)) {
				return;
			}
		}
	}

  public void forEach(Consumer<? super E> consumer) {
    Objects.requireNonNull(consumer);
    for( var current = head.get(); current!=null; current = current.next ) {
      consumer.accept(current.value);
    }
  }

//	public E pollFirst() {
//		if (head == null) {
//			return null;
//		}
//		var current = head.get();
//		var value = current.value;
//		head = current.next;
//		return value;
//	}

	public static void main(String[] args) {
		var list = new LinkedList<String>();
		list.addFirst("Noel");
		list.addFirst("papa");
		list.addFirst("petit");
		list.forEach(System.out::println);
	}

}
