###############
### Exercice 2
###############

######################
	# Question 1 :
		Dans un premier temps, créer l'interface DOMNode avec une méthode name qui renvoie le nom du nœud de l'arbre DOM et une méthode attributes qui renvoie les attributs ainsi que la classe correspondante implantant l'interface.
		La valeur d'un attribut ne peut être qu'une chaîne de caractères, un booléen, un entier, un entier long, un flottant ou un flottant long.
		Créer ensuite la classe DOMDocument, et sa méthode createElement(name, attributes) qui renvoie une nouvelle instance de DOMNode telles que le code suivant fonctionne
		      
		      DOMDocument document = new DOMDocument();
		      DOMNode node = document.createElement("div", Map.of("color", "red"));
		      System.out.println(node.name());  // div
		      System.out.println(node.attributes());  // {color=red}

		Vérifier que les tests JUnit marqués "Q1" passent.
	
	
-- 

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

}

package fr.uge.dom;

import java.util.Map;

final class NodeImpl implements DOMNode {

	private final String name;
	private final Map<String, Object> attributes;

	NodeImpl(String name, Map<String, Object> attributes) {
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


package fr.uge.dom;

import java.util.Map;

public sealed interface DOMNode permits NodeImpl {

	String name();

	Map<String, Object> attributes();

}

--
######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 2 :
	
		Êtes-vous sûr d'avoir d'avoir bien respecté l'ensemble des consignes ci-dessus et écrit vos classes correctement ?
		Vérifier que les tests JUnit marqués "Q2" passent.
		Note : lorsqu'un test ne passe pas (pas vert), il faut regarder la stack trace (la fenêtre en bas à gauche quand on clique sur le test) puis rechercher du haut vers le bas la méthode qui est dans la classe de test (dans notre cas, la classe DOMNodeTest). Cliquer sur cette méthode vous emportera à la ligne où il y a un problème.
		Note 2 : pour corriger le problème, lisez le code du test pour essayer de comprendre pourquoi le test ne passe pas. Vous pouvez vous aider de la javadoc (si on laisse le curseur sur une méthode la javadoc apparaît) pour comprendre les méthodes que vous ne connaissez pas. Une fois que vous avez compris votre erreur, modifiez votre code pour que le test pas (bien sûr, on ne modifie jamais le code du test).
		Note 3 : Dans le cas d'un assertAll, cette méthode peut reporter plusieurs erreurs, dans ce cas, la stack trace est composée de l'erreur principale qui dit juste qu'il y a plusieurs erreurs et qu'il faut regarder les stack traces des exceptions suppressed (en dessous de l'exception principale).
			
--

Même code que la question 1

--			
######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------


######################
	Question 3 :
	
		On souhaite ajouter une méthode getElementById à la classe DOMDocument qui renvoie un nœud par son id.
		En HTML, un id doit être une chaine de caractère (non vide) et si pour un même document, il y a plusieurs nœuds avec le même id seul le premier nœud est enregistré avec cet id.
		var document = new DOMDocument();
		var node = document.createElement("div", Map.of("id", "foo42"));
		var node2 = document.getElementById("foo42");
		System.out.println(node == node2);  // true
		      
		
		Quelle structure de données doit-on utiliser pour permettre de trouver un nœud par son id ?
		Ajouter la méthode getElementById dans la classe DOMDocument telle que le code ci-dessus fonctionne.
		Vérifier que les tests JUnit marqués "Q3" passent.
		
--

Pour trouver un noeud par son id il faut utiliser une map

DOMDocument est le seul fichier modifié dans cette question

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

--		
		
######################	
		
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
######################	
	Question 4 :
	
		On souhaite pouvoir ajouter des fils à un nœud (DOMNode) existant en utilisant la méthode appendChild(child) et accéder à ses enfants en utilisant la méthode children().
		Il ne doit être possible d'ajouter un nœud que si le nœud parent et le nœud enfant sont issus du même document.
		
			var document = new DOMDocument();
			var parent = document.createElement("foo", Map.of());
			var child = document.createElement("bar", Map.of());
			parent.appendChild(child);
			System.out.println(parent.children().getFirst() == child);  // true
		       
		Expliquer pourquoi on souhaite que la liste renvoyée par children() soit non modifiable ?
		Sachant cela, implanter les méthodes children() et appendChild(child).
		Vérifier que les tests JUnit marqués "Q4" passent.
		Note : pour l'implantation de la méthode children, on pourra utiliser une vue pour éviter de dupliquer trop d'objets.
		
--

On souhaite que la liste renvoyé par children() soit non-modifiable pour garantir l'intégrité des données
Cela limite les erreures et les incohérences

du code ici

--

######################	
		
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
######################	
	Question 5 :
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

