
public class HelloThreadBis {
	
	public static void println(String s) {
		for (var i = 0; i < s.length(); i++) {
			System.out.print(s.charAt(i));
		}
		System.out.print("\n");
	}

	public static void main(String[] args) {

		for (var i = 0; i < 4; i++) {
			var count = i;
			Thread.ofPlatform().start(() -> {
				for (int j = 1; j <= 5000; j++) {
					println("hello " + count + " " + j);
				}
			});
		}
	}

}

// Chaque thread essaie d'afficher une chaine de caractères sur la console.
// Les threads peuvent entrer en conflit / concurrence pour écrire dans la console en même temps

// le méthode System.out.println() est synchrone (synchronized), elle verrouille l'accès à la console (sortie standard) pour
// 		garantir un affichage complet de la chaîne de caractères
