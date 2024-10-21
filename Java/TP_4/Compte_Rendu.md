##### Selym OUMAKHLOUF

# TP 4 : Hacher menu

## Exercice 2

<br></br>

# Q1 : 

Quels doivent être les champs de la classe Entry correspondant à une case d'une des listes chaînées utilisées par table de hachage
Note : on va pas utiliser java.util.LinkedList, car on veut une liste simplement chaînée.
Rappeler quelle est l'intérêt de déclarer Entry comme membre de la classe HashTableSet plutôt que comme une classe à coté dans le même package que HashTableSet ?
Ne pourrait-on pas utiliser un record plutôt qu'une classe, ici ? Si oui, pourquoi ? Sinon, pourquoi ?
Écrire la classe HashTableSet dans le package fr.uge.set et ajouter Entry en tant que classe interne.
Vérifier que les tests marqués "Q1" passent.

### Rep Q1 : 

    Les champs de la classe Entry doivent être un object représentant l'élément et une Entry pour la prochaine entry de la liste chaînée

    Déclarer Entry comme une classe interne permet de limiter son accès à la classe HashTableSet pour encapsuler l'implémentation et la logique de la liste

    De plus Entry à accès aux champs privées de la classe HashTableSet

    Il est préférable d'utiliser un Record pour Entry. Cela permet d'avoir des champs immuables par défaut (et d'avoir des méthodes par défaut tel que hashCode()). Il est possible de faire un record car l'on effectue une insertion en tête de liste, dans le cas d'une insertion en fin de liste il faudrait modifier la valeur de la nextEntry (et donc ne pas avoir un champ immuable)

```java
public final class HashTableSet {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private final Entry[] hashTableSet;
}
```

<br></br>

# Q2 :

On souhaite maintenant ajouter un constructeur sans paramètre, une méthode add qui permet d'ajouter un élément non null et une méthode size qui renvoie le nombre d'éléments insérés (avec une complexité en O(1)).
Pour l'instant, on va dire que la taille du tableau est toujours 16, on fera en sorte que la table de hachage s'agrandisse toute seule plus tard.
Dans la classe HashTableSet, implanter le constructeur et les méthodes add et size.
Vérifier que les tests marqués "Q2" passent.
ATTENTION : add ne doit pas permettre d'ajouter deux fois le même élément, un ensemble/set ne permet pas les doublons.

### Rep Q2 : 

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class HashTableSet {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private final Entry[] hashTableSet;
	private int size;

	public HashTableSet() {
		this.hashTableSet = new Entry[16];
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public void add(Object element) {
		Objects.requireNonNull(element);
		if (this.contains(element)) return;

		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}
	
	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true) {
				return true;
			}
			current = current.next();
		}
		return false;
	}

}
```

<br></br>

# Q3 : 

On cherche maintenant à implanter une méthode forEach qui prend en paramètre une fonction. La méthode forEach parcourt tous les éléments insérés et pour chaque élément, appelle la fonction prise en paramètre avec l'élément courant.
Quelle doit être la signature de la functional interface prise en paramètre de la méthode forEach ?
Quel est le nom de la classe du package java.util.function qui a une méthode ayant la même signature ?
Écrire la méthode forEach.
Vérifier que les tests marqués "Q3" passent.
Note : si vous êtes balèze, forEach peut s'écrire avec un Stream.

### Rep Q3 :

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class HashTableSet {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private final Entry[] hashTableSet;
	private int size;

	public HashTableSet() {
		this.hashTableSet = new Entry[16];
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public void add(Object element) {
		Objects.requireNonNull(element);
		if (this.contains(element)) return;

		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}

	public void forEach(Consumer<Object> operator) {
		Objects.requireNonNull(operator);
		for (var elem : hashTableSet) {
			while (elem != null) {
				operator.apply(elem.element());
				elem = elem.next();
			}
		}
	}
	
	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true) {
				return true;
			}
			current = current.next();
		}
		return false;
	}

}
```

<br></br>

# Q4 : 

On souhaite maintenant ajouter une méthode contains qui renvoie si un objet pris en paramètre est un élément de l'ensemble ou pas, sous forme d'un booléen.
Expliquer pourquoi nous n'allons pas utiliser forEach pour implanter contains (Il y a deux raisons, une algorithmique et une spécifique à Java).
Écrire la méthode contains.
Vérifier que les tests marqués "Q4" passent.

### Rep Q4 :

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private Entry[] hashTableSet;
	private int size;

	public HashTableSet() {
		this.hashTableSet = new Entry[16];
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public void add(Object element) {
		Objects.requireNonNull(element);
		if (this.contains(element)) return;
		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}

	public void forEach(Consumer<Object> operator) {
		Objects.requireNonNull(operator);
		for (var elem : hashTableSet) {
			while (elem != null) {
				operator.accept(elem.element());
				elem = elem.next();
			}
		}
	}
	
	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true) {
				return true;
			}
			current = current.next();
		}
		return false;
	}

}
```

<br></br>

# Q5 :

On veut maintenant faire en sorte que la table de hachage se redimensionne toute seule. Pour cela, lors de l'ajout d'un élément, on peut avoir à agrandir la table pour garder comme invariant que la taille du tableau est au moins 2 fois plus grande que le nombre d'éléments.
Pour agrandir la table, on va créer un nouveau tableau deux fois plus grand et recopier tous les éléments dans ce nouveau tableau à la bonne place. Ensuite, il suffit de remplacer l'ancien tableau par le nouveau.
Expliquer pourquoi, en plus d'être plus lisible, en termes de performance, l'agrandissement doit se faire dans sa propre méthode.
Modifier votre implantation pour que la table s'agrandisse dynamiquement.
Vérifier que les tests marqués "Q5" passent.
Note : vous pouvez utiliser forEach pour parcourir les éléments de l'ancienne table.

### Rep Q5 :

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet {

	private record Entry(Object element, Entry next) {
		public Entry {
			Objects.requireNonNull(element);
		}
	}

	private Entry[] hashTableSet;
	private int size;

	public HashTableSet() {
		this.hashTableSet = new Entry[16];
		this.size = 0;
	}
	
	public void reSize() {
		var newTable = new Entry[hashTableSet.length * 2];
		forEach(element -> {
			var index = element.hashCode() & (newTable.length - 1);
			newTable[index] = new Entry(element, newTable[index]);
		});
		hashTableSet = newTable;
	}

	public int size() {
		return this.size;
	}

	public void add(Object element) {
		Objects.requireNonNull(element);
		if (this.contains(element)) return;
		if (size >= (hashTableSet.length / 2)) reSize();
		var index = element.hashCode() & (hashTableSet.length - 1);
		this.size++;
		var entry = new Entry(element, hashTableSet[index]);
		hashTableSet[index] = entry;
	}

	public void forEach(Consumer<Object> operator) {
		Objects.requireNonNull(operator);
		for (var elem : hashTableSet) {
			while (elem != null) {
				operator.accept(elem.element());
				elem = elem.next();
			}
		}
	}
	
	public boolean contains(Object element) {
		Objects.requireNonNull(element);

		var index = element.hashCode() & (hashTableSet.length - 1);
		var current = hashTableSet[index];

		while (current != null) {
			if (current.element().equals(element) == true) {
				return true;
			}
			current = current.next();
		}
		return false;
	}

}
```
