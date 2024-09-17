package fr.uge.sport2;

public sealed interface Sportswear permits ShoePair, Clothing {
  public Integer price();
  public boolean sale();
}
