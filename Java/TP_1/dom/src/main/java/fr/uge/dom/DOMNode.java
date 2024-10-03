package fr.uge.dom;

import java.util.List;
import java.util.Map;

public sealed interface DOMNode permits NodeImpl {

	String name();

	Map<String, Object> attributes();

	void appendChild(DOMNode child);

	List<DOMNode> children();

}
