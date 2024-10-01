package fr.uge.dom;

import java.util.Map;
import java.util.Objects;

public final class DOMDocument {

	public DOMNode createElement(String name, Map<String, Object> attributes) {
		Objects.requireNonNull(name);
		var map = Map.copyOf(attributes);
		checkAttributes(map);
		return new NodeImpl(name, map);

	}

	private static void checkAttributes(Map<String, Object> attributes) {
		attributes.forEach((name, a) -> {
			switch(a) {
			case String _: case Boolean _ : case Float _: case Double _: case Integer _: case Long _:
				break;
			default :
				throw new IllegalArgumentException();
			}
		});
	}
	
	public DOMNode getElementById() {
		return null;
	}

}
