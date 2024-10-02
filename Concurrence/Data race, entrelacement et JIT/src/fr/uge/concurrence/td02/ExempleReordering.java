package fr.uge.concurrence.td02;

public class ExempleReordering {
  int a = 0;
  int b = 0;

  public static void main(String[] args) {
    var e = new ExempleReordering();
    Thread.ofPlatform().start(() -> {
      System.out.println("a = " + e.a + "  b = " + e.b);
    });
    e.a = 1;
    e.b = 2;
  }
}

// prédiction : -> a = 0  b = 0

// Question 1 :
//	-> a = 1  b = 2
//	-> a = 0  b = 0
//	-> a = 1  b = 0
//	-> a = 0  b = 2 (T1 schedule -> commence affichage, lit e.a = 0 -> T1 deschedule | TMain schedule -> met e.b = 2 | T1 schedule -> imprime b = 2)