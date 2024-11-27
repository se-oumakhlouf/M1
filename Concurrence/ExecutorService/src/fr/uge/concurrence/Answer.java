package fr.uge.concurrence;

import java.util.Objects;

public record Answer(String site, String item, int price) {
  public Answer {
    Objects.requireNonNull(site);
    Objects.requireNonNull(item);
    if (price <= 0) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String toString() {
    return item + "@" + site + " : " + price;
  }
}