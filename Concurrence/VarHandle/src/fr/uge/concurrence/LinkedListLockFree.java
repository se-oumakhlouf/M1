package fr.uge.concurrence;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LinkedListLockFree<E> {

	private static final class Entry<E> {
		private final E element;
		private volatile Entry<E> next;

		Entry(E element) {
			this.element = element;
		}
	}

	private static final VarHandle NEXT_HANDLE;

	static {
		var lookup = MethodHandles.lookup();
		try {
			NEXT_HANDLE = lookup.findVarHandle(LinkedListLockFree.Entry.class, "next", Entry.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new AssertionError();
		}
	}
	
	/*
	 * Faire la version 2 au lieu de tail
	 * On reprend sur le maillon suivant
	 */

	private final Entry<E> head = new Entry<>(null); // fake first entry

	public void addLast(E element) {
		var entry = new Entry<>(element);
		for (;;) {
			var current = head;
			for (;;) {
				var next = current.next; // lecture volatile
				if (next == null) {
					break;
				}
				current = next;
			}
			if (NEXT_HANDLE.compareAndSet(current, null, entry)) {
				return;
			}
			current = head;
		}
	}

	/*
	 * La méthode est déjà thread safe
	 */
	public int size() {
		var count = 0;
		for (var entry = head.next; entry != null; entry = entry.next) {
			count++;
		}
		return count;
	}

	private static Runnable createRunnable(LinkedListLockFree<String> list, int id) {
		return () -> {
			for (var i = 0; i < 10_000; i++) {
				list.addLast(id + " " + i);
			}
		};
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		var threadCount = 5;
		var list = new LinkedListLockFree<String>();
		var tasks = IntStream.range(0, threadCount).mapToObj(id -> createRunnable(list, id)).map(Executors::callable)
				.toList();
		var executor = Executors.newFixedThreadPool(threadCount);
		var futures = executor.invokeAll(tasks);
		executor.shutdown();
		for (var future : futures) {
			future.get();
		}
		System.out.println(list.size());
	}
}