package fr.uge.concurrence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RequestWithCancel {

    private final String site;
    private final String item;
    private final Object lock = new Object();
    private boolean calledOnce;
    private boolean cancelled;

    private final static String[] ARRAY_ALL_SITES = { "amazon.fr", "amazon.uk", "darty.fr", "fnac.fr", "boulanger.fr",
            "cdiscount.fr", "tombeducamion.fr", "leboncoin.fr", "ebay.fr", "ebay.com", "laredoute.fr",
            "les3suisses.fr" };
    private final static Set<String> SET_ALL_SITES = Set.of(ARRAY_ALL_SITES);
    private final static List<String> ALL_SITES = List.of(ARRAY_ALL_SITES);

    public static List<String> getAllSites() {
        return ALL_SITES;
    }

    public RequestWithCancel(String site, String item) {
        if (!SET_ALL_SITES.contains(site)) {
            throw new IllegalStateException("invalid site " + site);
        }
        this.site = site;
        this.item = item;
    }

    @Override
    public String toString() {
        return item + "@" + site;
    }

    public void cancel() {
        synchronized (lock) {
            cancelled = true;
            lock.notify();
        }
    }

    /**
     * Performs the request the price for the item on the site. The returned answer
     * might not be successful. This method is blocking and might not terminate. It
     * is the user's responsibility to call the method cancel() to stop the request.
     * <p>
     * This method can only be called once. All further calls will throw an
     * IllegalStateException
     *
     * @throws InterruptedException
     */
    public Optional<Answer> request() throws InterruptedException {
        synchronized (lock) {
            if (calledOnce) {
                throw new IllegalStateException("already called once");
            }
            calledOnce = true;
        }
        System.out.println("DEBUG : starting request for " + item + " on " + site);
        if (item.equals("pokeball")) {
            System.out.println("DEBUG : " + item + " is not available on " + site);
            return Optional.empty();
        }
        long hash1 = (site + "|" + item).hashCode() & 0x7FFF_FFFF;
        long hash2 = (item + "|" + site).hashCode() & 0x7FFF_FFFF;
        if (hash1 % 1000 < 400) { // simulating timeout
            System.out.println("DEBUG : Request " + toString() + " is not terminating");
            synchronized (lock) {
                while (!cancelled) {
                    lock.wait();
                }
            }
            System.out.println("DEBUG : Request " + toString() + " is cancelled");
            return Optional.empty();
        }
        long time = System.currentTimeMillis();
        long endTime = time + (hash1 % 1000) * 2;
        synchronized (lock) {
            while (!cancelled && time < endTime) {
                lock.wait(endTime - time);
                time = System.currentTimeMillis();
            }
            if (cancelled) {
                System.out.println("DEBUG : Request " + toString() + " is cancelled");
                return Optional.empty();
            }
        }
        if ((hash1 % 1000 < 500)) {
            System.out.println("DEBUG : " + item + " is not available on " + site);
            return Optional.empty();
        }
        int price = (int) (hash2 % 1000) + 1;
        System.out.println("DEBUG : " + item + " costs " + price + " on " + site);
        return Optional.of(new Answer(site, item, price));
    }

    public static void main(String[] args) throws InterruptedException {
        RequestWithCancel request = new RequestWithCancel("amazon.fr", "pikachu");

        new Thread(() -> {
            try {
                System.out.println(request.request());
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }).start();
        Thread.sleep(5_000);
        request.cancel();
    }
}