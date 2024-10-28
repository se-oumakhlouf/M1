package fr.uge.dedup;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this(new ArrayList<T>(), new HashMap<>());
	}

	private DedupVec(List<T> array, Map<T, T> map) {
		this.dedupElements = (ArrayList<T>) array;
		this.dedupMap = (HashMap<T, T>) map;
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0 || index >= this.size())
			throw new IndexOutOfBoundsException("Error : index < 0 || index >= size");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		var same = dedupMap.computeIfAbsent(element, elem -> elem);
		dedupElements.add(same);
	}

	public boolean contains(Object element) {
		Objects.requireNonNull(element);
		return dedupMap.containsKey(element);
	}

	public void addAll(DedupVec<T> dedupVec) {
		Objects.requireNonNull(dedupVec);
		dedupVec.dedupElements.forEach(this::add);
	}

	static <E> Map<E, E> newMapFromSet(Set<E> set) {
		Objects.requireNonNull(set);
		return new AbstractMap<E, E>() {

			@Override
			public Set<Entry<E, E>> entrySet() {
				return new AbstractSet<Map.Entry<E, E>>() {

					@Override
					public int size() {
						return set.size();
					}

					@Override
					public Iterator<Entry<E, E>> iterator() {
						return new Iterator<>() {

							Iterator<E> iterator = set.iterator();

							@Override
							public boolean hasNext() {
								return iterator.hasNext();
							}

							@Override
							public Entry<E, E> next() {
								if (!iterator.hasNext())
									throw new NoSuchElementException();
								var elem = iterator.next();
								return Map.entry(elem, elem);
							}
						};
					}
				};
			}

		};
	}

	public static <E> DedupVec<E> fromSet(Set<E> set) {
		Objects.requireNonNull(set);
		var map = new HashMap<E, E>(newMapFromSet(set));
		var array = new ArrayList<E>(set);
		return new DedupVec(array, map);

	}

}
