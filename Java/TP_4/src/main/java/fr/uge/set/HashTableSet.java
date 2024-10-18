package fr.uge.set;

import java.util.LinkedList;
import java.util.Objects;

public class HashTableSet {
	
	private final LinkedList<Entry> hashTableSet = new LinkedList<>();
	
	private record Entry(Object element, Entry next) {

		public Entry {
			Objects.requireNonNull(element);
		}
	}

}
