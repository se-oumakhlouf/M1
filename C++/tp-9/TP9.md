# TP9 - Conteneurs et algorithmes

## Objectifs

- Utiliser quelques algorithmes de la librairie standard
- Définir des lambdas
- Utiliser différents types de conteneurs

## Exercice 1 - Documentation (20 min)

1. Consulter la documentation des conteneurs spécifiés afin de renseigner les complexités en temps de chacune des opérations :

| Conteneur     | Insertion (en tête / en fin) | Suppression (en tête / en fin) | Accès |
|---------------|--------------|--------------|--------------|
| array         |     N/A      |     N/A      |     $O(1)$   |
| vector        | $O(n)/O(1)$  | $O(n)/O(1)$  |     $O(1)$   |
| deque         | $O(1)/O(1)$  | $O(1)/O(1)$  |     $O(1)$   |
| forward_list  | $O(1)$ / N/A | $O(1)$ / N/A |      N/A     |
| list          | $O(1)/O(1)$  | $O(1)/O(1)$  |      N/A     |
| set           |     N/A      |     N/A      |      N/A     |
| unordered_set |     N/A      |     N/A      |      N/A     |

```
Les sets sont sous forme de Tree, on ne peut donc pas parler d'insertion/suppression en tête/en fin
```

2. Supposons que vous ayez récupéré un itérateur sur un élément d'un conteneur avec : `auto it = std::find(ctn.begin(), ctn.end(), element_to_find)`.  
En fonction du type de conteneur, quelles sont les opérations susceptibles d'invalider cet itérateur ? Essayez d'être précis dans vos réponses.  
Exemple : Si `ctn` est un `std::vector`, alors `it` peut être invalidé en cas de suppression d'un élément précédent `it` dans le conteneur.

   - section `Iterator invalidation` des différents conteneurs

3. Quelle est la différence entre les fonctions `push_back` et `emplace_back` de la classe-template `std::vector` ?

   - `push_back` ajoute un objet déjà construit au vecteur, `emplace_back` construit l’objet directement à l’intérieur du vecteur, évitant ainsi une copie ou un déplacement.

## Exercice 2 - Lambdas et algorithmes (40 min)

Ouvrez chacun des liens ci-dessous et complétez le code afin que les fonctions produisent les résultats attendus.  
Une seule contrainte, on vous imposera un algorithme de la librairie standard à utiliser.

1. Algorithme: std::remove_if  
   Code: https://godbolt.org/z/KaTnr8Pr4

```cpp
void remove_cats(std::vector<Animal>& animals)
{
    animals.erase(
        std::remove_if(
        animals.begin(), animals.end(), [](const Animal& animal) {return animal.species == "cat";}), 
        animals.end()
    );
}
```

2. Algorithme: std::find_if  
   Code: https://godbolt.org/z/55x9Efrza

```cpp
std::ptrdiff_t get_position_of_first_with_species(const std::deque<Animal>& animals, const std::string& species)
{
    const auto it = std::find_if(animals.begin(), animals.end(), [&species](const Animal& bestiole) { return bestiole.species == species; });
    return it != animals.end() ? it - animals.begin() : -1;
}
```

3. Algorithme: std::transform + std::back_inserter  
   Code: https://godbolt.org/z/PrPoEYK5d  

```cpp
std::vector<std::string> compute_full_names(const std::vector<Person>& persons)
{
    std::vector<std::string> full_names;
    std::transform(persons.begin(), persons.end(), std::back_inserter(full_names), [](const Person& p) {return p.first_name + " " + p.last_name;});
    return full_names;
}
```

4. Algorithme: std::accumulate  
   Code: https://godbolt.org/z/61P6K414P  

```cpp
#include <numeric>
#include <algorithm>

std::string capstring(const std::string& s)
{
    std::string result;
    std::transform(s.begin(), s.end(), std::back_inserter(result), [](const char& c) {return std::toupper(c); });
    return result;
}

std::string concat_in_caps(const std::vector<std::string>& words)
{
    std::string result;
    return std::accumulate(words.begin(), words.end(), result, [](const std::string& a, const std::string& b) {return a + capstring(b);});
}
```

5. Algorithme: peu importe, du moment que vous implémentez le contenu de apply_on_entities_with_type et que vous l'utilisez ensuite.  
   Code: https://godbolt.org/z/v5rn1aqGe  

```cpp
struct Entity
{
    Entity(std::string name, int life)
    : name { std::move(name) }
    , life { life }
    {}

    std::string name;
    int life = 100;
};

using Entities = std::list<std::unique_ptr<Entity>>;

void apply_on_entities_with_type(std::map<std::string, Entities>& all_entities,
                                 const std::string& type,
                                 const std::function<void(Entities&)>& fcn)
{
    for(auto& [pairType, entity]: all_entities) {
        if(pairType == type) {
            fcn(entity);
        }
    }
}

void print_entities(std::map<std::string, Entities>& all_entities, const std::string& type)
{
    apply_on_entities_with_type(all_entities, type, [](const Entities& ents) {for(auto& e: ents) std::cout << e->name << " (" << e->life << " HP)" << std::endl; });
}

void hit_monsters(std::map<std::string, Entities>& all_entities, int life_decrease)
{
    apply_on_entities_with_type(all_entities, "Monster", [&life_decrease](Entities& ents) { for (auto& e: ents) e->life -= life_decrease;});
}

void remove_dead_entities(std::map<std::string, Entities>& all_entities)
{
    auto remove_if_dead = [](Entities& ents){
        ents.erase(std::remove_if(ents.begin(), ents.end(), [](std::unique_ptr<Entity>& e) {return e->life <= 0;}), ents.end());
    };
    apply_on_entities_with_type(all_entities, "Monster", remove_if_dead);
    apply_on_entities_with_type(all_entities, "Human", remove_if_dead);
}

void add_entity(std::map<std::string, Entities>& all_entities, const std::string& type, std::unique_ptr<Entity> entity)
{
    apply_on_entities_with_type(all_entities, type, [&entity](Entities& ents) {ents.push_back(std::move(entity));});
}
```

## Exercice 3 - unordered_map (30 min)

L'objectif de cet exercice est de vous faire implémenter un type que vous pourrez utiliser en temps que clé de hashage d'une `unordered_map`.

### A. Point2d

1. Définissez une structure `Point2d` contenant deux attributs entiers `x` et `y`.
2. Définissez ensuite une enumération `Content` au moyen d'une `enum class`. Elle devra contenir les valeurs `Empty`, `Red` ou `Yellow`.
3. Ajoutez une fonction `main` à votre programme et définissez une variable `grid` de type `std::unordered_map<Point2d, Content>`. Quelle est l'erreur de compilation ?

### B. Spécifier des foncteurs

Pour utilisez une `unordered_map`, il faut définir une fonction de hash acceptant la clé du dictionnaire en paramètre. Il existe deux méthodes pour spécifier cette fonction.

1. Définissez une classe `Point2dHash` disposant d'une fonction-membre `operator()` qui :  
\- est `const`,
\- attend une instance de `Point2d`,
\- retourne un `std::size_t`.
En ce qui concerne le contenu de la fonction, vous pouvez chercher sur Internet une fonction qui permet de hasher une paire d'entiers.

2. En plus de la fonction de hash, vous devez définir une fonction permettant de calculer l'égalité entre deux clés. De la même manière que pour `Point2dHash`, définissez une classe `Point2dEqual`. Pour celle-ci, l'`operator()` devra :  
\- être `const` également,
\- attendre deux instances de `Point2d`,
\- retourner un booléen.

3. La classe-template `unordered_map` accepte en réalité 5 paramètres de template : le type des clés, le type des valeurs, le type du foncteur de hashage, le type du foncteur d'égalité et l'allocateur.  
Modifiez le type de `grid` de manière à ce que les classes `Point2dHash` et `Point2dEqual` soient utilisées pour instancier les foncteurs de hashage et d'égalité.

4. Vérifiez que vous pouvez désormais utiliser votre dictionnaire correctement (insertions, suppressions et accès).

### C. Utiliser les foncteurs par défaut

Il n'est pas forcément très pratique de devoir spécifier les foncteurs à utiliser dès lors que l'on souhaite utiliser des `Point2d` en tant que clé de conteneurs hashable.
Par défaut, ce sont les classes `std::hash<Point2d>` et `std::equal_to<Point2d>` qui sont utilisées pour instancier les foncteurs de hashage et d'égalité.

1. Par défaut, la classe-template `std::equal_to` tente d'appeler l'`operator==` du type concerné.  
Implémentez du coup cet opérateur pour la classe `Point2d`.  
Vous devriez maintenant pouvoir retirer l'argument `Point2dEqual` du type de `grid`.

Par défaut, la classe-template `std::hash` ne produit aucun `operator()` valide. Il est donc nécessaire de définir une spécialisation pour le type concerné si on souhaite pouvoir retirer l'argument `Point2dHash` du type de `grid`.  
Afin de définir une spécialisation pour la classe `std::hash`, il faut écrire :
```cpp
namespace std {   // il faut se placer dans le bon namespace

template <>
struct hash<type_pour_lequel_on_specialise>
{
   // nouveau contenu...
};

}
```

2. Définissez la spécialisation de `std::hash` pour le type `Point2d` et vérifiez que `grid` peut maintenant être définie avec le type `std::unordered_map<Point2d, Content>`.

## Exercice 4 - set (30 min)

L'objectif est le même que pour l'exercice précédent, mais le type servira de clé au type `set`.

### A. Point3d

1. Définissez une structure `Point3d` contenant trois attributs entiers `x`, `y` et `z`.
2. Ajoutez une fonction `main` à votre programme et définissez une variable `coords` de type `std::set<Point3d>`.
3. Essayez maintenant d'insérer des éléments dans le `coords`. Comment expliquez-vous que vous ayez une erreur de compilation maintenant alors que vous n'en aviez pas à la question précédente ?

### B. Spécifier des foncteurs

Pour utilisez un `std::set`, il faut définir une fonction permettant de comparer deux instances de clés entre elles.

1. Définissez une classe `Point3dCompare` disposant d'une fonction-membre `operator()` qui :  
\- est `const`,
\- attend deux instances de `Point3d`,
\- retourne un booléen indiquant si le premier paramètre est strictement inférieur au second.  
Pour implémentez le contenu de cette fonction en une seule instruction, vous pouvez utiliser la fonction `std::tie` afin de créer un tuple à partir de vos attributs (les tuples disposent d'un `operator<` si les attributs en possèdent un aussi).

2. La classe-template `set` accepte comme second paramètre de template le type du foncteur de comparaison.  
Modifiez le type de `coords` de manière à ce que la classe `Point3dCompare` soit utilisée pour instancier le foncteur de comparaison du conteneur.

3. Vérifiez que vous pouvez désormais utiliser votre `std::set` correctement (insertions, suppressions et accès).

### C. Utiliser les foncteurs par défaut

Si le type du foncteur de comparaison n'est pas spécifié, c'est la classe `std::less<Point3d>` qui sera utilisée pour instancier ce foncteur.

1. Par défaut, la classe-template `std::less` tente d'appeler l'`operator<` du type concerné.  
Implémentez `Point3d::operator<`.  

2. Vérifiez que vous pouvez maintenant retirer l'argument `Point3dCompare` du type de `coords`.  
