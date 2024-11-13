package fr.uge.seq;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Seq<E> {

	static <E> Seq<E> from(List<? extends E> list) {
		Objects.requireNonNull(list);
		List<E> tmp = List.copyOf(list);
		return new SeqImpl<>(tmp, e -> e);
	}

	int size();

	E get(int i);
	
	<T> Seq<T> map(Function<? super E,? extends T> function);
	
	Optional<E> findFirst();

}
