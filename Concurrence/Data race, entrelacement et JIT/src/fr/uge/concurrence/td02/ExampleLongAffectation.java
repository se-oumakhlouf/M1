package fr.uge.concurrence.td02;

public class ExampleLongAffectation {
  long l = -1L;

  public static void main(String[] args) {
    var e = new ExampleLongAffectation();
    Thread.ofPlatform().start(() -> {
      System.out.println("l = " + e.l);
    });
    e.l = 0;
  }
}

// Question 2 :
// -> l = 0
// -> l = -1
// -> l = 2 ** (32 -1)
//				l'écriture du long est deschedule (sur machine 32 bits)
//				écriture des 32 premiers -> deschedule -< ...
// 4 possibilitées
