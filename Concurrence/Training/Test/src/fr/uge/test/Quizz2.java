package fr.uge.test;

import java.util.ArrayList;

public class Quizz2 {
  private ArrayList<Integer> list = new ArrayList<>();
  private String flag = "before";

  public void go() {
      Thread.ofPlatform().start(() -> {
          list.add(1);
          list.add(2);
          flag = "after";
          var a = list.get(0);
      });

      while (true) {
          if (flag.equals("after")) {
              break;
          }
      }
      System.out.println(flag);
      System.out.println(list);
  }

  public static void main(String[] args) {
      new Quizz2().go();
  }
}
