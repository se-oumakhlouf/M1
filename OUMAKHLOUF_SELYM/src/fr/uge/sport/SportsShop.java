package fr.uge.sport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
  
  // à faire
  public Map<Integer, List<ShoePair>> shoesBySize(){
	return null;
	
  }
  
  @Override
  
  public String toString() {
	return name + "\n" + articles.stream()
		.map(article -> article.toString())
		.collect(Collectors.joining("\n"));
  }
}
