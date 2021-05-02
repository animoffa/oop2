package lr8;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
//https://mtuci.ru/ 2
public class Crawler {

    public static final int HTTPS_PORT = 443;
    public static final String HOOK_REF = "<a href=\"";
    public static final String BAD_REQUEST_LINE = "HTTP/1.1 400 Bad Request";
    public static final int NUM_OF_DEFAULT_THREADS = 4;
    public int depth;
    public int numOfThreads;

    public Crawler() {

    }


    public static void main(String[] args) {
        Crawler crawler = new Crawler();

        crawler.numOfThreads = Crawler.NUM_OF_DEFAULT_THREADS;

        URLDepthPair res = crawler.getFirstURLDepthPair(args);
        crawler.numOfThreads = CrawlerHelper.getNumOfThreads(args);

        URLPool pool = new URLPool(crawler.depth);
        pool.put(res);
        //возвращаем количество активных потоков
        int initialActive = Thread.activeCount();

        while (pool.getWaitThread() != crawler.numOfThreads) {
            if (Thread.activeCount() - initialActive < crawler.numOfThreads) {
                CrawlerTask crawlerTask = new CrawlerTask(pool);
                new Thread(crawlerTask).start();
            }
        }

        System.out.println("==========END================");
        System.out.println("=============RESULT:==========");

        LinkedList<URLDepthPair> list = pool.getWatchedList();
        System.out.println("==========WATCHED:===========");
        int count = 1;
        for (URLDepthPair page : list) {
            System.out.println(count + " |  " + page.toString());
            count += 1;
        }

        list = pool.getBlockedList();
        System.out.println("==============NOT WATCHED:============");
        count = 1;
        for (URLDepthPair page : list) {
            System.out.println(count + " |  " + page.toString());
            count += 1;
        }

        System.exit(0);
    }

    public static void createURlDepthPairObject(String url, int depth, LinkedList<URLDepthPair> listOfUrl) {
        URLDepthPair newURL = null;
        try {

            newURL = new URLDepthPair(url, depth);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        listOfUrl.addLast(newURL);
    }


    public URLDepthPair getFirstURLDepthPair(String[] args) {
        CrawlerHelper help = new CrawlerHelper();

        URLDepthPair urlDepth = help.getURLDepthPairFromArgs(args);
        if (urlDepth == null) {
            System.out.println("Args are empty or have exception. Now you need to enter URL and depth manually!\n");

            urlDepth = help.getURLDepthPairFromInput();
        }

        this.depth = urlDepth.getDepth();
        urlDepth.setDepth(0);


        return (urlDepth);

    }

//SSLSocketFactory для https
    public static LinkedList<URLDepthPair> parsePage(URLDepthPair element) {
        LinkedList<URLDepthPair> listOfUrl = new LinkedList<URLDepthPair>();
        SocketFactory socketFactory = null;

        try {
            socketFactory = SSLSocketFactory.getDefault();
            Socket socket = socketFactory.createSocket(element.getHostName(), HTTPS_PORT);

            try {
                socket.setSoTimeout(5000);
            } catch (SocketException exc) {
                System.err.println("SocketException: " + exc.getMessage());
                return null;
            }

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("GET " + element.getPagePath() + " HTTP/1.1");
            out.println("Host: " + element.getHostName());
            out.println("Connection: close");
            out.println("");


            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line = in.readLine();

            if (line.startsWith(BAD_REQUEST_LINE)) {
                System.out.println(line + "\n");
                return null;
            }

            int strCount = 0;

            while (line != null) {

                try {

                    line = in.readLine();
                    strCount += 1;

                    String url = CrawlerHelper.getURLFromHTMLTag(line);
                    if (url == null) continue;

                    if (url.startsWith("../")) {
                        String newUrl = CrawlerHelper.urlFromBackRef(element.getURL(), url);
                        Crawler.createURlDepthPairObject(newUrl, element.getDepth() + 1, listOfUrl);
                    } else if (url.startsWith("https://")) {
                        String newUrl = CrawlerHelper.cutTrashAfterFormat(url);
                        Crawler.createURlDepthPairObject(newUrl, element.getDepth() + 1, listOfUrl);
                    } else if (url.startsWith("http://")) {
                        String newUrl = CrawlerHelper.cutTrashAfterFormat(url);
                        Crawler.createURlDepthPairObject(newUrl, element.getDepth() + 1, listOfUrl);
                    } else {
                        String newUrl;
                        newUrl = CrawlerHelper.cutURLEndFormat(element.getURL()) + url;
                        Crawler.createURlDepthPairObject(newUrl, element.getDepth() + 1, listOfUrl);
                    }

                } catch (Exception e) {
                    break;
                }
            }

            if (strCount == 1) {
                return null;
            }


        } catch (UnknownHostException e) {
            System.out.println("Opps, UnknownHostException catched, so [" + element.getURL() + "] is not workable now!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listOfUrl;
    }


    public static void showResults(URLDepthPair element, LinkedList<URLDepthPair> listOfUrl) {
        System.out.println("===========CRAWLER RESULTS OF WORKING============");
        System.out.println("===========ORIGINAL PAGE===============: " + element.getURL());

        System.out.println("===========FOUNDED:====================");
        int count = 1;
        for (URLDepthPair pair : listOfUrl) {
            System.out.println(count + " |  " + pair.toString());
            count += 1;
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }

}