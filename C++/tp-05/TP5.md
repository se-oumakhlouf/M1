# TP5 - Copie et déplacement

## Objectifs

- Identifier les l-values et les r-values
- Comprendre le mécanisme du déplacement
- Définir un constructeur de copie
- Définir un constructeur de déplacement
- Définir un opérateur d'assignation par copie
- Définir un opérateur d'assignation par déplacement

## Exercice 1 - L-value ou R-value ? (30 min)

1. Identifiez si les expressions ci-dessous sont des l-values ou des r-values et justifiez.  

a. `i` est un `int`  
```cpp
4        // -> R-value
4 + i    // -> R-value
i        // -> L-value
i = 4    // -> L-value
i == 4   // -> R-value
```

b. `vec` est un `std::vector<char>`
```cpp
vec[5]                           // -> L-value
vec                              // -> L-value
vec.pop_back()                   // -> void
std::vector { 'a', 'b', 'c' }    // -> R-value
vec.emplace_back('d')            // -> L-value
std::move(vec)                   // -> R-value
```

c. `ptr` est un pointeur de `int`
```cpp
ptr + 3     // -> R-value
*(ptr + 3)  // -> L-value
*ptr + 3    // -> R-value
```

d. `str` est une `std::string`
```cpp
std::string { "aaaa" }  // -> R-value
str                     // -> L-value
str + "aaaa"            // -> R-value
str += "aaaa"           // -> L-value
```

2. Supposons que vous ayiez l'instruction : `auto inst = Class { expr };`.  
Quelles sont les deux conditions pour que le constructeur de copie soit appelé ?  
Même question pour le constructeur de déplacement ?

   - La classe `Class` doit avoir un constructeur de copie (ou une implémentation par défaut)
   - `expr` doit être de type `Class&` ou `const Class&`

## Exercice 2 - Minimisons les copies du tp02 (45 min)

Cet exercice est simple. Nous avons repris une solution du tp02 (un peu amélioré) dans lequel quasiment toutes les fonctions passent leurs argument par valeur. Il n'y a donc aucune fuite mémoire, mais le code fait des copies (notamment de `Card`) sans arrêt.\
Votre mission est de faire en sorte qu'il continue de fonctionner pareil, mais sans jamais ne faire aucune copie de `Card` et sans créer de fuite mémoire.

On utilise la classe `Tracker` pour suivre les copies au fil du code.

1. Ouvrez le fichier `Tracker.hpp` et lisez le.  

2. Pour chacun des 6 premiers constructeurs/opérateurs/Destructeurs de `Tracker`, écrivez leur nom en commentaire au dessus.

3. Quelle est la règle des `0/3/5` et est-ce que `Tracker` la suit?

```
- 1.  Destructeur
- 2.  Constructeur de copie
- 3.  Opérateur d’affectation par copie
- 4.  Constructeur de déplacement
- 5.  Opérateur d’affectation par déplacement
   
- Pour une classe donnée, il faut **généralement** définir à la main:
   - **aucun** des cinq éléments éléments ci-dessus
   - les **trois** premiers (destructeurs, constructeur de copie et opérateur d'affectation par copie)
   - ou les **cinq**

- Dans **Tracker.hpp** on retrouve :
   - Destructeur: ~Tracker() { --_count; }
   - Constructeur de copie: Tracker(const Tracker& o) ...
   - Opérateur d'affectation par copie: Tracker& operator=(const Tracker& other) ...
   - Constructeur de déplacement: Tracker(Tracker&& o) noexcept ...
   - Opérateur d'affectation par déplacement: Tracker& operator=(Tracker&& other) noexcept = default;

- Tracker suit bien la règle eds 0/3/5 en fournissant les 5
```

Pour traquer les instances de `Card`, on utilise le fait que `Card` possède un attribut de type `Tracker`, qui sera donc:
- construit quand une `Card` est construite;
- copié quand une `Card` est copiée;
- déplacé quand une `Card` est déplacée.

Pour ne pas casser notre exercice, faire en sorte que `Card` continue de suivre la règle des 0!

Regarder maintenant le `main.cpp`.  Il y a des appels à `assert_no_copy` et `assert_mem_count` qui vérifient le nombre de copie et de `Card` en mémoire à la fin du code. Vous pouvez rajouter des `assert_no_copy` et des `assert_mem_count` pour vous aider à débugguer la mémoire. 
Hormis cela, vous n'avez la plupart du temps qu'à modifier les prototypes des fonctions et comment elles sont appelées.

4. A vos claviers: 
   - `mkdir build && cd build && cmake ..` pour faire le Makefile
   - `make ex2 && ./ex2 10` pour compiler et lancer 10 tours de bataille.


## Exercice 3 - Implémentations de listes chaînées ( 45min )

Dans l'exercice 2, on a montré comment un developpeur utilise les copie et déplacements le plus fréquemment, c'est-à-dire comme "client" des structures de données déjà codées.\
Dans cet exercice, on va prendre le rôle inverse, celui du développeur d'une nouvelle structure de donnéé révolutionnaire: les liste doublement chainées.
Le but est de ne pas faire de fuite mémoire et de faire le minimum de copies.

Description du code fourni:
- La classe `Tracker`, comme dans l'exercice 2 permet de traquer les copies et les instances présentes en mémoire.
- Le main contient des tests pour votre code. Vous pouvez ajouter du debug dans le main mais me gardez le flow général du TP.  N'hésitez pas à décommenter les lignes une par une plutôt que partie par partie.
- La classe `Person` est implémentée naïvement et vous devez l'améliorer tout en gardant les fonctionnalités.
- Des prototypes de fonction-membres sont données dans les autres classes (`Link`, `LinkedList`) pour vous aider à comprendre ce qu'on attend. Leurs prototype n'est pas forcément correctes.

1. Pour commencer, on va écrire la classe `Link`, qui implémente un chainon de la liste.  A ce stade, les attributs `_prev` et `_next` restent toujours des pointeurs nuls.

2. La partie 2 demande d'implémenter les fonctionnalités de base d'une liste dans la classe `LinkedList`: ajout d'un élément, accès au début et à la fin de la liste, taille de la liste, affichage, etc.
Les attributs de `LinkedList` sont donnés pour vous guider.
Attention au fait que pour faire passer le dernier test de cette partie, il faut désallouer correctement les éléments dans la liste.

3. On fera en suite en sorte que la copie d'une liste se passe correctement (les éléments de la liste sont effectivements copiés).\
Bien que ça ne soit pas testé dans le main qu'on vous a donné, qu'est-ce qu'il faudrait aussi implémenter dans la classe `LinkedList` ?

4. Même chose pour le déplacement d'une liste.\
Bien que ça ne soit pas testé dans le main qu'on vous a donné, qu'est-ce qu'il faudrait aussi implémenter dans la classe `LinkedList` ?

5. Ici on vous demande d'implémenter la concaténation de deux listes.  Ici, on est dans le cas d'école où on peut gagner du temps quand on à le droit de voler le contenu de l'argument (à `LinkedList::concatenate_back`).

6. Pour préparer la question suivante, implémentons une fonction pour tester si une Personne est dans la liste. Elle renvoie `true` si l'argument appartient à la liste.  On pourra utiliser l'opérateur l'égalité de `Person`.

7. En réutilisant la fonction précédente, on veut implémenter une fonction qui ajoute dans la liste un élément qui n'est pas déjà présent.  La difficulté ici se trouve dans le fait qu'on ne peut pas savoir statiquement si on aura besoin de faire une copie de l'argument ou non.

8. Si vous n'avez pas utilisez `std::move` dans le constructeur de Person : bouh, vous ne travaillez que contre les tests, et vous n'avez pas pris toutes les bons reflexes de programmation!