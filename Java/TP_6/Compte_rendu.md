### *Selym OUMAKHLOUF*

# Faites la queue

On souhaite écrire une structure de données pour représenter une file (first-in first-out ou FIFO) utilisant un tableau circulaire qui aura, au moins pour les premières questions, une taille fixée à la création. La taille doit être d'au moins 1 case et il est impossible de stocker null dans la file.
Les deux opérations principales sont offer qui permet d'insérer un élément à la fin de la file et poll qui permet de retirer un élément en début de la file.

L'idée est d'utiliser deux indices, un correspondant à la tête (head) de la file pour retirer les éléments et un correspondant à la queue (tail) de la file pour ajouter des éléments.
Prenons un exemple avec un tableau de 4 cases. Par défaut, la file est vide, on a head et tail à 0 :

       head   |
             xx xx xx xx
       tail   |
      

Si on ajoute l'élément 42 avec offer, on ajoute celui-ci à la fin de la liste, donc au niveau de tail, et on décale tail :

       head   |
             42 xx xx xx
       tail      |
      

Ensuite, si on ajoute respectivement 12 et 34, on obtient :

       head   |
             42 12 34 xx
       tail            |
      

Si on retire un élément (ici 42) avec poll, on retire l'élément à la position head et on décale head :

      head       |
             xx 12 34 xx
      tail             |
     

Enfin, si on ajoute les éléments 78 et 89, comme le tableau est vu comme un tableau circulaire, on obtient :

      head       |
             89 12 34 78
      tail       |
     
On peut remarquer que comme le tableau est plein, head et tail ont la même valeur.

Remarquez que si head et tail sont égaux, on ne sait pas si le tableau est vide ou si le tableau est plein... Il y a deux façons classiques de résoudre ce problème : soit le tableau n'est jamais plein et on garde toujours une case libre ; soit on ajoute un champ size qui contient le nombre d'éléments. Pour notre implantation, nous allons choisir la seconde solution.

Les tests JUnit 5 de cet exercice sont FifoTest.java.

<br></br>

# Question 1 : 

On souhaite écrire une classe Fifo générique (avec une variable de type E) dans le package fr.uge.fifo prenant en paramètre une capacité (un entier), le nombre d’éléments maximal que peut stocker la structure de données.
On souhaite de plus, écrire la méthode offer qui permet d'ajouter des éléments à la fin (tail) du tableau circulaire sachant que pour l'instant on ne se préoccupera pas du cas où le tableau est plein. Et une méthode size qui renvoie le nombre d'éléments ajoutés.
Écrire le code correspondant.
Vérifier que les tests marqués "Q1" passent !
Note : la capacité n'est pas nécessairement une puissance de 2.

```java

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}
}

```

<br></br>

# Question 2 :

Avez-vous pensé aux préconditions ?

Vérifier que les tests marqués "Q2" passent.

```java

// Q1 passe déjà Q2

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}
}

```

<br></br>

# Question 3 :

On souhaite écrire une méthode poll qui retire un élément du tableau circulaire et une méthode peek qui récupère cet élement sans le retirer. Que faire si la file est vide ?

Écrire le code de les méthodes poll et peek.

Vérifier que les tests marqués "Q3" passent.

```java

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

}


```

<br></br>

# Question 4 :

Rappelez ce qu'est un memory leak en Java et assurez-vous que votre implantation n'a pas ce comportement indésirable.

Pour cela, vous pouvez vérifier que les tests marqués "Q4" passent.

```java

// Il faut mettre les valeurs enlevées à *null*

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

}

```

<br></br>

# Question 5 :

On souhaite agrandir le tableau circulaire dynamiquement en doublant sa taille quand le tableau est plein. Attention, il faut penser au cas où le début de la liste (head) a un indice qui est supérieur à l'indice indiquant la fin de la file (tail).

De plus, on va ajouter un nouveau constructeur sans paramètre qui, par défaut, crée un tableau circulaire de taille 16.

Modifier votre implantation pour que le tableau s'agrandisse dynamiquement en ajoutant une méthode resize.

Vérifier que les tests marqués "Q5" passent.

Note : il existe les méthodes Arrays.copyOf et System.arraycopy.

```java

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public Fifo() {
		this(16);
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		if (size == elements.length) {
			resize();
		}
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

	public void resize() {
		@SuppressWarnings("unchecked")
		var tmp = (E[]) new Object[elements.length * 2];
		System.arraycopy(elements, head, tmp, 0, elements.length - head);
		System.arraycopy(elements, 0, tmp, elements.length - tail, elements.length - tail);
		head = 0;
		tail = size;
		elements = tmp;
	}

}

```

<br></br>

# Question 6 :

On souhaite ajouter une méthode d'affichage qui affiche les éléments dans l'ordre dans lequel ils seraient sortis en utilisant poll. L'ensemble des éléments devra être affiché entre crochets ('[' et ']') avec les éléments séparés par des virgules (suivies d'un espace).

Écrire la méthode d'affichage.

Vérifier que les tests marqués "Q6" passent.

Note : attention à bien faire la différence entre la file pleine et la file vide.

Note 2 : Il existe une classe StringJoiner qui est ici plus pratique à utiliser qu'un StringBuilder !

```java

public String toString() {
    if (size == 0) {
        return "[]";
    }
    var joiner = new StringJoiner(", ", "[", "]");
    var index = this.head;
    for (int i = 0; i < size; i++) {
        if (index >= elements.length) {
            index = 0;
        }
        joiner.add("" + elements[index]);
        index++;
    }
    return joiner.toString();
}
```

<br></br>

# Question 7 :

En fait, le code que vous avez écrit est peut-être faux (oui, les testent passent, et alors ?)... Le calcul sur les entiers n'est pas sûr/sécurisé en Java (ou en C, car Java à copié le modèle de calcul du C). En effet, une opération '+' sur deux nombres suffisamment grand devient négatif.
Si celà peut se produire dans votre code, modifiez-le ! L'astuce est d'utiliser deux indices, pour qu'il soit correct.

Dé-commenter le code du test correspondant à la question "Q7" et vérifier que votre code passe le test (comme on manipule des milliards d'objets, cela prend plusieurs dizaines de secondes).

Optionnellement, pour les plus balèzes, vous pouvez écrire aussi une version avec un Stream en faisant attention de ne pas avoir le même bug.

```java
// même code que Q6
```

<br></br>

# Question 8 :

Rappelez quel est le principe d'un itérateur.
Quel doit être le type de retour de la méthode iterator() ? Implanter la méthode iterator().

Vérifier que les tests marqués "Q8" passent.

Note : ici, pour simplifier le problème, on considérera que l'itérateur ne peut pas supprimer des éléments pendant son parcours.

```java

package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E>{
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public Fifo() {
		this(16);
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		if (size == elements.length) {
			resize();
		}
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

	public void resize() {
		@SuppressWarnings("unchecked")
		var tmp = (E[]) new Object[elements.length * 2];
		System.arraycopy(elements, head, tmp, 0, elements.length - head);
		System.arraycopy(elements, 0, tmp, elements.length - tail, elements.length - tail);
		head = 0;
		tail = size;
		elements = tmp;
	}

	public String toString() {
		if (size == 0) {
			return "[]";
		}
		var joiner = new StringJoiner(", ", "[", "]");
		var index = this.head;
		for (int i = 0; i < size; i++) {
			if (index >= elements.length) {
				index = 0;
			}
			joiner.add("" + elements[index]);
			index++;
		}
		return joiner.toString();
	}

	public Iterator<E> iterator() {
		return new Iterator<>() {

			private int index = head;

			@Override
			public boolean hasNext() {
				return index != size + head;
			}

			@Override
			public E next() {
				if (index >= size + head) {
					throw new NoSuchElementException();
				}
				if (index >= elements.length) {
					index = 0;
				}
				var elem = elements[index];
				index++;
				return elem;
			}
		};

	}

}

```

<br></br>

# Question 9 :

On souhaite que le tableau circulaire soit parcourable en utilisant une boucle for-each-in, comme ceci :

    var fifo = ...
    for(var value: fifo) {
      ...
    }
  
Quelle interface doit implanter la classe Fifo ?

Implanter cette interface et vérifier que les tests "Q9" passent.

```java
package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
	private int head;
	private int tail;
	private int size;
	private E[] elements;

	@SuppressWarnings("unchecked")
	public Fifo(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Fifo size < 1 at init");
		}
		this.elements = (E[]) new Object[capacity];
	}

	public Fifo() {
		this(16);
	}

	public void offer(E elem) {
		Objects.requireNonNull(elem);
		if (size == elements.length) {
			resize();
		}
		elements[tail] = elem;
		tail = (tail + 1) % elements.length;
		size++;
	}

	public int size() {
		return size;
	}

	public E poll() {
		if (size == 0) {
			return null;
		}
		var head = elements[this.head];
		elements[this.head] = null;
		this.head = (this.head + 1) % elements.length;
		this.size--;
		return head;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

	public void resize() {
		@SuppressWarnings("unchecked")
		var tmp = (E[]) new Object[elements.length * 2];
		System.arraycopy(elements, head, tmp, 0, elements.length - head);
		System.arraycopy(elements, 0, tmp, elements.length - tail, elements.length - tail);
		head = 0;
		tail = size;
		elements = tmp;
	}

	public String toString() {
		if (size == 0) {
			return "[]";
		}
		var joiner = new StringJoiner(", ", "[", "]");
		var index = this.head;
		for (int i = 0; i < size; i++) {
			if (index >= elements.length) {
				index = 0;
			}
			joiner.add("" + elements[index]);
			index++;
		}
		return joiner.toString();
	}

	public Iterator<E> iterator() {
		return new Iterator<>() {

			private int index = head;

			@Override
			public boolean hasNext() {
				return index != size + head;
			}

			@Override
			public E next() {
				if (index >= size + head) {
					throw new NoSuchElementException();
				}
				if (index >= elements.length) {
					index = 0;
				}
				var elem = elements[index];
				index++;
				return elem;
			}
		};

	}

}

```
