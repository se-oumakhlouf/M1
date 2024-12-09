### *Selym OUMAKHLOUF*

<br></br>

# L'API des gatherers

<br></br>

## Exercice 1 : Maven

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.uge.gatherer</groupId>
    <artifactId>gatherer</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.11.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>23</release>
                    <compilerArgs>
                        <compilerArg>--enable-preview</compilerArg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <argLine>--enable-preview</argLine>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

<br></br>

## Exercie 2 : GathererDemo

<br></br>

### Question 1 :

On souhaite écrire une méthode filterIntegers qui permet de filtrer un Stream d'Integer, pour garder uniquement que les entiers qui sont vrais pour la fonction prise en paramètre.

```java
var list = List.of(1, 2, 3, 7, 10);
var result = list.stream().gather(GathererDemo.filterIntegers(x -> x % 2 == 0)).toList();
```    

Quelle est l'interface fonctionnelle dans java.util.fonction qui correspond à la fonction prise en paramètre de filterIntegers ?

Quels sont les types des 3 paramètres du Gatherer que la méthode filterIntegers va renvoyer.

Le Gatherer a-t-il besoin d'un état ?

Quelle version de la méthode Gatherer.ofSequential() allons-nous utilisez ici ?

Écrire la méthode filterIntegers.

Vérifier que les tests marqués "Q1" passent.

```txt
L'interface fonctionnelle est IntPredicate
```

```java
Gatherer<Integer, ?, Integer>
```

```txt
Non
```

```java
static <T,R> GathererPREVIEW<T,Void,R> ofSequential(Gatherer.IntegratorPREVIEW<Void,T,R> integrator)
```


```java
package fr.uge.gatherer;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.stream.Gatherer;

public class GathererDemo {

	public static Gatherer<Integer, ?, Integer> filterIntegers(IntPredicate predicate) {
		Objects.requireNonNull(predicate);
		return Gatherer.ofSequential((_, element, downstream) -> {
			if (predicate.test(element)) {
				downstream.push(element);
			}
			return true;
		});
	}
	
}
```

<br></br>

### Question 2 :

On veut maintenant écrire une méthode takeWhileIntegers qui renvoie un Gatherer qui fonctionne comme la méthode Stream.takeWile mais qui ne fonctionne que sur un Stream d'Integer.

Comme pour la question précédente, quelle est l'interface fonctionnelle prise en paramètre de takeWhileIntegers ? Quel est le type de retour de takeWhileIntegers ? Quelle est la version de Gatherer.ofSequential() que allons nous utiliser ?

Écrire la méthode takeWhileIntegers.

Vérifier que les tests marqués "Q2" passent.

Rappel : takeWhile() fait passer les éléments du Stream tant que la fonction prise en paramètre renvoie vrai.

```txt
On prend un IntPredicate
```

```java
Gatherer<Integer, ?, Integer>
```

```java
static <T,R> GathererPREVIEW<T,Void,R> ofSequential(Gatherer.IntegratorPREVIEW<Void,T,R> integrator)
```

```java
public static Gatherer<Integer, ?, Integer> takeWhileIntegers(IntPredicate predicate) {
    Objects.requireNonNull(predicate);
    return Gatherer.ofSequential((_, element, downstream) -> {
        if (predicate.test(element)) {
            return downstream.push(element);
        }
        return false;
    });
}
```

<br></br>

### Question 3 :

On souhaite écrire une méthode takeWhile qui, contrairement à la méthode précédente, fonctionne sur n'importe quel type de Stream.

Quelle est l'interface fonctionnelle prise en paramètre de takeWhile ? Quel est le type de retour de takeWhile ? Quelle est la version de Gatherer.ofSequential() que allons nous utiliser ?

Écrire la méthode takeWhile.

Vérifier que les tests marqués "Q3" passent.

```txt
On utilise un Predicate<T>
```

```java
<T> Gatherer<T, ?, T>
```

```java
static <T,R> GathererPREVIEW<T,Void,R> ofSequential(Gatherer.IntegratorPREVIEW<Void,T,R> integrator)
```

```java
public static <T> Gatherer<T, ?, T> takeWhile(Predicate<T> predicate) {
    Objects.requireNonNull(predicate);
    return Gatherer.ofSequential((_, element, downstream) -> {
        if (predicate.test(element)) {
            return downstream.push(element);
        }
        return false;
    });
}
```

<br></br>

### Question 4 :

On veut maintenant écrire une méthode indexed() qui renvoie un Gatherer qui ajoute à chaque élément un index correspondant à sa position dans le Stream (en commençant à 0).
Par exemple

```java
var list = List.of("foo", "bar");
var result = list.stream().gather(GathererDemo.indexed()).toList();
System.out.println(result);  // [Indexed[element=foo, index=0], Indexed[element=bar, index=1]]
```    

Pour cela, nous allons créer, dans un premier temps, un record Indexed avec comme composants un élément (element) et un index (index).

Écrire le record Indexed à l'intérieur de GathererDemo.

Expliquer pourquoi le Gatherer renvoyé par indexed() à besoin d'un état. Quel est le type de retour de indexed ? Quelle est la version de Gatherer.ofSequential() que allons nous utiliser ?

Écrire la méthode indexed.

Vérifier que les tests marqués "Q4" passent.

```txt

```

```java
<T> Gatherer<T, ?, Indexed<T>>
```

```java
static <T,A,R> GathererPREVIEW<T,A,R> ofSequential(Supplier<A> initializer, Gatherer.IntegratorPREVIEW<A,T,R> integrator)
```

```java
public static <T> Gatherer<T, ?, Indexed<T>> indexed() {
    class State {
        int index = 0;
    }
    return Gatherer.ofSequential(State::new, (state, element, downstream) -> {
        return downstream.push(new Indexed<T>(element, state.index++));
    });
}
```
