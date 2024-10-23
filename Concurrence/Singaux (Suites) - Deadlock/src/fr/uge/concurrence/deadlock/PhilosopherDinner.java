package fr.uge.concurrence.deadlock;

import java.util.Arrays;
import java.util.stream.IntStream;

/* Quel est le problème du code ci-dessus ? Dans quelle(s) condition(s) se produit-il ?
 * 
 * Il y a des forts risques d'obtenir un deadlock dans ce code
 * 
 * Scénario : 
 * 	Philosophe 0 acquiert fork 0 et commencer à essayer d'acquérir fork 1
 *  Philosophe 1 acquiert fork 1 et commencer à essayer d'acquérir fork 2
 *  Philosophe 2 acquiert fork 2 et commencer à essayer d'acquérir fork 3
 *  Philosophe 3 acquiert fork 3 et commencer à essayer d'acquérir fork 4
 *  Philosophe 4 acquiert fork 4 et commencer à essayer d'acquérir fork 0
 *  		-- deadlock --
 */

public class PhilosopherDinner {
  private final Object[] forks;

  private PhilosopherDinner(Object[] forks) {
    this.forks = forks;
  }

  public static PhilosopherDinner newPhilosopherDinner(int forkCount) {
    Object[] forks = new Object[forkCount];
    Arrays.setAll(forks, i -> new Object());
    return new PhilosopherDinner(forks);
  }

  public void eat(int index) {
    var fork1 = forks[index];
    var fork2 = forks[(index + 1) % forks.length];
    synchronized (fork1) {
      synchronized (fork2) {
        System.out.println("philosopher " + index + " eat");
      }
    }
  }

  public static void main(String[] args) {
    var dinner = newPhilosopherDinner(5); // Array [5] d'objet représentant des fourchettes
    IntStream.range(0, 5).forEach(i -> {	// 5 Threads -> boucle infinie
      new Thread(() -> {									// eat -> prend sa fourchette + fourcette de droite
        for (;;) {
          dinner.eat(i);
        }
      }).start();
    });
  }
}