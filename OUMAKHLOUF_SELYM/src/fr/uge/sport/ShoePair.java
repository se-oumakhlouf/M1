package fr.uge.sport;

import java.util.Objects;

public record ShoePair(String brand, String color, Integer size, Integer price) implements Sportswear {

  public ShoePair {
	Objects.requireNonNull(brand);
	Objects.requireNonNull(color);
	if (size < 37 || size > 44) {
	  throw new IllegalArgumentException("37 <= size <= 44");
	}
	if (price < 0) {
	  throw new IllegalArgumentException("price >= 0");
	}
  }
  
  public String toString() {
	return "ShoePair[brand=" + brand + ", color=" + color
		+ ", size=" + size + ", price=" + price + "]";
  }
  
  public Integer price() {
	return this.price;
  }
  
  public boolean sale() {
	return this.size >= 43;
  }
  
}
