package fr.uge.dedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this.dedupMap = new HashMap<>();
		this.dedupElements = new ArrayList<>();
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0 || index >= this.size())
			throw new IndexOutOfBoundsException("Error : index < 0");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		T sameElem = dedupMap.get(element);
		if (sameElem == null) {
			dedupElements.add(element);
			dedupMap.put(element, element);
		} else {
			dedupElements.add(sameElem);
		}
	}

	public boolean contains(Object element) {
		Objects.requireNonNull(element);
		return dedupMap.containsKey(element);
	}

	public void addAll(DedupVec<T> dedupVec) {
		Objects.requireNonNull(dedupVec);
		dedupVec.dedupElements.forEach(this::add);
	}

	public void fromSet(T set) {

	}

}
