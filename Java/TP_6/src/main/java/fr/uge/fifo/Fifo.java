package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public Fifo() {
		this(16);
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		if (size == elements.length) {
			resize();
		}
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

	public void resize() {
		@SuppressWarnings("unchecked")
		var tmp = (E[]) new Object[elements.length * 2];
		System.arraycopy(elements, head, tmp, 0, elements.length - head);
		System.arraycopy(elements, 0, tmp, elements.length - tail, elements.length - tail);
		head = 0;
		tail = size;
		elements = tmp;
	}

	public String toString() {
		if (size == 0) {
			return "[]";
		}
		var joiner = new StringJoiner(", ", "[", "]");
		var index = this.head;
		for (int i = 0; i < size; i++) {
			if (index >= elements.length) {
				index = 0;
			}
			joiner.add("" + elements[index]);
			index++;
		}
		return joiner.toString();
	}

	public Iterator<E> iterator() {
		return new Iterator<>() {

			private int index = head;

			@Override
			public boolean hasNext() {
				return index != size + head;
			}

			@Override
			public E next() {
				if (index >= size + head) {
					throw new NoSuchElementException();
				}
				if (index >= elements.length) {
					index = 0;
				}
				var elem = elements[index];
				index++;
				return elem;
			}
		};

	}

}
