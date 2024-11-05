package fr.uge.concurrence.reentrantlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Sync<V> {

	private final Object lock = new Object();
//	private final ReentrantLock lock = new ReentrantLock();
//	private final Condition condition = lock.newCondition();
	private boolean in;

//	public boolean inSafe() throws InterruptedException {
//		lock.lock();
//		try {
//			return in;
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	public V safe(Supplier<? extends V> supplier) throws InterruptedException {
//		lock.lock();
//		try {
//			while (in) {
//				condition.await();
//			}
//			in = true;
//			return supplier.get();
//		} finally {
//			in = false;
//			condition.signal();
//			lock.unlock();
//		}
//	}

	public boolean inSafe() throws InterruptedException {
		synchronized (lock) {
			return in;
		}
	}

	public V safe(Supplier<? extends V> supplier) throws InterruptedException {
		synchronized (lock) {
			while (in) {
				lock.wait();
			}
			in = true;
		}
		var res = supplier.get();
		
		synchronized (lock) {
			in = false;
			lock.notifyAll();
			return res;
		}
	}
	
}
