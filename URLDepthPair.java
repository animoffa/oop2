import java.net.MalformedURLException;
import java.net.URL;
// не меняли
public class URLDepthPair {

    private String url;
    private int depth;
    public static final String URL_PREFIX = "http://";
    public static final String HTTP_URL_PREFIX = "https://";
    public static final int MAX_DEPTH_VALUE = 100;

    public URLDepthPair(String url, int depth) throws MalformedURLException {

        if (depth < 0 || depth > MAX_DEPTH_VALUE) {
            throw new IllegalArgumentException("Error limits of depth");
        }

        if (!URLDepthPair.isHttpPrefixInURL(url)) {
            MalformedURLException ex = new MalformedURLException("Error of url prefix");
            throw ex;
        }

        this.url = url;
        this.depth = depth;
    }

    public static boolean isHttpPrefixInURL(String url) {
        if (!(url.startsWith(URL_PREFIX) || url.startsWith(HTTP_URL_PREFIX))) return false;
        return true;
    }

    public String toString() {
        return new String("[ " + this.url + ", " + this.depth + " ]");
    }

    public String getHostName() {
        try {
            URL url = new URL(this.url);
            return url.getHost();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    public String getPagePath() {
        try {
            URL url = new URL(this.url);
            return url.getPath();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    public String getURL() {
        return this.url;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        if (depth < 0 || depth > MAX_DEPTH_VALUE) {
            throw new IllegalArgumentException("Error limits of depth");
        }
        this.depth = depth;
    }


}