// Question 3 : L'affichage est aléatoire, les valeurs ne se suivent pas tout le temps
// 							Ceci est normal car le scheduler attribut un temps d'xécution aléatoire à chaque thread

public class HelloThread {

	public static void main(String[] args) {

		for (var i = 0; i < 4; i++) {
			var count = i;
			Thread.ofPlatform().start(() -> {
				for (int j = 1; j <= 5000; j++) {
					System.out.println("hello " + count + " " + j);
				}
			});
		}
	}

}
