package fr.uge.sport2;

public class Main {
  
  public static void main(String[] args) {
	var polo = new Clothing(ClothingType.POLO, "Colmar", 3, 40);
	var polo2 = new Clothing(ClothingType.POLO, "Colmar", 40);
	var shirt1 = new Clothing(ClothingType.T_SHIRT, "Burton", 4, 50);
	var shirt2 = new Clothing(ClothingType.T_SHIRT,"Burton", 4, 50);
	var shoePair1 = new ShoePair("Nike", "black", 38, 300);
	var shoePair2 = new ShoePair("Nike", "white", 44, 300);
	
	System.out.println("Polo2 : " + polo2 + "\n");
	
	var shop1 = new SportsShop("Italie2");
	shop1.add(polo);
	shop1.add(shirt1);
	shop1.add(shoePair1);
	shop1.add(shoePair2);
	shop1.add(shoePair2);

	
	var shop2 = new SportsShop("Jaude");
	shop2.add(shirt2);
	shop2.add(polo);
	shop2.add(shoePair1);
	shop2.add(shoePair1);
	shop2.add(shoePair2);
	
	
	System.out.println("Shop1 :");
	System.out.println(shop1);
	
	System.out.println("\nShop2 :");
	System.out.println(shop2);
	
	System.out.println("\nQuestion 14 : (shop1 - totalPrice)");
	System.out.println(shop1.totalPrice());
	
	System.out.println("\nQuestion 7 : (shop1 - onSale)");
	System.out.println(shop1.onSale());
	
	System.out.println("\nQuestion 8 : (shop2 - shoesBySize)");
	System.out.println(shop2.shoesBySize());
	
	System.out.println("\nQuestion 9 : (shop2 - selectedItems)");
	System.out.println(shop2.selectedItems(SportsShop::priceTooHigh));
	
	System.out.println("\nQuestion 10 : (shop2 - occurences)");
	System.out.println(shop2.occurrences());
	
//	System.out.println("\nQuestion 11 : (sameItems)");
//	System.out.println(SportsShop.sameItems(shop1, shop2));
	
  }
}
