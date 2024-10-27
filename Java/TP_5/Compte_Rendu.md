### *Selym OUMAKHLOUF*

# Deduplication


DedupVec est une liste dynamique qui, lorsque l'on stocke deux objets égaux au sens de equals, stocke une référence sur la première instance à la place de la seconde.
Par exemple, on déclare deux points point1 et point2 ayant les mêmes valeurs

    record Point(int x, int y) {}
    ...
    Point point1 = new Point(2, 3);
    Point point2 = new Point(2, 3);
     
Si l'on ajoute successivement les deux points à un DedupVec,

    DedupVec<Point> dedupVec = new DedupVec<Point>();
    dedupVec.add(point1);
    dedupVec.add(point2);
     
lors de l'ajout de point2, l'implantation de DedupVec se rend compte qu'il existe déjà dans DedupVec une instance de Point égale à point2 et substitue point2 par point1. Les deux points stockés dans DedupVec ont alors la même adresse.
On peut tester cela en utilisant l'opérateur == sur les références.

    point1.equals(dedupVec.get(0))  // true
    point1 == dedupVec.get(0)       // true
    point2.equals(dedupVec.get(1))  // true
    point2 == dedupVec.get(1)       // false
    point1 == dedupVec.get(1)       // true
    

Le fait de remplacer une instance par une autre ayant les mêmes valeurs est appelé dé-duplication. En Java, certains garbage collectors, entre autres G1, le garbage collector par défaut, font cela automatiquement pour le contenu des Strings.

En terme d'implantation, la classe DedupVec contient 2 champs. Le premier est une instance de l'interface java.util.List qui stocke les éléments et le second est une instance de l'interface java.util.Map qui associe à un élément que l'on a déjà vu, le même élément, comme cela, si l'on ajoute un élément égal (au sens de equals) à l'élément déjà vu, on peut le substituer.

Attention : si votre implantation n'a pas le bon nombre de champs (2) ou qu'ils n'ont pas le bon type (sous-type de List et sous-type de Map), votre implantation sera considérée comme hors sujet !
Le test unitaire suddenDeath de la question 2 vérifie que votre implantation n'est pas hors sujet.

Dans l'exemple ci-dessous, après avoir ajouté point1, la liste contient point1 et la map associe point1 à point1. Lorsque l'on ajoute point2, comme il est egal à point1, la liste contient maintenant deux fois point1 et la map associe toujours point1 à point1.
La classe DedupVec représente une liste d'éléments non-null qui possède les méthodes suivantes :

    size qui renvoie le nombre d'éléments,
    get(index) renvoie l'élément à l'index index,
    add(element) qui ajoute un élément (ou un élément égal déjà présent).
    contains(element) qui indique si element est un élément de la liste
    addAll(dedupVec) qui ajoute tous les éléments du dedupVec pris en paramètre dans le DedupVec courant.
    fromSet(set) qui prend un ensemble en paramètre et créé le DedupVec correspondant.

Des tests unitaires correspondant à l'implantation sont ici : DedupVecTest.java


<br></br>

# Question 1 :

On va dans un premier temps écrire une version simple de la classe DedupVec, avec un constructeur sans paramètre, les méthodes size et get(index) ainsi que la méthode add(element) qui pour l'instant n'effectue pas la déduplication.
Quelle implantation de l'interface List allez-vous choisir pour stocker les éléments ?
Implanter la classe DedupVec.
Vérifier que les tests marqués "Q1" passent.

```java
package fr.uge.dedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this.dedupMap = new HashMap<>();
		this.dedupElements = new ArrayList<>();
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("Error : index < 0");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		dedupElements.add(element);
	}

	public boolean contains(T element) {
		Objects.requireNonNull(element);
		return false;
	}

	public void addAll(T dedupVec) {
		Objects.requireNonNull(dedupVec);
	}

	public void fromSet(T set) {

	}

}
```

<br></br>

# Question 2 :

On souhaite maintenant que lorsque l'on ajoute un élément, l'implantation effectue la déduplication comme décrit ci-dessus.
Quelle implantation de l'interface Map allez-vous choisir ?
Modifier l'implantation de la classe DedupVec pour que la déduplication des instances soit faite.


```java
package fr.uge.dedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this.dedupMap = new HashMap<>();
		this.dedupElements = new ArrayList<>();
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Error : index < 0");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		T elem = sameElementorElement(element);
		dedupElements.add(elem);
		dedupMap.putIfAbsent(element, elem);
	}

	public boolean contains(T element) {
		Objects.requireNonNull(element);
		return false;
	}

	public void addAll(T dedupVec) {
		Objects.requireNonNull(dedupVec);
	}

	public void fromSet(T set) {

	}

	private T sameElementorElement(T element) {
		if (!dedupMap.containsKey(element)) return element;
		for (var elem : dedupElements) {
			if (elem.equals(element))
				return elem;
		}
		return element;
	}

}

```

<br></br>

# Question 3 :

On souhaite ajouter une méthode contains(element) qui teste si une valeur est un des éléments de DedupVec.
Sachant que l'on veut une complexité pire cas en temps constant, comment doit-on faire ?
Implanter la méthode contains(element).
Vérifier que les tests marqués "Q3" passent.

    La recherche dans une HashMap est en O(1) (en moyenne)
    Pour obtenir une complexité O(1) au pire cas il faut ....

```java
package fr.uge.dedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this.dedupMap = new HashMap<>();
		this.dedupElements = new ArrayList<>();
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0 || index >= this.size())
			throw new IndexOutOfBoundsException("Error : index < 0");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		T sameElem = dedupMap.get(element);
		if(sameElem == null) {
			dedupElements.add(element);
			dedupMap.put(element, element);
		} else {
			dedupElements.add(sameElem);
		}
	}

	public boolean contains(Object element) {
		Objects.requireNonNull(element);
		return dedupMap.containsKey(element);
	}

	public void addAll(T dedupVec) {
		Objects.requireNonNull(dedupVec);
	}

	public void fromSet(T set) {

	}
	s

}
```

<br></br>

# Question 4 :

On souhaite ajouter une méthode addAll(dedupVec) qui permet d'ajouter tous les éléments d'un DedupVec à un autre DedupVec.
Quelle doit être la signature (le type des paramètres) d'une telle méthode ?
Implanter la méthode addAll.
Vérifier que les tests marqués "Q4" passent.

    signature : public void addAll(DedupVec<T> dedupVec)

```java
package fr.uge.dedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class DedupVec<T> {

	private final ArrayList<T> dedupElements;
	private final HashMap<T, T> dedupMap;

	public DedupVec() {
		this.dedupMap = new HashMap<>();
		this.dedupElements = new ArrayList<>();
	}

	public int size() {
		return dedupElements.size();
	}

	public T get(int index) {
		if (index < 0 || index >= this.size())
			throw new IndexOutOfBoundsException("Error : index < 0");
		return dedupElements.get(index);
	}

	public void add(T element) {
		Objects.requireNonNull(element);
		T sameElem = dedupMap.get(element);
		if (sameElem == null) {
			dedupElements.add(element);
			dedupMap.put(element, element);
		} else {
			dedupElements.add(sameElem);
		}
	}

	public boolean contains(Object element) {
		Objects.requireNonNull(element);
		return dedupMap.containsKey(element);
	}

	public void addAll(DedupVec<T> dedupVec) {
		Objects.requireNonNull(dedupVec);
		dedupVec.dedupElements.forEach(this::add);
	}

	public void fromSet(T set) {

	}

}

```

<br></br>

# Question 5 :

Dans le but d'implanter la méthode fromSet de façon efficace, on souhaite ajouter une méthode d'aide (helper method) pas publique, newMapFromSet(set) qui prend en paramètre un ensemble et renvoie une Map qui pour chaque élément de l'ensemble associe ce même élément. Par exemple, si l'ensemble contient uniquement la chaîne de caractères "toto", on obtient une Map map telle que si on appelle map.get("toto"), le résultat est "toto".

Pour des questions d'efficacité (cf. question suivante) la Map renvoyée doit être une vue de l'ensemble pris en paramètre et si l'ensemble pris en paramètre contient la valeur null, l'appel à la méthode newMapFromSet(set) ne devra pas lever d'exception et c'est uniquement lorsque l'on essaye d'accéder à l'élément qu'une exception NullPointerException devra être levée.

Sachant qu'il existe la classe AbstractMap et AbstractSet et la méthode Map.entry(key, value), écrire la méthode newMapFromSet(set).
Vérifier que les tests marqués "Q5" passent.

```java

```