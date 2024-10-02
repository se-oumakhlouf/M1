package fr.uge.dom;

import java.util.Map;

public class Main {

	public static void main(String[] args) {
		 DOMDocument document = new DOMDocument();
     DOMNode node = document.createElement("div", Map.of("color", "red"));
     System.out.println(node.name());  // div
     System.out.println(node.attributes());  // {color=red}
   
     var document2 = new DOMDocument();
     var node1 = document2.createElement("div", Map.of("id", "un id"));
     var node2 = document2.getElementById("foo42");
     System.out.println(node1 == node2);  // true
     // document.getElementById(null); NullPointerException
	}

}
