import java.util.*;

public class CrawlerTask implements Runnable {

    private URLDepthPair element;

    private URLPool myPool;

    public CrawlerTask(URLPool pool) {
        this.myPool = pool;
    }

    public void run() {

        element = myPool.get();

        int myDepth = element.getDepth();

        LinkedList<URLDepthPair> linksList = new LinkedList<URLDepthPair>();
        linksList = Crawler.parsePage(element);

        Crawler.showResults(element, linksList);

        for (URLDepthPair pair: linksList) {
            myPool.put(pair);
        }

    }
}