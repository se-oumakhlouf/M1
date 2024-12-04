package fr.uge.concurrence;

public class StopThreadBug {
  private volatile boolean stop;

  public void runCounter() {
    var localCounter = 0;
    for(;;) {
      if (stop) {
        break;
      }
      localCounter++;
    }
    System.out.println(localCounter);
  }

  public void stop() {
    stop = true;
  }

  public static void main(String[] args) throws InterruptedException {
    var bogus = new StopThreadBug();
    var thread = Thread.ofPlatform().start(bogus::runCounter);
    Thread.sleep(100);
    bogus.stop();
    thread.join();
  }
}

/* Data-race sur le champ stop durant l'execution de thread et du main
 * 
 * Le programme ne peut jamais s'arreter si on commence par thread qui lance runCounter
 * et qui entre dans la boucle infinie avec une valeur de stop = false
 * 
 * Même si le thread main décleance bogus.stop() qui passe la valeur de stop à true
 * celle-ci ne sera pas changé dans le thread de runCounter car il n'y a pas de synchronization
 * ou de système de relecture
 */

/* 
 * On met le stop en volatile pour rendre la classe Thread-Safe
 * garantit que le if (stop) va lire en mémoire la valeur de stop
 */
