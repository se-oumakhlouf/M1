package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet {

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
	
	public void reSize() {
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

	public void add(Object element) {
		Objects.requireNonNull(element);
		if (this.contains(element)) return;
		if (size >= (hashTableSet.length / 2)) reSize();
		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}

	public void forEach(Consumer<Object> operator) {
		Objects.requireNonNull(operator);
		for (var elem : hashTableSet) {
			while (elem != null) {
				operator.accept(elem.element());
				elem = elem.next();
			}
		}
	}
	
	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true) {
				return true;
			}
			current = current.next();
		}
		return false;
	}

}
