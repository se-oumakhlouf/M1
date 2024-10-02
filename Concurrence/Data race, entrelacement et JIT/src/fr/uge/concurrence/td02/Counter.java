package fr.uge.concurrence.td02;

public class Counter {
    private int value;

    public void addALot() {
        for (var i = 0; i < 100_000; i++) {
            this.value++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var counter = new Counter();
        var thread1 = Thread.ofPlatform().start(counter::addALot);
        var thread2 = Thread.ofPlatform().start(counter::addALot);
        thread1.join();
        thread2.join();
        System.out.println(counter.value);
    }
}

//Question 2 : 
//	
//	On s'attend a obtenir 200 000 mais on obtient souvent des valeurs
//		< 200 000 (en moyenne 140K-150K)
// 	Ceci est expliquer par le fait que dexu threads accèdent à un même zone mémoire
// 	Les threads peuvent lire la même valeur et n'incrementer que de 1 au lieu de 2


//Question 3 : 
//	Il est possible d'obtenir moins que 100 000
// 	Cela peut se produire lorsque :
//		Thread 1 lit valeur
//		Thread 2 lit valeur + incrémente et écrit la valeur
//		Thread 1 incrémente + écrit la valeur
//		On obtient donc la valeur écrit par T1 qui n'a pas été incrémenté par T2



//	. Le Thread 2 (T2) est schedulé, il lit 0 en mémoire, 
//			et il est deschedulé avant d'écrire 1

//	. A ce moment value vaut 0 en mémoire

//	. Le Thread 1 (T1) est schedulé, il fait 99 999 incrément,
//			et il est deschedulé avant de faire le dernier

//	. A ce moment value vaut 99 999 en mémoire

//	. T2 est schedulé et écrit 1 en mémoire, et il est deschedulé

// 	. T1 est schedulé, il lit 1 en mémoire et déschedulé avant d'écrire 2

// 	. A ce moment value vaut 1 en mémoire

// 	. T2 est schedulé et il tous ses incréments et il meurt

//	. A ce moment value vaut 1 en mémoire

//

