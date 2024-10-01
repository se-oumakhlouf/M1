package fr.uge.dom;

import java.util.Map;
import java.util.Objects;

public class DOMDocument {

	public NodeImpl createElement(String name, Map<String, Object> attributes) {
		Objects.requireNonNull(name);
		var map = Map.copyOf(attributes);
		checkAttributes(map);
		return new NodeImpl(name, map);

	}

	private static void checkAttributes(Map<String, Object> attributes) {
		attributes.forEach((name, a) -> {
			switch(a) {
			case String _: case boolean _ : case float _:
				break;
			default :
				throw new IllegalArgumentException();
			}
		});
	}

}
