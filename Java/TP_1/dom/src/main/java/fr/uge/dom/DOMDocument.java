package fr.uge.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DOMDocument {

	private final Map<String, DOMNode> idMap = new HashMap<String, DOMNode>(); // <id, node>

	public DOMNode createElement(String name, Map<String, Object> attributes) {
		Objects.requireNonNull(name);
		var map = Map.copyOf(attributes);
		checkAttributes(map);
		DOMNode node = new NodeImpl(name, map);

		if (attributes.containsKey("id")) {
			var id = attributes.get("id");
			if (id instanceof String idString && !idString.isEmpty()) {
					idMap.putIfAbsent(idString, node);
			} else {
				throw new IllegalArgumentException("ID must be a non-empty string");
			}
		}
		return node;
	}

	private static void checkAttributes(Map<String, Object> attributes) {
		attributes.forEach((name, attribute) -> {
			switch (attribute) {
			case String _:
			case Boolean _:
			case Float _:
			case Double _:
			case Integer _:
			case Long _:
				break;
			default:
				throw new IllegalArgumentException();
			}
		});
	}

	public DOMNode getElementById(String id) {
		Objects.requireNonNull(id);
		return idMap.get(id);
	}

}
