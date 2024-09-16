package fr.uge.sport;

public sealed interface Sportswear permits ShoePair, Clothing {
  public Integer price();
  public boolean sale();
}
