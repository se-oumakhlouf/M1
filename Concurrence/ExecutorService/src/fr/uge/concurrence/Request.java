package fr.uge.concurrence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Request {

    private final String site;
    private final String item;
    private final Object lock = new Object();
    private boolean calledOnce;

    private final static String[] ARRAY_ALL_SITES = { "amazon.fr", "amazon.uk", "darty.fr", "fnac.fr", "boulanger.fr",
            "cdiscount.fr", "tombeducamion.fr", "leboncoin.fr", "ebay.fr", "ebay.com", "laredoute.fr",
            "les3suisses.fr" };
    private final static Set<String> SET_ALL_SITES = Set.of(ARRAY_ALL_SITES);
    private final static List<String> ALL_SITES = List.of(ARRAY_ALL_SITES);

    public static List<String> getAllSites() {
        return ALL_SITES;
    }

    public Request(String site, String item) {
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

    /**
     * Performs the request the price for the item on the site waiting at most
     * timeoutMilli milliseconds. The returned Answered is not guaranteed to be
     * successful.
     *
     * This method can only be called once. All further calls will throw an
     * IllegalStateException
     *
     *
     * @param timeoutMilli
     * @throws InterruptedException
     */
    public Optional<Answer> request(int timeoutMilli) throws InterruptedException {
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
        if ((hash1 % 1_000 < 400) || ((hash1 % 1_000) * 2 > timeoutMilli)) { // simulating timeout
            Thread.sleep(timeoutMilli);
            System.out.println("DEBUG : Request " + toString() + " has timeout");
            return Optional.empty();
        }
        Thread.sleep((hash1 % 1_000) * 2);
        if ((hash1 % 1_000 < 500)) {
            System.out.println("DEBUG : " + item + " is not available on " + site);
            return Optional.empty();
        }
        int price = (int) (hash2 % 1_000) + 1;
        System.out.println("DEBUG : " + item + " costs " + price + " on " + site);
        return Optional.of(new Answer(site, item, price));
    }

    public static void main(String[] args) throws InterruptedException {
        Request request = new Request("amazon.fr", "pikachu");
        Optional<Answer> answer = request.request(5_000);
        if (answer.isPresent()) {
            System.out.println("The price is " + answer.orElseThrow().price());
        } else {
            System.out.println("The price could not be retrieved from the site");
        }
    }
}