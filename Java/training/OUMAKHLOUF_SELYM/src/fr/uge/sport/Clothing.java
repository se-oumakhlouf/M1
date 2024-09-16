package fr.uge.sport;

import java.util.Objects;

public record Clothing(ClothingType type, String brand, Integer size, Integer price) implements Sportswear {

  public Clothing {
    Objects.requireNonNull(brand);
    Objects.requireNonNull(type);
    if (size > 5 || size < 1) {
      throw new IllegalArgumentException("1 <= size <= 5");
    }
    if (price < 0) {
      throw new IllegalArgumentException("price >= 0");
    }
  }
  
  public Clothing(ClothingType type, String brand, Integer price) {
	this(type, brand, 1, price);
  }
  
  
  public String toString() {
	return "Clothing[type=" + type + ", brand=" + brand +
		", size=" + size + ", price=" + price + "]"; 
  }
  
  public Integer price() {
	return this.price;
  }
  
  public boolean sale() {
	return this.size >= 3;
  }
  
}
