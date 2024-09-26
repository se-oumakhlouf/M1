package fr.uge.sport2;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SportsShop {

	private final HashMap<Sportswear, Long> articles;
	private final String name;

	public SportsShop(String name) {
		this.articles = new HashMap<Sportswear, Long>();
		this.name = Objects.requireNonNull(name);
	}

	public void add(Sportswear article) {
		Objects.requireNonNull(article);
		articles.put(article, articles.getOrDefault(article, (long) 0) + 1);
	}

	public long totalPrice() {
		return articles.entrySet().stream().mapToLong(entry -> entry.getKey().price() * entry.getValue()).sum();
	}

	public List<Sportswear> onSale() {
		var onSaleList = articles.entrySet().stream().filter(article -> article.getKey().sale())
				.flatMap(entry -> Collections.nCopies(entry.getValue().intValue(), entry.getKey()).stream()).toList();
		return Collections.unmodifiableList(onSaleList);
	}

	public Map<Integer, List<ShoePair>> shoesBySize() {
		return articles.keySet().stream().flatMap(a -> switch (a) {
		case ShoePair shoePair -> Stream.of(shoePair);
		default -> Stream.empty(); // Clothing
		}).collect(Collectors.groupingBy(ShoePair::size));
	}

	static boolean priceTooHigh(Sportswear sportswear) {
		return sportswear.price() >= 300;
	}

	public List<Sportswear> selectedItems(Predicate<Sportswear> predicate) {
		return List.copyOf(articles.entrySet().stream().filter(article -> predicate.test(article.getKey()))
				.flatMap(article -> Collections.nCopies(article.getValue().intValue(), article.getKey()).stream()).toList());
	}

// La complexité est en O(1), c'est uniquement le renvoie de la map
	public Map<Sportswear, Long> occurrences() {
		return articles;
	}

//	Complexité : O(1)
	public long get(Sportswear article) {
		return articles.getOrDefault(article, 0L);
	}

	public static boolean sameItems(SportsShop shop1, SportsShop shop2) {
		return shop1.articles.keySet().equals(shop2.articles.keySet());
	}

	@Override

	public String toString() {
		return name + "\n" + articles.keySet().stream().map(article -> article + ": " + articles.get(article))
				.collect(Collectors.joining("\n"));
	}
}
