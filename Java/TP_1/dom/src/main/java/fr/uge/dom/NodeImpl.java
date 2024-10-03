package fr.uge.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class NodeImpl implements DOMNode {

	private final String name;
	private final Map<String, Object> attributes;
	private final List<DOMNode> childList = new ArrayList<>();
	private final DOMDocument document;
	private DOMNode parent;
	private String cache;

	NodeImpl(String name, Map<String, Object> attributes, DOMDocument document) {
		this.name = name;
		this.attributes = attributes;
		this.document = document;
		this.parent = null;
		this.cache = null;
	}

	public String name() {
		return this.name;
	}

	public Map<String, Object> attributes() {
		return this.attributes;
	}

	public void appendChild(DOMNode child) {
		Objects.requireNonNull(child);
		if (child instanceof NodeImpl node && this.document.equals(node.document)) {
			if (node.parent != null) {
				((NodeImpl) node.parent).removeChild(child);
			}
			node.parent = this;
			childList.add(node);
			this.cache = null;
		} else {
			throw new IllegalStateException("Child node must be from the same document as the Parent node");
		}

	}

	public List<DOMNode> children() {
		return Collections.unmodifiableList(this.childList);
	}

	private void removeChild(DOMNode child) {
		Objects.requireNonNull(child);
		childList.remove(child);
	}

	@Override
	public String toString() {
		if (this.cache != null) return cache;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("<").append(name);
		attributes.forEach((key, value) -> {
			builder.append(" ").append(key).append("=\"").append(value.toString()).append("\"");
		});
		builder.append(">");
		
		childList.forEach(child -> builder.append(child.toString()));
		builder.append("</").append(name).append(">");
		this.cache = builder.toString();
		return this.cache;
	}

}
