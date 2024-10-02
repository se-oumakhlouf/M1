package fr.uge.concurrence.td02;

public class StopThreadBug implements Runnable {
  private boolean stop = false;

  public void stop() {
    stop = true;
  }

  @Override
  public void run() {
    while (!stop) {
    	System.out.println("Up");
    }
    System.out.print("Done");
  }

  public static void main(String[] args) throws InterruptedException {
    var stopThreadBug = new StopThreadBug();
    Thread.ofPlatform().start(stopThreadBug::run);
    Thread.sleep(1_000); // 
    System.out.println("Trying to tell the thread to stop");
    stopThreadBug.stop();
  }
}


// Question 1 : 
//	T1 affiche "Up"+ pendant 5 secondes
//	Puis le message "Trying ..."
// 	Puis "Done"


// Question 2 :
// 	On observe bien l'affichage des "Up" pendant 5 secondes
//	Puis les message "Trying to tell the thread to stop"
// 	Et ici on observe encore une fois un "Up"
// 	Puis "Done"


// Question 3 :
//	Le programme ne s'arrête plus
// 	Après le "Trying to tell the thread to stop", on s'attend à obtenir "Done"
//			mais ce n'est pas le cas alors que la valeur de stop est modifié en true
//
// 	Explication : Le JIT a probablement JIT modifié le code pour l'optimiser car 
//									la valeur de stop est utilisé uniquement en condition du while
//								Par exemple JIT a pu remplacer while(!stop) par while(false)


// Question 4 :
//		Le code avec affichage ne va pas toujours arrêter le thread, l'arrêt dépend
//			de l'algo du JIT, pour le moment JIT n'optimise pas lorsqu'il y les print
//			mais cela peut changer
