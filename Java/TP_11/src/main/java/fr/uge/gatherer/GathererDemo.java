package fr.uge.gatherer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

public class GathererDemo {

	public final record Indexed<T>(T element, int index) {
		public Indexed {
			if (index < 0) {
				throw new IllegalArgumentException();
			}
		}
	}

	public static Gatherer<Integer, ?, Integer> filterIntegers(IntPredicate predicate) {
		Objects.requireNonNull(predicate);
		return Gatherer.ofSequential((_, element, downstream) -> {
			if (predicate.test(element)) {
				downstream.push(element);
			}
			return true;
		});
	}

	public static Gatherer<Integer, ?, Integer> takeWhileIntegers(IntPredicate predicate) {
		Objects.requireNonNull(predicate);
		return Gatherer.ofSequential((_, element, downstream) -> {
			if (predicate.test(element)) {
				return downstream.push(element);
			}
			return false;
		});
	}

	public static <T> Gatherer<T, ?, T> takeWhile(Predicate<T> predicate) {
		Objects.requireNonNull(predicate);
		return Gatherer.ofSequential((_, element, downstream) -> {
			if (predicate.test(element)) {
				return downstream.push(element);
			}
			return false;
		});
	}

	public static <T> Gatherer<T, ?, Indexed<T>> indexed() {
		class State {
			int index = 0;
		}
		return Gatherer.ofSequential(State::new, (state, element, downstream) -> {
			return downstream.push(new Indexed<T>(element, state.index++));
		});
	}

}
