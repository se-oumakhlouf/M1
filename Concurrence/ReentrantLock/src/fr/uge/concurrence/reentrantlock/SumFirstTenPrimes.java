package fr.uge.concurrence.reentrantlock;

public class SumFirstTenPrimes {
	
	public static boolean isPrime(long l) {
    if (l <= 1) {
        return false;
    }
    for (long i = 2L; i <= l / 2; i++) {
        if (l % i == 0) {
            return false;
        }
    }
    return true;
}


	public static void main(String[] args) throws InterruptedException {
		var sum = new MyThreadSafeClass();
		for (int i = 0; i < 5; i++) {
			Thread.ofPlatform().start(() -> {
				while(true) {
					long nb = sum.submit();
					if (isPrime(nb)) {
						try {
							sum.addValue(nb);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			});
		}
		long finalSum = sum.returnSum();
		System.out.println("La somme finale : " + finalSum);
		
	}

}
