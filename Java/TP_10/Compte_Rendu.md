### *Selym OUMAKHLOUF*

<br></br>

# JSON, réflexion et annotations

<br></br>

## Exercice 1 : Maven

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>
	<groupId>fr.uge.dedup</groupId>
	<artifactId>dedup</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

	<dependencies>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.18.1</version>
			<scope>test</scope>
		</dependency>
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

## Exercice 2 :

<br></br>

### Question 1 :

    Avant de se lancer dans l'écriture de la méthode toJSON, on va commencer par écrire une méthode d'aide (helper method) invoke(method, object) qui appelle la méthode method sur l'objet object en utilisant la réflexion. On extrait le code de cette méthode du reste du code, car on veut gérer les exceptions correctement.

    Quelle est la méthode qui permet d'appeler une méthode (de type java.lang.reflect.Method) sur un objet ?

    Quelle est l'exception qui peut être levée...

        ... parce que les arguments de la méthode de java.lang.reflect.Method ne sont pas bons ?
        Comment doit-on la traiter ?

        ... parce que la méthode à appeler n'est pas visible ?

        Ici on veut lever une Error si c'est le cas, quelle Error doit-on lever, et comment faire ?

        ... parce que la méthode appelée lève elle-même une exception checked, une exception non checked ou une erreur ?
        Pour chacun de ses 3 cas, que doit-on faire ?

    Écrire la helper method invoke(method, object) et vérifier que les tests marqués "Q1" passent.

<br></br>


    Méthode permettant d'appeler une méthode sur un objet :
        Object invoke(Object receiver, Object… args)

    Exception :
        type d'arguments                    ->      IllegalArgumentException()  -> on ne fait rien   
        méthode pas accessible              ->      IllegalAccessException()    -> throw IllegalAccessError
        méthode lève une exception checked  ->      InvocationTargetException   -> propager l'exception "cause"

```java
package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class JSONPrinter {

	public static String toJSON(Person person) {
		return """
				{
				  "firstName": "%s",
				  "lastName": "%s"
				}
				""".formatted(person.firstName(), person.lastName());
	}

	public static String toJSON(Alien alien) {
		return """
				{
				  "age": %s,
				  "planet": %s
				}
				""".formatted(alien.age(), alien.planet());
	}

	static Object invoke(Method accessor, Object o) {
		try {
			return accessor.invoke(o);
		} catch (IllegalAccessException e) {
			throw (IllegalAccessError) new IllegalAccessError().initCause(e);
		} catch (InvocationTargetException e) {
			switch (e.getCause()) {
				case RuntimeException rte -> throw rte;
				case Error error -> throw error;
				case Throwable throwable -> throw new UndeclaredThrowableException(e);
			}
		}
	}

	public static void main(String[] args) {
		var person = new Person("John", "Doe");
		System.out.println(toJSON(person));
		var alien = new Alien(100, "Saturn");
		System.out.println(toJSON(alien));
	}
}
```

<br></br>

### Question 2 :

    On souhaite maintenant écrire la méthode toJSON qui prend en paramètre un java.lang.Record, utilise la réflexion pour accéder à l'ensemble des composants d'un record (java.lang.Class.getRecordComponent), sélectionne les accesseurs, puis affiche les couples nom du composant, valeur associée au format JSON.
    Puis vérifier que les tests marqués "Q2" passent.

    Note : il est recommandé d'écrire la méthode en utilisant un Stream.
    Note 2 : il y a une petite subtilité avec les guillemets. Dans le format JSON, les chaînes de caractères apparaissent entre "". Nous vous offrons la méthode suivante pour gérer cela :

```java
private static String escape(Object o) {
    return o instanceof String s ? "\"" + s + "\"": "" + o;
}
```

```java
public static String toJSON(Record record) {
    return Arrays.stream(record.getClass().getRecordComponents()).map(component -> {
        var prefix = "\"" + component.getName() + "\": ";
        var accessor = component.getAccessor();
        return prefix + escape(invoke(accessor, record));
    }).collect(Collectors.joining(", ", "{", "}"));
}
```

<br></br>

### Question 3 :

    En fait, on peut avoir des noms de clé d'objet JSON qui ne sont pas des noms valides en Java, par exemple "book-title", pour cela, on se propose d'utiliser une annotation pour indiquer quel doit être le nom de clé utilisée pour générer le JSON.

    Déclarez l'annotation JSONProperty visible à l'exécution et permettant d'annoter des composants de record, puis modifiez le code de toJSON pour n'utiliser que les propriétés issues de méthodes marquées par l'annotation JSONProperty.

    Puis vérifier que les tests marqués "Q3" passent.

```java
package fr.uge.json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JSONProperty {

	String value();

}
```

```java
public static String toJSON(Record record) {
    return Arrays.stream(record.getClass().getRecordComponents()).map(component -> {
        var annotation = component.getAnnotation(JSONProperty.class);
        String name = component.getName();
        if (annotation != null) {
            name = annotation.value();
        }
        var prefix = "\"" + name + "\": ";
        var accessor = component.getAccessor();
        return prefix + escape(invoke(accessor, record));
    }).collect(Collectors.joining(", ", "{", "}"));
}
```

<br></br>

### Question 4 :

    On souhaite maintenant pouvoir gérer des listes de records, pour cela, nous allons ajouter une surcharge à la méthode toJSON qui prend en paramètre une liste de records.

    Écrire la méthode toJSON(list).
    
    Puis vérifier que les tests marqués "Q4" passent.
