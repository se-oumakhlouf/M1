package fr.uge.sport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SportsShop {
  
  private final ArrayList<Sportswear> articles;
  private final String name;
  
  public SportsShop(String name) {
	this.articles = new ArrayList<Sportswear>();
	this.name = Objects.requireNonNull(name);
  }
  
  public void add(Sportswear article) {
	Objects.requireNonNull(article);
	articles.add(article);
  }
  
  public long totalPrice() {
	return articles.stream()
		.mapToInt(Sportswear::price)
		.sum();
  }
  
  public List<Sportswear> onSale() {
	var onSaleList = articles.stream()
		.filter(article -> article.sale())
		.toList();
	return Collections.unmodifiableList(onSaleList);
  }
  
    public Map<Integer, List<ShoePair>> shoesBySize(){
	return articles.stream()
		.flatMap(a -> switch(a) {
    	case ShoePair shoePair -> Stream.of(shoePair);
    	default -> Stream.empty(); // Clothing
		})
		.collect(Collectors.groupingBy(ShoePair::size));
  }
    
  static boolean priceTooHigh(Sportswear sportswear) {
  	return sportswear.price() >= 300;
  }
  
  public List<Sportswear> selectedItems(Predicate<Sportswear> predicate) {
  	return List.copyOf( 
  			articles.stream()
	  			.filter(predicate)
	  			.toList()
  			);
  }
  
  public Map<Sportswear, Long> occurrences() {
  	return articles.stream()
  			.collect(Collectors.groupingBy(
  					article -> article, Collectors.counting()
  					)
				);
  }
  
  public static boolean sameItems(SportsShop shop1, SportsShop shop2) {
  	Set<Sportswear> articlesShop1 = new HashSet<>(shop1.articles);
  	Set<Sportswear> articlesShop2 = new HashSet<>(shop2.articles);
  	return articlesShop1.equals(articlesShop2);
  }
  
  @Override
  
  public String toString() {
	return name + "\n" + articles.stream()
		.map(article -> article.toString())
		.collect(Collectors.joining("\n"));
  }
}
