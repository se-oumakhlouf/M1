package fr.uge.concurrence.td02;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {
	
	public static void main(String[] args) throws InterruptedException {
	  var nbThreads = 4;
	  var threads = new Thread[nbThreads];
	  var list = new ArrayList<Integer>(5000 * nbThreads);

	  IntStream.range(0, nbThreads).forEach(j -> {
	    Runnable runnable = () -> {
	      for (var i = 0; i < 5000; i++) {
	      	list.add(i);
	      }
	    };

	    threads[j] = Thread.ofPlatform().start(runnable);
	  });

	  for (var thread : threads) {
	    thread.join(); 
	  }

	  System.out.println("le programme est fini");
	  System.out.println("List size : " + list.size());
	}
}

// Question 2 :
//		1 -> List size : 9466
//		2 -> List size : 8852
// 		3 -> List size : 10490
//		4 -> List size : 7025

// Question 3 :
//		Deux threads vont add en même temps, et vont finir par écrire au même endroit
//			ce qui donne une seule incrémentation
// 		T1 est schedulé et read l'index (i), T1 est deschedulé
//		T2 est schedulé et fait la lecture de l'index qui est le même que pour T1
// 		T2 écrit à l'index i, T2 est deschedulé
//		T1 est schedulé, T1 écrit par dessus l'écriture de T2 à l'index i

// Question 4 :


// Question 4 :
//

