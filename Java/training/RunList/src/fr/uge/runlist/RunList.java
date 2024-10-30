package fr.uge.runlist;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class RunList<E> {

	public record Run(Object element, int count) {
	}

	public int size() {
		return 0;
	}

	public void addRun(E elem, int quantity) {
	}

	public E get(int index) {
		return null;
	}

	public void forEach(Consumer<E> function) {
	}
	
//	public Object asList() {
//		return null;
//	}

	public static <F> RunList<F> newRunLengthList() {
		return new RunList<F>() {
			private int size = 0;
			private final ArrayList<Run> list = new ArrayList<>();

			@Override
			public int size() {
				return size;
			}

			@Override
			public void addRun(F elem, int quantity) {
				Objects.requireNonNull(elem);
				if (quantity <= 0)
					throw new IllegalArgumentException("Quantity <= 0");
				list.add(new Run(elem, quantity));
				size += quantity;
			}

			@Override
			@SuppressWarnings("unchecked")
			public F get(int index) {
				Objects.checkIndex(index, size);
				int i = index;
				for (var run : list) {
					if (i < run.count) {
						return (F) run.element;
					}
					i -= run.count;
				}
				return null;
			}

//			public void forEach(Consumer<F> function) {
//				Objects.requireNonNull(function);
//				for (var elem : list) {
//					for (int i = 0; i < elem.count; i++)
//						function.accept((F) elem.element);
//				}
//			}

			@SuppressWarnings("unchecked")
			public void forEach(Consumer<F> function) {
				Objects.requireNonNull(function);
				list.forEach(elem -> IntStream.range(0, elem.count).forEach(notUsed -> function.accept((F) elem.element)));
			}

		};
	}

	public static <F> RunList<F> newBinarySearchList() {
		return new RunList<F>() {

			private int[] offset = new int[4];
			private int index = 1;
			private int size = 0;
			@SuppressWarnings("unchecked")
			private F[] elements = (F[]) new Object[4];

			public int size() {
				return size;
			}

			public void addRun(F elem, int quantity) {
				Objects.requireNonNull(elem);
				if (quantity <= 0)
					throw new IllegalArgumentException("Quantity <= 0");
				if (index == offset.length) {
					offset = Arrays.copyOf(offset, offset.length * 2);
					elements = Arrays.copyOf(elements, elements.length * 2);
				}
				offset[index] = quantity + offset[index - 1];
				elements[index - 1] = elem;
				size += quantity;
				index++;
			}

			public F get(int index) {
				Objects.checkIndex(index, size());
				var result = Arrays.binarySearch(offset, 0, this.index, index);
				if (result < 0)
					result = -result - 2;
				return elements[result];
			}

		};
		
		
	}




}
