package lr8;

import lr8.Crawler;

import java.util.*;
//Чтобы выполнить веб-сканирование в нескольких потоках, необходимо

public class CrawlerTask implements Runnable {

    private URLDepthPair element;

    private URLPool myPool;

    public CrawlerTask(URLPool pool) {
        this.myPool = pool;
    }

    public void run() {
        element = myPool.get();
        LinkedList<URLDepthPair> linksList;
        linksList = Crawler.parsePage(element);

        if (linksList != null && !linksList.isEmpty()) {
            Crawler.showResults(element, linksList);
            for (URLDepthPair pair : linksList) {
                myPool.put(pair);
            }
        }
    }
}