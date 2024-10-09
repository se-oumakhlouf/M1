## OUMAKHLOUF Selym


###############
### Exercice 2
###############

######################
	# Question 1 :
		Dans un premier temps, on va prendre une commande (une chaîne de caractères comme "u" ou "*4") et créer l'objet correspondant pour éviter de re-parser la commande à chaque ligne. On se propose de nommer cette objet Transformer car son rôle est de transformer une ligne du fichier en une nouvelle ligne.
		En plus de la méthode createTransformer qui créé un transformer pour une commande, on a besoin d'une autre méthode rewrite qui prend un reader, un writer et un transformer. Elle lit chaque ligne du reader, la transforme avec le transformer et l'écrit sur le writer.

        	var transformer = StreamEditor.createTransformer("u");  // exemple avec "u" comme commande
	        try(BufferedReader reader = ...;
	            BufferedWriter writer = ...) {
	          StreamEditor.rewrite(reader, writer, transformer);
	        }

		Pour l'instant, on ne s'intéresse qu'aux commandes "u", "l" et "*" suivie d'un chiffre (pas un nombre !) indiquant la répétition ("*4", "*7", etc), et on veut une classe par type de commande. Déclarer Transformer ainsi que 3 implantations UpperCaseTransformer, LowerCaseTransformer et StarTransformer.
		Puis créer la méthode createTransformer qui prend une commande, regarde le premier caractère et créé une instance de Transformer avec la bonne implantation.
		Enfin, écrire la méthode rewrite qui boucle sur chaque ligne du reader, envoie la ligne au transformer et écrit la ligne résultante dans le writer. Pour le retour à la ligne on utilisera un '\n', ainsi le programme fonctionnera de la même façon quelque soit l'OS.
		Vérifier que les tests marqués "Q1" passent.
		Note : il existe une méthode replace() sur la classe String. En fait, il en existe plusieurs, à vous de prendre la bonne. 
			
	
-- 

```java
package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public final class StreamEditor {

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		return switch (command.charAt(0)) {
			case 'u' -> new UpperCaseTransformer();
			case 'l' -> new LowerCaseTransformer();
			case '*' -> new StarTransformer(Integer.parseInt(command.substring(1)));
			default -> throw new IllegalArgumentException("Unexpected value: " + command.charAt(0));
		};
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;

		while ((line = reader.readLine()) != null) {
			switch (transformer) {
				case UpperCaseTransformer upper -> line = upper.transform(line);
				case LowerCaseTransformer lower -> line = lower.transform(line);
				case StarTransformer star -> line = star.transform(line);
			}
			writer.write(line);
			writer.write("\n");
		}
		reader.close();
		return;
	}

}

package fr.uge.sed;

public sealed interface Transformer permits UpperCaseTransformer, LowerCaseTransformer, StarTransformer {
//	String transform(String line);
}


package fr.uge.sed;

import java.util.Locale;

final class UpperCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toUpperCase(Locale.ROOT);
	}

}

package fr.uge.sed;

import java.util.Locale;

final class LowerCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toLowerCase(Locale.ROOT);
	}
	
}

package fr.uge.sed;

import java.util.Objects;

final class StarTransformer implements Transformer {
	
	private int stars;

	public StarTransformer(int stars) {
		Objects.requireNonNull(stars);
		if (stars > 10 || stars < 0) {
			throw new IllegalArgumentException("0 < stars < 10");
		}
		this.stars = stars;
	}
	
	public final String transform(String line) {
		return line.replace("*", "*".repeat(stars));
	}
}

```

--
######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 2 :
		On veut que notre programme fonctionne de la même façon, quelle que soit la machine or la norme Unicode. On demande que les méthodes String.toUpperCase() et String.toLowerCase() aient un comportement spécifique en fonction de la langue (Locale) par défaut de l'OS.
		Si vous ne l'avez pas déjà fait, changer votre code pour que la mise en majuscule/minuscule soit fait de façon indépendante de l'OS. Si vous ne vous souvenez plus de comment on fait, vous pouvez relire la javadoc de toUpperCase() ou toLowerCase()
		Modifier, si nécessaire, votre code et vérifier que les tests marqués "Q2" passent.
		
-- 

Pour que le programme fonctionne de la même manière peut imporet la machine qui il faut utiliser LOCAL.ROOT dans toUpperCase() et toLowerCase()

```java

package fr.uge.sed;

import java.util.Locale;

final class LowerCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toLowerCase(Locale.ROOT);
	}
	
}


package fr.uge.sed;

import java.util.Locale;

final class UpperCaseTransformer implements Transformer {

	public final String transform(String line) {
		return line.toUpperCase(Locale.ROOT);
	}

}

```

--

######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 3 :
		Vous avez sûrement utilisé le polymorphisme pour implanter Transformer alors que votre programme contrôle toutes les implantations possibles. C'est MAL, vous auriez du utiliser le pattern matching.
		Commenter votre code puis changer votre implantation pour utiliser le pattern matching plutôt que le polymorphisme.
		Vérifier que les tests marqués "Q3" passent.
		Rappel : Ne pas utiliser le polymorphisme veut dire ne pas avoir de méthode abstract dans Transformer. Et pattern matching, signifie implanter en faisant un switch sur toutes les implantations. 

--

Je suis tombé dans le piège du polymorphisme

```java

package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		return switch (command.charAt(0)) {
			case 'u' -> new UpperCaseTransformer() ;
			case 'l' -> new LowerCaseTransformer();
			case '*' -> new StarTransformer(Integer.parseInt(command.substring(1)));
			default -> throw new IllegalArgumentException("Unexpected value: " + command.charAt(0));
		};
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;

		while ((line = reader.readLine()) != null) {
			switch (transformer) {
				case UpperCaseTransformer _ -> line = line.toUpperCase(Locale.ROOT);
				case LowerCaseTransformer _ -> line = line.toLowerCase(Locale.ROOT);
				case StarTransformer (int count) -> line = line.replace("*", "*".repeat(count));
			}
			writer.write(line);
			writer.write("\n");
		}
		reader.close();
		return;
	}

}

package fr.uge.sed;

public record StarTransformer(int stars) implements Transformer{
	
	public StarTransformer(int stars) {
		this.stars = stars;
	}
}

package fr.uge.sed;

public record UpperCaseTransformer() implements Transformer{
	
}

package fr.uge.sed;

public record LowerCaseTransformer() implements Transformer {

}

package fr.uge.sed;

//@FunctionalInterface
public sealed interface Transformer permits UpperCaseTransformer, LowerCaseTransformer, StarTransformer {
//	String transform(String line);
}


```

--

######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 4 :
		En fait, les transformations sont des actions, donc pour chaque transformation, on pourrait utiliser une lambda, ainsi le code serait plus simple à écrire et donc à maintenir.
		Comment indiquer que Transformer est implantée en utilisant des lambdas ?
		Comment faire pour que l'on empêche d'autre code de fournir une autre implantation ?
		Vérifier que les tests marqués "Q4" passent. 

--
```java
package fr.uge.sed;

@FunctionalInterface
public interface Transformer {
	String transform(String line);
}

package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		return switch (command.charAt(0)) {
			case 'l' -> line -> line.toLowerCase(Locale.ROOT) ;
			case 'u' -> line -> line.toUpperCase(Locale.ROOT);
			case '*' -> line -> line.replace("*", "*".repeat(command.charAt(1) - '0'));
			default -> throw new IllegalArgumentException("Unexpected value: " + command.charAt(0));
		};
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;
		String newLine;

		while ((line = reader.readLine()) != null) {
			newLine = transformer.transform(line);
			writer.write(newLine);
			writer.write("\n");
		}
		reader.close();
		return;
	}

}
```

--
		
######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 5 :		
		On veut maintenant pouvoir gérer une commande qui est elle-même composée de plusieurs commandes comme lu ou *4u (qui respectivement mette la ligne en minuscule puis majuscule, remplace les étoiles par quatre étoiles puis mette la ligne en majuscule).
		Cela veut dire qu'il va falloir décomposer une commande en plusieurs commandes, pour cela, on se propose d'écrire une méthode parse(command, transformer, index) qui renvoie un tuple composé de la commande à l'index index ainsi que de l'index de la prochaine commande (pour la mise en majuscule/miniscule la prochaine commande est à l'index suivante mais pour les étoiles il faut sauter deux cases).
		Comment représente-t-on des tuples en Java ? Dans notre cas, quel est la représentation d'un tuple qui contient un Transformer et un index
		Écrire la méthode parse(command, transformer, index) et changer le code de la méthode createTransformer(command) en conséquence.
		Vérifier que les tests marqués "Q5" passent. 
		
--

En java, on utilise des records pour simuler des tuples

```java

package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String command) {
		Objects.requireNonNull(command);
		Transformer transformer = line -> line;
		int index = 0;
		while (index < command.length()) {
			TransformerIndexTuple parsed = parse(command, transformer, index);
			transformer = parsed.transformer();
			index = parsed.index();
		}
		return transformer;
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;
		String newLine;

		while ((line = reader.readLine()) != null) {
			newLine = transformer.transform(line);
			writer.write(newLine);
			writer.write("\n");
		}
		reader.close();
		return;
	}

	public static TransformerIndexTuple parse(String command, Transformer transformer, int index) {
		var currentCommand = command.charAt(index);
		Transformer newTransformer;

		switch (currentCommand) {
			case 'l' -> newTransformer = line -> line.toLowerCase(Locale.ROOT);
			case 'u' -> newTransformer = line -> line.toUpperCase(Locale.ROOT);
			case '*' -> {
				newTransformer = line -> line.replace("*", "*".repeat(command.charAt(index + 1) - '0'));
				return new TransformerIndexTuple(line -> newTransformer.transform(transformer.transform(line)), index + 2);
			}
			default -> throw new IllegalArgumentException("Unknow command : " + currentCommand);
		}
		return new TransformerIndexTuple(line -> newTransformer.transform(transformer.transform(line)), index + 1);
	}

}

package fr.uge.sed;

import java.util.Objects;

public record TransformerIndexTuple(Transformer transformer, int index) {
	
	public TransformerIndexTuple {
		Objects.requireNonNull(transformer);
		if (index < 0) {
			throw new IllegalArgumentException("Negative index");
		}
	}
}

```

--
		
######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

######################
	# Question 6 :	
		En fait, le code précédent est la façon fonctionnelle de voir la décomposition en transformer, on peut aussi écrire une version plus objet des choses. En POO, on va encapsuler les mutations, ici, la mutation est l'index qui nous indique là où décoder/parser le prochain transformer dans la commande. Encapsuler la mutation revient donc à déclarer une classe Parser avec un champ mutable qui va être modifié à chaque fois que l'on décode une transformation.
		On a de plus besoin en plus d'une méthode pour savoir si on est arrivé à la fin de la chaîne de caractères (appelée ici, canParse())
		
		  static final class Parser {
		    private final String commands;
		    private int index;
		
		    Parser(String commands) {
		      this.commands = commands;
		    }
		
		    boolean canParse() {
		      return index < commands.length;
		    }
		
		    Transformer parse(Transformer t) {
		      // TODO
		    }
		  }
		    
		
		Écrire la classe Parser et modifier la méthode createTransformer(commande) pour utiliser le parser (l'idée est de faire une boucle tant que canParse renvoie vrai et envoyer le précédent transformer à parse() qui retourne le nouveau transformer.
		Vérifier que les tests marqués "Q6" passent.
		Note : on peut remarquer qu'il y a un modificateur static devant le nom de la classe Parser, on verra la semaine prochaine en cours pourquoi.
		
--

```java

package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

public final class StreamEditor {

	private final Transformer t;

	static final class Parser {
		private final String commands;
		private int index;

		Parser(String commands) {
			this.commands = commands;
		}

		boolean canParse() {
			return index < commands.length();
		}

		Transformer parse(Transformer t) {

			Transformer newTransformer;
			var currentCommand = commands.charAt(index);

			switch (currentCommand) {
				case 'l' -> newTransformer = line -> line.toLowerCase(Locale.ROOT);
				case 'u' -> newTransformer = line -> line.toUpperCase(Locale.ROOT);
				case '*' -> {
					if (index + 1 >= commands.length())
						throw new IllegalArgumentException("Missing number after '*'");

					int repeatCount = commands.charAt(index + 1) - '0';
					newTransformer = line -> line.replace("*", "*".repeat(repeatCount));
					index += 2;
					return line -> newTransformer.transform(t.transform(line));
				}
				default -> throw new IllegalArgumentException("Unknow command : " + currentCommand);
			}
			index++;
			return line -> newTransformer.transform(t.transform(line));
		}
	}

	private StreamEditor() {
		throw new AssertionError();
	}

	public final static Transformer createTransformer(String commands) {
		Objects.requireNonNull(commands);
		Transformer transformer = line -> line;

		var parser = new Parser(commands);
		while (parser.canParse()) {
			transformer = parser.parse(transformer);
		}

		return transformer;
	}

	public final static void rewrite(BufferedReader reader, Writer writer, Transformer transformer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		Objects.requireNonNull(transformer);
		String line;
		String newLine;

		while ((line = reader.readLine()) != null) {
			newLine = transformer.transform(line);
			writer.write(newLine);
			writer.write("\n");
		}
		reader.close();
	}

}


```
--

######################

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	