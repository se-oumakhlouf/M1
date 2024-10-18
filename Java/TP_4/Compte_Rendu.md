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
package fr.uge.set;

import java.util.LinkedList;
import java.util.Objects;

public class HashTableSet {
	
	private final LinkedList<Entry> hashTableSet = new LinkedList<>();
	
	private record Entry(Object element, Entry next) {

		public Entry {
			Objects.requireNonNull(element);
		}
	}
}
```