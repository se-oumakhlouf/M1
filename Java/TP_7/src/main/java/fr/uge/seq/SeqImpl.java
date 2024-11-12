package fr.uge.seq;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

class SeqImpl<E, T> implements Seq<T> {

	private final List<E> list;
	private final Function<? super E,? extends T> mapping;

	SeqImpl(List<E> list, Function<? super E, ? extends T> function) {
		this.list = list;
		this.mapping = function;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public T get(int i) {
		Objects.checkIndex(i, list.size());
		return mapping.apply(list.get(i));
	}
	
	@Override
	public String toString() {
		var joiner = new StringJoiner(", ", "<", ">");
		for (var elem : list) {
			joiner.add(mapping.apply(elem).toString());
		}
		return joiner.toString();
		
	}

	@Override
	public <A> Seq<A> map(Function<? super T, ? extends A> function) {
		Objects.requireNonNull(function);
		Function<?super E, ? extends A> cadeau = mapping.andThen(function);
		return new SeqImpl<>(list, cadeau);
	}






}
