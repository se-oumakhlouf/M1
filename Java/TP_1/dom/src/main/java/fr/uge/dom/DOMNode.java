package fr.uge.dom;

import java.util.Map;

public sealed interface DOMNode permits NodeImpl {
	String name();
	Map<String, Object> attributes();
	
}
