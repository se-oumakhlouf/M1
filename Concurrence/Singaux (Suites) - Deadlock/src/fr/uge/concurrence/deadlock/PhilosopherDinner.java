package fr.uge.concurrence.deadlock;

import java.util.Arrays;
import java.util.stream.IntStream;

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
    var dinner = newPhilosopherDinner(5);
    IntStream.range(0, 5).forEach(i -> {
      new Thread(() -> {
        for (;;) {
          dinner.eat(i);
        }
      }).start();
    });
  }
}