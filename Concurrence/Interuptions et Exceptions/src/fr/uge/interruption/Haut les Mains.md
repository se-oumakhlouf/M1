# Question 1 :

	Pourquoi n'est il pas possible d’arrêter un thread de façon non coopérative ?
	
	Cela n'est pas possible car arreter une thread de façon non coopérative peut bloquer entièrement le programme
		(ex : arrêt sans rendu d'un lock)
		
<br></br>

# Question 2 :

	Rappeler ce qu'est un appel de méthode bloquant.
	
	Un appel de méthode est bloquant si il met le thread courant en attente
		(ex : .wait(), Thread.sleep())
		
<br></br>

# Question 3 :

	À quoi sert la méthode d'instance interrupt() de la classe Thread?
	
	La méthode interrupt() sert à mettre le boolean de Thread.Interrupted() à vrai pour signaler gentiment au Thread que l'on souhaite qu'il s'arrête 
	
<br></br>

# Question 4 :

	Expliquer comment interrompre un thread en train d'effectuer un appel de méthode bloquant et le faire sur l'exemple suivant : le thread main attend 5 secondes avant d'interrompre le thread qui dort et ce dernier affiche son nom.

```java
package fr.uge.interruption;

public class HautLesMains {
	
  public static void main(String[] args) throws InterruptedException {
    var thread = Thread.ofPlatform().start(() -> {
      for (var i = 1;; i++) {
        try {
          Thread.sleep(1_000);
          System.out.println("Thread slept " + i + " seconds.");
        } catch (InterruptedException e) {
          return;
        }
      }
    });
    Thread.sleep(5_100); // ne lève jamais d'exception car on interrupt jamais le Thread Main
    thread.interrupt();
  }

}
```

<br></br>

# Question 5 :

	Expliquer, sur l'exemple suivant, comment utiliser la méthode Thread.interrupted pour arrêter le calcul de findPrime() qui n'est pas une méthode bloquante. Modifier le code de findPrime (mais ni sa signature, ni isPrime) pour pouvoir l'interrompre. Dans ce cas, elle renvoie un OptionalLong vide.
	Puis faire en sorte que le main attende 3 secondes avant d'interrompre le thread qui cherche un nombre premier, en affichant "STOP".
	
```java
	public static boolean isPrime(long candidate) {
		if (candidate <= 1) {
			return false;
		}
		for (var i = 2L; i <= Math.sqrt(candidate); i++) {
			if (candidate % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static OptionalLong findPrime() {
		var generator = ThreadLocalRandom.current();
		for (;;) {
			var candidate = generator.nextLong();
			if (isPrime(candidate)) {
				return OptionalLong.of(candidate);
			}
			if (Thread.interrupted()) {
				return OptionalLong.empty();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var thread = Thread.ofPlatform().start(() -> {
			System.out.println("Found a random prime : " + findPrime().orElseThrow());
		});
		Thread.sleep(3_000);
		thread.interrupt();
		System.out.println("STOP");
	}
```

<br></br>

# Question 6 :

	Expliquer la (trop) subtile différence entre les méthodes Thread.interrupted et thread.isInterrupted de la classe Thread.
	
	
	thread.isInterrupted() ne modifie pas la valeur du boolean alors que Thread.Interrupted() repasse le boolean à false

<br></br>

# Question 7 :

	On souhaite maintenant faire en sorte que findPrime s'arrête dès que possible si le thread qui l’utilise est interrompu. Pour cela, modifier le code de findPrime et/ou isPrime sans modifier leur signature.

```java
// pas fait
```

<br></br>

# Question 8 et 9 :

	 Et si vous pouvez modifier le code des méthodes ET leur signature, que faites-vous ?


 	Pouvez-vous garantir que le programme afichera soit un nombre, soit "STOP", mais pas les deux ?

```java
	public static boolean isPrime(long candidate) throws InterruptedException {
		if (candidate <= 1) {
			return false;
		}
		for (var i = 2L; i <= Math.sqrt(candidate); i++) {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			if (candidate % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static OptionalLong findPrime() throws InterruptedException {
		var generator = ThreadLocalRandom.current();
		for (;;) {
			var candidate = generator.nextLong();
			if (isPrime(candidate)) {
				return OptionalLong.of(candidate);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var thread = Thread.ofPlatform().start(() -> {
			try {
				System.out.println("Found a random prime : " + findPrime().orElseThrow());
			} catch (InterruptedException e) {
				System.out.println("STOP");
			}
		});
		Thread.sleep(5_000);
		thread.interrupt();
	}
}
```



