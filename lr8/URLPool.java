package lr8;

import lr8.URLDepthPair;

import java.util.LinkedList;
//хранить список
//всех URL-адресов для поиска, а также относительный "уровень" каждого из
//этих URL-адресов
public class URLPool {

    private LinkedList<URLDepthPair> watchedList;

    private LinkedList<URLDepthPair> notWatchedList;

    private LinkedList<URLDepthPair> blockedList;

    public int waitingThreads;

    private int depth;


    public URLPool(int depth) {
        waitingThreads = 0;
        watchedList = new LinkedList<URLDepthPair>();
        notWatchedList = new LinkedList<URLDepthPair>();
        blockedList = new LinkedList<URLDepthPair>();
        this.depth = depth;
    }
    //  синхронизируем
    public synchronized int getWaitThread() {
        return waitingThreads;
    }

    public synchronized boolean put(URLDepthPair depthPair) {

        boolean ad = false;

        if (depthPair.getDepth() < this.depth) {
            notWatchedList.addLast(depthPair);
            ad = true;

            if (waitingThreads > 0) waitingThreads--;
//продолжает работу потока, у которого ранее был вызван метод wait()
            this.notify();
        } else {
            blockedList.add(depthPair);
        }

        return ad;
    }
    //Если глубина URL-адреса меньше максимальной, добавьте
//пару в очередь ожидания. Иначе добавьте URL-адрес в список обработанных,
//не сканируя страницу.
    public synchronized URLDepthPair get() {

        URLDepthPair myDepthPair = null;

        if (notWatchedList.size() == 0) {
            waitingThreads++;
            try {
                //   освобождает монитор и переводит вызывающий поток в состояние ожидания до тех пор, пока другой поток не вызовет метод notify()
                this.wait();
            } catch (InterruptedException e) {
                System.err.println("MalformedURLException: " + e.getMessage());
                return null;
            }
        }

        myDepthPair = notWatchedList.removeFirst();
        watchedList.add(myDepthPair);

        return myDepthPair;
    }


    public LinkedList<URLDepthPair> getWatchedList() {
        return this.watchedList;
    }

    public LinkedList<URLDepthPair> getBlockedList() {
        return this.blockedList;
    }

}