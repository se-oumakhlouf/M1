package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet<E> {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private Entry[] hashTableSet;
	private int size;

	public HashTableSet() {
		this.hashTableSet = new Entry[16];
		this.size = 0;
	}

	private void reSize() {
		var newTable = new Entry[hashTableSet.length * 2];
		forEach(element -> {
			var index = element.hashCode() & (newTable.length - 1);
			newTable[index] = new Entry(element, newTable[index]);
		});
		hashTableSet = newTable;
	}

	public int size() {
		return this.size;
	}

	public void add(E element) {
		Objects.requireNonNull(element);
		if (!(element instanceof E))
			throw new ClassCastException("Invalid Type for : " + element.getClass());
		if (this.contains(element))
			return;
		if (size >= (hashTableSet.length / 2))
			reSize();
		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}

	@SuppressWarnings("unchecked")
	public void forEach(Consumer<E> operator) {
		Objects.requireNonNull(operator);
		for (var elem : hashTableSet) {
			while (elem != null) {
				operator.accept((E) elem.element());
				elem = elem.next();
			}
		}
	}

	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true)
				return true;
			current = current.next();
		}
		return false;
	}

}
