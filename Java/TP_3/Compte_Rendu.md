##### Selym OUMAKHLOUF

# Slices of bread

## Exercice 2 : The Slice and The Furious

<br></br>

### Question 1 :

En Java, un slice correspond à l'interface suivante

    public interface Slice<E> {
        int size();
        E get(int index);
    }
    

Slice une interface paramétrée par le type d'élément du tableau sur lequel un Slice est créé. Les éléments peuvent ou on être null.
Voici un exemple d'utilisation d'un Slice

    String[] array = new String[] { "foo", "bar", "baz", "whizz" };
    Slice<String> slice = Slice.of(array, 1, 3);
    System.out.println(slice.size());  // 2
    System.out.println(slice.get(0));  // bar
    System.out.println(slice.get(1));  // baz
    
La méthode of permet de créer un slice sur un tableau avec les paramètres from et to, qui sont respectivement l'index du premier élément du slice et l'index après le dernier élément. Dit différemment, un slice correspond aux éléments dans l'intervalle [from, to[ (from inclus et to exclu).

Rappeler pourquoi doit-on déclarer encore une fois E en début de la méthode of

    static <E> Slice<E> of(E[] elements, int from, int to) {
        ...
    }
    

Écrire l'interface Slice et sa méthode of sachant que l'on va declarer l'implantation de l'interface SliceImpl en tant que classe interne de l'interface.
Vérifier que les tests marqués "Q1" passent.
Note : pour les préconditions, il existe les méthodes java.util.Objects.checkIndex() et Objects.checkFromToIndex().


#### Rep Q1 :

La méthode of est une méthode générique. Déclarer le type E une nouvelle fois en début de méthode permet de typée explicitement lors de l'appel et permet de faire fonctionner correctement avec différents types d'éléments.


```java
package fr.uge.slice;

import java.util.Objects;

public interface Slice<E> {

	int size();

	E get(int index);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}

	final class SliceImpl<E> implements Slice<E> {
		private final E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}
	}

}

```
<br></br>
### Question 2 :

On souhaite pouvoir afficher le contenu d'un slice avec la même syntaxe que pour une List.

    var array = new String[] { "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 3);
    System.out.println(slice);  // [bar, baz]
     

Sachant qu'il existe une méthode java.util.Arrays.stream(array, start, end), implanter la méthode d'affichage en utilisant un stream.
Vérifier que les tests marqués "Q2" passent.

#### Rep Q2 : 

```java
...

public interface Slice<E> {

	...

	final class SliceImpl<E> implements Slice<E> {

        ...

		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}
	}

}
```

<br></br>

### Question 3 :

On souhaite écrire une méthode subSlice(from, to) qui renvoie un slice qui est une sous-partie du slice sur lequel on appelle la méthode subSlice.
Par exemple

    var array = new String[]{ "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 4);
    var subSlice = slice.subSlice(1, 2);
    System.out.println(subSlice.size());  // 1
    System.out.println(subSlice.get(0));  // "baz"
      
Dans l'exemple ci-dessous, on crée un slice sur le tableau avec l'intervalle [1, 4[, puis on crée un second slice sur le premier slice avec l'intervalle [1, 2[. C'est équivalent à créer un slice sur [2, 3[ sur le tableau.
Implanter la méthode subSlice de telle façon que le code ci-dessus fonctionne.
Vérifier que les tests marqués "Q3" passent.

#### Rep Q3 :

```java

package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;

public interface Slice<E> {

	int size();
	E get(int index);
	Slice<E> subSlice(int from, int to);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}
	

	final class SliceImpl<E> implements Slice<E> {
		private final E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}
		

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}
		
		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}


		@Override
		public Slice<E> subSlice(int from, int to) {
			Objects.checkFromToIndex(from, to, size());
			return Slice.of(elements, this.from + from, this.from + to);
		}
	}

}

```

<br></br>

### Question 4 :

On souhaite maintenant implanter une méthode reversed qui renvoie un nouveau slice (une vue) qui permet de voir les éléments en sens inverse (sans copier les éléments).
Voici un exemple d'utilisation

    var array = new String[]{ "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 4);
    var reversed = slice.reversed();
    System.out.println(reversed.size()); // 3
    System.out.println(reversed.get(0));  // whizz
    System.out.println(reversed.get(1)); // baz
    System.out.println(reversed.get(2)); // bar
      
On peut remarquer que pour renverser un slice, il n'est pas nécessaire de connaître l'implantation si on a accès aux méthode size et get(index). On peut donc implanter la méthode reversed sous forme de méthode par défaut directement dans l'interface.
Implanter la méthode reversed sachant qu'en termes d'implantation, nous allons utiliser une classe anonyme et que pour l'instant, on ne va pas implanter la méthode subSlice(from, to)
Vérifier que les tests marqués "Q4" passent.
Note : en Java, lorsqu'une méthode abstraite est déclarée dans l'interface, on doit l'implanter. Mais si on ne veut pas l'implanter tout de suite (comme c'est demandé ici), on peut faire qu'elle lève une exception. Habituellement, on utilise l'exception UnsupportedOperationException pour cela.
Optimisation : si on reversed() un slice déjà reversed() on obtient le slice original.

#### Rep Q4 :

```java
package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;

public interface Slice<E> {

	int size();
	E get(int index);
	Slice<E> subSlice(int from, int to);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}
	
	default Slice<E> reversed() {
		return new Slice<E>() {

			@Override
			public int size() {
				return Slice.this.size();
			}

			@Override
			public E get(int index) {
				Objects.checkIndex(index, size());
				return Slice.this.get(size() - 1 - index);
			}

			@Override
			public Slice<E> subSlice(int from, int to) {
				throw new UnsupportedOperationException();
			}
			
			public Slice<E> reversed() {
				return Slice.this;
			}
			
		};
	}
	

	final class SliceImpl<E> implements Slice<E> {
		private final E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}
		

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}
		
		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}


		@Override
		public Slice<E> subSlice(int from, int to) {
			Objects.checkFromToIndex(from, to, size());
			return Slice.of(elements, this.from + from, this.from + to);
		}
	}

}

```

<br></br>

### Question 5 :

On souhaite ajouter à l'interface Slice une méthode replaceAll qui permet de remplacer chaque élément en appelant une fonction avec l'ancienne valeur de l'élément, la fonction renvoyant la nouvelle valeur de l'élément.
Par exemple, pour ajouter des étoiles autour des chaînes de caractères, on peut écrire

    var array = new String[]{ "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 4);
    slice.replaceAll(x -> "*" + x + "*");
    System.out.println(slice.get(0));  // *bar*
    System.out.println(slice.get(1));  // *baz*
    System.out.println(slice.get(2));  // *whizz*
      

Quelle est l'interface fonctionnelle que l'on doit utiliser en paramètre de replaceAll ?
Quelle est le type de retour de la méthode replaceAll ?
Ajouter la méthode replaceAll à l'interface Slice et modifier les implantations en conséquence.
Vérifier que les tests marqués "Q5" passent.

#### Rep Q5 :

L'interface fonctionnelle à utiliser est UnaryOperator et le type de retour est void ce qui donne la signature suivante :

    void replaceAll(UnaryOperator<E> operator);

```java
package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Slice<E> {

	int size();

	E get(int index);

	Slice<E> subSlice(int from, int to);

	void replaceAll(UnaryOperator<E> operator);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}

	default Slice<E> reversed() {
		return new Slice<E>() {

			@Override
			public int size() {
				return Slice.this.size();
			}

			@Override
			public E get(int index) {
				Objects.checkIndex(index, size());
				return Slice.this.get(size() - 1 - index);
			}

			@Override
			public Slice<E> subSlice(int from, int to) {
				throw new UnsupportedOperationException();
			}

			public Slice<E> reversed() {
				return Slice.this;
			}

			@Override
			public String toString() {
				return "["
						+ IntStream.range(0, size()).mapToObj(this::get).map(Object::toString).collect(Collectors.joining(", "))
						+ "]";
			}

			@Override
			public void replaceAll(UnaryOperator<E> operator) {
				Objects.requireNonNull(operator);
				Slice.this.replaceAll(operator);
			}

		};
	}

	final class SliceImpl<E> implements Slice<E> {
		private E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}

		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}

		@Override
		public Slice<E> subSlice(int from, int to) {
			Objects.checkFromToIndex(from, to, size());
			return Slice.of(elements, this.from + from, this.from + to);
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			Objects.requireNonNull(operator);
			for (int i = 0; i < size(); i++) {
				elements[i + from] = operator.apply(get(i));
			}
		}
	}
}
```

<br></br>

### Question 6 :

On souhaite maintenant implanter la méthode subSlice(from, to) quand le Slice est reversed.
Par exemple

    var array = new String[]{ "foo", "bar", "baz", "whizz" };
    var slice = Slice.of(array, 1, 4);
    var reversed = slice.reversed();
    var subSlice = reversed.subSlice(1, 3);
    System.out.println(subSlice.get(0));  // baz
    System.out.println(subSlice.get(0));  // bar
      

Implanter la méthode subSlice(from, to) dans la classe anonyme dans la méthode reversed().
Vérifier que les tests marqués "Q6" passent.
Note : vous avez le droit de prendre un papier pour comprendre comment calculer les index correctement.

#### Rep Q6 :

```java
// méthode modifier :

@Override
public Slice<E> subSlice(int from, int to) {
	Objects.checkFromToIndex(from, to, size());
	return Slice.this.subSlice(size() - to, size() - from).reversed();
}



// code entier :

package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Slice<E> {

	int size();

	E get(int index);

	Slice<E> subSlice(int from, int to);

	void replaceAll(UnaryOperator<E> operator);

	static <E> Slice<E> of(E[] elements, int from, int to) {
		Objects.checkFromToIndex(from, to, elements.length);
		return new SliceImpl<>(elements, from, to);
	}

	default Slice<E> reversed() {
		return new Slice<E>() {

			@Override
			public int size() {
				return Slice.this.size();
			}

			@Override
			public E get(int index) {
				Objects.checkIndex(index, size());
				return Slice.this.get(size() - 1 - index);
			}

			@Override
			public Slice<E> subSlice(int from, int to) {
				Objects.checkFromToIndex(from, to, size());
				return Slice.this.subSlice(size() - to, size() - from).reversed();
			}
			
			public Slice<E> reversed() {
				return Slice.this;
			}

			@Override
			public String toString() {
				return "["
						+ IntStream.range(0, size()).mapToObj(this::get).map(Object::toString).collect(Collectors.joining(", "))
						+ "]";
			}

			@Override
			public void replaceAll(UnaryOperator<E> operator) {
				Objects.requireNonNull(operator);
				Slice.this.replaceAll(operator);
			}

		};
	}

	final class SliceImpl<E> implements Slice<E> {
		private E[] elements;
		private final int from;
		private final int to;

		public SliceImpl(E[] elements, int from, int to) {
			this.elements = elements;
			this.to = to;
			this.from = from;
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return elements[from + index];
		}

		@Override
		public String toString() {
			var array = Arrays.stream(elements, from, to).toArray();
			return Arrays.toString(array);
		}

		@Override
		public Slice<E> subSlice(int from, int to) {
			Objects.checkFromToIndex(from, to, size());
			return Slice.of(elements, this.from + from, this.from + to);
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			Objects.requireNonNull(operator);
			for (int i = 0; i < size(); i++) {
				elements[i + from] = operator.apply(get(i));
			}
		}
	}

}

```

<br></br>

### Question 7 :


#### Rep Q7 : 

Mon ancienne méthode ci dessous ne fonctionnait pas en cas de value null car il n'est pas possible de toString sur un Object null

	@Override
	public String toString() {
		return "["
				+ IntStream.range(0, size()).mapToObj(this::get).map(Object::toString).collect(Collectors.joining(", "))
				+ "]";
	}

```java
// Nouvelle version

@Override
public String toString() {
	// String.valueOf transforme les null en "null"
	return "[" + IntStream.range(0, size()).mapToObj(i -> String.valueOf(get(i))).collect(Collectors.joining(", "))
			+ "]";
}
```





