import java.lang.Exception;
import java.util.*;
import java.net.MalformedURLException;
import java.net.*;
import java.io.*;

public class CrawlerHelper {


    public static String[] formats = {".html", ".pdf", ".java", ".xml", "txt", ".css", ".doc", ".c"};

    public URLDepthPair getURLDepthPairFromArgs(String[] args) {
        if (args.length > 2) System.out.println("Warning more than 2 parameters from command line!\n");
        if (args.length < 2) {
            System.out.println("Warning less than 2 parameters from command line!\n");
            return null;
        }


        int depth;
        try {
            depth = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Error depth parameter!");
            return null;
        }

        URLDepthPair urlDepth;

        try {
            urlDepth = new URLDepthPair(args[0], depth);
        }
        catch (MalformedURLException ex) {
            System.out.println(ex.getMessage() + "\n");
            return null;
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
            return null;
        }

        return urlDepth;
    }



    public URLDepthPair getURLDepthPairFromInput() {

        String url;
        int depth;


        String[] args;

        URLDepthPair urlDepth = null;

        Scanner in = new Scanner(System.in);

        while (urlDepth == null) {


            System.out.println("Enter URL and depth of parsing (in a line with a space between):");
            String input = in.nextLine();

            args = input.split(" ", 2);


            urlDepth = this.getURLDepthPairFromArgs(args);
            if (urlDepth == null) System.out.println("Try again!\n");
        }
        return urlDepth;
    }

    public static int getNumOfThreads(String[] args) {
        if (args == null || args.length < 3) {

            Scanner in = new Scanner(System.in);

            boolean nice = false;
            int input = Crawler.NUM_OF_DEFAULT_THREADS;

            while (!nice) {
                try{
                    System.out.println("\nEnter amount of threads which you want to do parsing:");
                    input = in.nextInt();
                    if (input > 0 && input < 100) nice = true;
                }
                catch (Exception e) {
                    in.nextLine();
                }
            }
            System.out.println("");
            return input;
        }
        else {

            int threads;
            try {
                threads = Integer.parseInt(args[2]);
                if (threads > 0) return threads;
            } catch (Exception e) {
                System.out.println("Error threads-parameter in arguments. Using default amount!");
            }
        }
        return Crawler.NUM_OF_DEFAULT_THREADS;
    }

    public static String[] getInfoAboutUrl(URL url, boolean needToOut) {

        String[] info = new String[10];
        info[0] = url.toString();
        info[1] = url.getHost();
        try {
            info[2] = url.getContent().toString();
        } catch (IOException e) {
            System.out.println("Cannot get content-type, this may be https protocol page");
            info[2] = "";
        }
        info[3] = url.getProtocol();
        info[4] = url.getPath();
        info[5] = url.getUserInfo();
        info[6] = url.getFile();
        info[7] = url.getRef();
        try {
            info[8] = url.toURI().toString();
        } catch (URISyntaxException e) {
            System.out.println("Cannot get URI, this may be https protocol page");
            info[8] = "";
        }
        info[9] = String.valueOf(url.getPort());

        if (needToOut) {
            System.out.println("\n---Info about this url---");
            System.out.println("Full url: " + info[0]);
            System.out.println("Host name of url: " + info[1]);
            System.out.println("Content of url: " + info[2]);
            System.out.println("Protocol of url: " + info[3]);
            System.out.println("Path of url: " + info[4]);
            System.out.println("UserInfo of url: " + info[5]);
            System.out.println("Files on url: " + info[6]);
            System.out.println("Ref of url: " + info[7]);
            System.out.println("URI of url: " + info[8]);
            System.out.println("Port of url: " + info[9]);
            System.out.println("---------------------------\n");
        }
        return info;
    }

    public static String[] getInfoAboutUrl(String urlStr, boolean needToOut) {
        URL url = null;
        try {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }

        String[] info = getInfoAboutUrl(url, needToOut);
        return info;

    }

    public static String getURLFromHTMLTag(String line) {
        if (line.indexOf(Crawler.HOOK_REF) == -1) return null;

        int indexStart = line.indexOf(Crawler.HOOK_REF) + Crawler.HOOK_REF.length();
        int indexEnd = line.indexOf("\"", indexStart);

        if (indexEnd == -1) return null;

        return line.substring(indexStart, indexEnd);
    }
    public static String cutURLEndFormat(String url) {

        url = CrawlerHelper.cutTrashAfterFormat(url);


        for (String format : formats) {
            if (url.endsWith(format)) {
                int lastCatalog = url.lastIndexOf("/");
                return url.substring(0, lastCatalog + 1);
            }
        }
        return url;
    }

    public static String urlFromBackRef(String url, String backRef) {
        int count = 2;
        int index = url.length();


        char[] urlSequnce = url.toCharArray();
        while (count > 0 && index > 0) {
            index -= 1;
            if (urlSequnce[index] == '/') count -= 1;
        }

        if (index == 0) return null;

        String cutURL = url.substring(0, index + 1);
        String cutBackRef = backRef.substring(3, backRef.length());

        return (cutURL + cutBackRef);
    }

    public static String cutTrashAfterFormat(String url) {
        int index = url.lastIndexOf("#");
        if (index == -1) return url;
        return url.substring(0, index);

    }
}