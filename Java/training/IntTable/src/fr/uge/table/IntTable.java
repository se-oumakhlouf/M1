package fr.uge.table;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class IntTable {
	
	private final MapImpl storage;
	
	public IntTable() {
		storage = new MapImpl();
	}
	
	public int size() {
		return storage.size();
	}
	
	public void set(String elem, int number) {
		storage.set(elem, number);
	}
	
	public int get(String elem, int error) {
		return storage.get(elem, error);
	}
	
	public IntTable apply(UnaryOperator<Integer> function) {
		return storage.apply(function);
	}

	private static final class MapImpl {
		
		private final HashMap<String, Integer> map = new HashMap<>();

		public int size() {
			return map.size();
		}

		public IntTable apply(UnaryOperator<Integer> function){
			Objects.requireNonNull(function);
			var newStorage = new IntTable();
			map.forEach((key, value) -> newStorage.set(key, function.apply(value)));
			return newStorage;
		}

		public void set(String elem, int number) {
			Objects.requireNonNull(elem);
			map.put(elem, number);
		}
		
		public int get(String elem, int error) {
			Objects.requireNonNull(elem);
			return map.getOrDefault(elem, error);
		}
	}

}
