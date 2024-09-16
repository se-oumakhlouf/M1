package fr.uge.sport;

public class Main {
  
  public static void main(String[] args) {
	var polo = new Clothing(ClothingType.POLO, "Colmar", 3, 40);
	var polo2 = new Clothing(ClothingType.POLO, "Colmar", 40);
	var shirt1 = new Clothing(ClothingType.T_SHIRT, "Burton", 4, 50);
	var shirt2 = new Clothing(ClothingType.T_SHIRT,"Burton", 4, 50);
	var shoePair1 = new ShoePair("Nike", "black", 38, 300);
	var shoePair2 = new ShoePair("Nike", "white", 44, 300);
	var shop1 = new SportsShop("Italie2");
	shop1.add(polo);
	shop1.add(shirt1);
	shop1.add(shoePair1);
	shop1.add(shoePair2);
	var shop2 = new SportsShop("Jaude");
	shop2.add(shirt2);
	shop2.add(polo);
	shop2.add(shoePair1);
	shop2.add(shoePair1);
	shop2.add(shoePair2);
	System.out.println(shop1);
	System.out.println(shop2);
	System.out.println(shop1.totalPrice());
	System.out.println(shop1.onSale());

  }
}
