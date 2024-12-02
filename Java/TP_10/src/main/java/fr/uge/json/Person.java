package fr.uge.json;

import static java.util.Objects.requireNonNull;

@JSONProperty(value = "Person")
public record Person(String firstName, String lastName) {
  public Person {
    requireNonNull(firstName);
    requireNonNull(lastName);
  }
}
    