package fr.uge.concurrence.exo1;

public class MyClass {
  private String first;
  private String second;
  private final Object lock = new Object();

  public MyClass(String first, String second) {
     this.first = first;
     this.second = second;
  }

  public void set(String value1, String value2) {
     synchronized (lock) {
        first = value1;  
        second = value2; 
     }
  }

  public void setCheckFirst(String value1, String value2) {
     if (value1 != null) {
        synchronized (lock) {
           first = value1;
        }
     }
     synchronized (lock) {
        second = value2;
     }
  }

  @Override
  public String toString() {
     synchronized (lock) {
        return first + " + " + second;
     }
  }

  public static void main(String[] args) throws InterruptedException { 
     var quizz = new MyClass("mouse", "duck");

     var thread1 = Thread.ofPlatform().daemon().start(() -> {
        for (;;) {
           quizz.set("cat", "dog"); 
           System.out.println("1. " + quizz);
        }
     });

     var thread2 = Thread.ofPlatform().start(() -> {
        quizz.setCheckFirst("bird", "fish");
        System.out.println("2. " + quizz);
     });

     thread2.join();
     //thread1.join();

     quizz.set("mouse", "duck");        
     System.out.println("3. " + quizz); 
  }
}