package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Slice<E> {

	int size();

	E get(int index);

	Slice<E> subSlice(int from, int to);
	
	void replaceAll(UnaryOperator<E> operator);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}

	default Slice<E> reversed() {
		return new Slice<E>() {

			@Override
			public int size() {
				return Slice.this.size();
			}

			@Override
			public E get(int index) {
				Objects.checkIndex(index, size());
				return Slice.this.get(size() - 1 - index);
			}

			@Override
			public Slice<E> subSlice(int from, int to) {
				throw new UnsupportedOperationException();
			}

			public Slice<E> reversed() {
				return Slice.this;
			}

			@Override
			public String toString() {
				return "["
						+ IntStream.range(0, size()).mapToObj(this::get).map(Object::toString).collect(Collectors.joining(", "))
						+ "]";
			}

			@Override
			public void replaceAll(UnaryOperator<E> operator) {
				Objects.requireNonNull(operator);
				for (int i = 0; i < size(); i++) {
					int originalIndex = size() - 1 - i;
					E originalElem = Slice.this.get(originalIndex);
					E newValue = operator.apply(originalElem);
					
					((SliceImpl<E>) Slice.this).set(originalIndex, newValue);
				}
				
			}


		};
	}

	final class SliceImpl<E> implements Slice<E> {
		private final E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}
		
		private void set(int index, E value) {
			Objects.checkIndex(index, size());
			elements[from + index] = value;
		}

		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}

		@Override
		public Slice<E> subSlice(int from, int to) {
			Objects.checkFromToIndex(from, to, size());
			return Slice.of(elements, this.from + from, this.from + to);
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			Objects.requireNonNull(operator);
			for (int i = 0; i < size(); i++) {
				E newValue = operator.apply(get(i));
				set(i, newValue);
			}
		}
	}

}
