package fr.uge.dom;

import java.util.Map;

final class NodeImpl implements DOMNode {
	
	private final String name;
	private final Map<String, Object> attributes;
	
	NodeImpl (String name, Map<String, Object> attributes) {
		this.name = name;
		this.attributes = attributes;
	}

	public String name() {
		return this.name;
	}

	public Map<String, Object> attributes() {
		return this.attributes;
	}

}
