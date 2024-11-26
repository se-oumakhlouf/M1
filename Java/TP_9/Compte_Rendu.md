### *Selym OUMAKHLOUF*

<br></br>

# Single instruction, Multiple Data (SIMD)

<br></br>

## Exercice 1 : Maven

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>fr.uge.simd</groupId>
	<artifactId>simd</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<dependencies>

		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter-api</artifactId>
			<version>5.11.0</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter-params</artifactId>
			<version>5.11.0</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.openjdk.jmh</groupId>
			<artifactId>jmh-core</artifactId>
			<version>1.37</version>
		</dependency>

		<dependency>
			<groupId>org.openjdk.jmh</groupId>
			<artifactId>jmh-generator-annprocess</artifactId>
			<version>1.37</version>
			<scope>provided</scope>
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
					<annotationProcessorPaths>
						<path>
							<groupId>org.openjdk.jmh</groupId>
							<artifactId>jmh-generator-annprocess</artifactId>
							<version>1.37</version>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.6.0</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<finalName>benchmarks</finalName>
							<transformers>
								<transformer
									implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>org.openjdk.jmh.Main</mainClass>
								</transformer>
							</transformers>
							<filters>
								<filter>
									<artifact>*:*</artifact>
									<excludes>
										<exclude>**/module-info.class</exclude>
										<exclude>META-INF/MANIFEST.MF</exclude>
									</excludes>
								</filter>
							</filters>
						</configuration>
					</execution>
				</executions>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.13.0</version>
				<configuration>
					<release>23</release>
					<compilerArgs>
						<compilerArg>--add-modules</compilerArg>
						<compilerArg>jdk.incubator.vector</compilerArg>
					</compilerArgs>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.5.0</version>
				<configuration>
					<argLine>--add-modules jdk.incubator.vector</argLine>
				</configuration>
			</plugin>

		</plugins>

	</build>

</project>
```

<br></br>

## Exercice 2 : Vectorized Add / Vectorized Min

    Le but de cet exercice est de calculer la somme et le minimum d'un tableau de façon vectorisée (en utilisant des vecteurs de valeurs). Nous allons pour cela créer une classe fr.uge.simd.VectorComputation (dans src/main/java) qui contiendra les différentes méthodes statiques.
    Les tests unitaires correspondants s'appellent fr.uge.simd.VectorComputationTest (dans src/test/java).

    Note : il est possible de lancer les tests directement, mais il faut penser à rajouter --add-modules jdk.incubator.vector dans les options de la VM.
    Pour Maven, sous Eclipse, clic droit sur le projet, "run as..." puis maven clean et maven install.

    De plus la classe fr.uge.simd.VectorComputationBenchmark (dans src/main/java) contient les tests de performance JMH pour vérifier que le code que vous avez écrit est bel et bien plus performant qu'une simple boucle sur les données du tableau.

    Note : par défaut les benchmarks (les méthodes annotées avec @Benchmark) sont commentés, il faut les dé-commenter si vous voulez exécuter un benchmark. Et il faut penser à produire le jar correspondant.

<br></br>

### Question 1 :

    On cherche à écrire une fonction sum qui calcule la somme des entiers d'un tableau passé en paramètre. Pour cela, nous allons utiliser l'API de vectorisation pour calculer la somme sur des vecteurs.

    1.Quelle est la classe qui représente des vecteurs d'entiers ?
    2.Qu'est ce qu'un VectorSpecies et quelle est la valeur de VectorSpecies que nous allons utiliser dans notre cas ?
    3.Comment créer un vecteur contenant des zéros et ayant un nombre préféré de lanes ?
    4.Comment calculer la taille de la boucle sur les vecteurs (loopBound) ?
    5.Comment faire la somme de deux vecteurs d'entiers ?
    6.Comment faire la somme de toutes les lanes d'un vecteur d'entiers ?
    7.Si la longueur du tableau n'est pas un multiple du nombre de lanes, on va utiliser une post-loop, quel doit être le code de la post-loop ?

```txt
1 -> 
IntVector
```

```txt
2 -> 
Représente un type (byte|short|int|long|float|double) et une shape (64|128|256|512 bits), nous allons utiliser int|256
```

```java
// 3 -> 
VectorSpecies<Integer> species = IntVector.SPECIES_PREFERRED;    
IntVector v1 = IntVector.zero(species);
```

```java
// 4 -> 
var loopBound = SPECIES.loopBound(length);
// ou
var loopBound = length – length % SPECIES.length();
```

```java
// 5 ->
IntVector v1 = ...;
IntVector v2 = ...;
IntVector v3 = v1.add(v2);
```

```java
// 6 ->
int sum = v3.reduceLanes(VectorOperators.ADD);
```

```java
for(; i < length; i++) { // post loop
    newArray[i] = array[i];
}
```

```java
package fr.uge.simd;
import jdk.incubator.vector.*;

public class VectorComputation {
	private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
	
	public static int sum(int[] array) {
		var length = array.length;
		var loopBound = length - length % SPECIES.length();
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for (; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		int sum = v1.reduceLanes(VectorOperators.ADD);
		for (; i < length; i++) {
			sum += array[i];
		}
		return sum;
	}
}
```

```txt
Benchmark                                      Mode  Cnt   Score   Error  Units
VectorComputationBenchmark.sum_novector_array  avgt    5  25,268 ± 0,164  us/op
VectorComputationBenchmark.sum_vector_array    avgt    5   3,933 ± 1,445  us/op

Les performances pour la somme avec vecteur sont bien meilleures que les performances pour une somme d'un tableau
```

### Question 2 :

    On souhaite écrire une méthode sumMask qui évite d'utiliser une post-loop et utilise un mask à la place.

    Comment peut-on faire une addition de deux vecteurs avec un mask ?
    Comment faire pour créer un mask qui allume les bits entre i la variable de boucle et length la longueur du tableau ?

    Écrire le code de la méthode sumMask et vérifier que le test "testSumMask" passe.

    Que pouvez dire en termes de performance entre sum et sumMask en utilisant les tests de performances JMH ?

```java
package fr.uge.simd;
import jdk.incubator.vector.*;

public class VectorComputation {
	private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
	
	public static int sum(int[] array) {
		var length = array.length;
		var loopBound = length - length % SPECIES.length();
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for (; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		int sum = v1.reduceLanes(VectorOperators.ADD);
		for (; i < length; i++) {
			sum += array[i];
		}
		return sum;
	}
	
	public static int sumMask(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		var mask = SPECIES.indexInRange(i, length);
		var v3 = IntVector.fromArray(SPECIES, array, i, mask);
		v1 = v1.add(v3);
		return v1.reduceLanes(VectorOperators.ADD);
	}
}
```


```txt
Benchmark                                         Mode  Cnt   Score   Error  Units
VectorComputationBenchmark.sum_novector_array     avgt    5  25,193 ± 0,049  us/op
VectorComputationBenchmark.sum_vector_array       avgt    5   3,151 ± 0,001  us/op
VectorComputationBenchmark.sum_vector_mask_array  avgt    5   3,556 ± 0,107  us/op

Les sommes avec et sans masque sont équivalentes 
```


<br></br>

### Question 4 :

    On souhaite enfin écrire une méthode minMask qui au lieu d'utiliser une post-loop comme dans le code précédent, utilise un mask à la place.

    Attention, le minimum n'a pas d’élément nul (non, toujours pas !), donc on ne peut pas laisser des zéros "traîner" dans les lanes lorsque l'on fait un minimum sur deux vecteurs.

    Écrire le code de la méthode minMask et vérifier que le test nommé "testMinMask" passe.

    Que pouvez-vous dire en termes de performance entre min et minMask en utilisant les tests de performances JMH ?

```java
package fr.uge.simd;
import jdk.incubator.vector.*;

public class VectorComputation {
	private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
	
	public static int sum(int[] array) {
		var length = array.length;
		var loopBound = length - length % SPECIES.length();
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for (; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		int sum = v1.reduceLanes(VectorOperators.ADD);
		for (; i < length; i++) {
			sum += array[i];
		}
		return sum;
	}
	
	
	public static int min(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.min(v2);
		}
		var min = v1.reduceLanes(VectorOperators.MIN);
		for (; i < length; i++) {
			min = Integer.min(min, array[i]);
		}
		return min;
	}
	
	public static int sumMask(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		var mask = SPECIES.indexInRange(i, length);
		var v3 = IntVector.fromArray(SPECIES, array, i, mask);
		v1 = v1.add(v3, mask);
		return v1.reduceLanes(VectorOperators.ADD);
	}
	
	public static int minMask(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.min(v2);
		}
		var mask = SPECIES.indexInRange(i, length);
		var v3 = IntVector.fromArray(SPECIES, array, i, mask);
		v1 = v1.lanewise(VectorOperators.MIN, v3, mask);
		var min = v1.reduceLanes(VectorOperators.MIN);
		return min;
	}
	
}
```

```txt
Benchmark                                         Mode  Cnt   Score   Error  Units
VectorComputationBenchmark.min_loop_array         avgt    5  50,385 ± 0,183  us/op
VectorComputationBenchmark.min_vector_array       avgt    5   3,521 ± 0,025  us/op
VectorComputationBenchmark.min_vector_mask_array  avgt    5   3,531 ± 0,018  us/op
VectorComputationBenchmark.sum_novector_array     avgt    5  25,065 ± 0,010  us/op
VectorComputationBenchmark.sum_vector_array       avgt    5   3,526 ± 0,001  us/op
VectorComputationBenchmark.sum_vector_mask_array  avgt    5   3,584 ± 0,192  us/op
```






