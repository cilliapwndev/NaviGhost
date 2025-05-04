package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dirbuster {

    // Default User-Agent used when none is provided
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";

    // Original 4-parameter method for backward compatibility
    public static void dirbust(String baseUrl, String wordlistPath, boolean hide400, boolean hide500) {
        dirbust(baseUrl, wordlistPath, hide400, hide500, DEFAULT_USER_AGENT);
    }

    // New 5-parameter method with User-Agent support
    public static void dirbust(String baseUrl, String wordlistPath, boolean hide400, boolean hide500, String userAgent) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = Math.max(1, (int) (availableProcessors * 0.6));

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        try (BufferedReader br = new BufferedReader(new FileReader(wordlistPath))) {
            String dir;
            while ((dir = br.readLine()) != null) {
                String fullUrl = baseUrl + "/" + dir;
                executor.submit(() -> checkUrl(fullUrl, hide400, hide500, userAgent));
            }
        } catch (IOException e) {
            System.err.println("Error reading wordlist: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static void checkUrl(String url, boolean hide400, boolean hide500, String userAgent) {
        int statusCode = getHttpStatusCode(url, userAgent);

        if (statusCode == 200) {
            System.out.println("Found: " + url);
        } else if (statusCode == 403) {
            System.out.println("Forbidden: " + url);
        } else if (statusCode >= 301 && statusCode <= 310) {
            String redirectLocation = getRedirectLocation(url, userAgent);
            if (redirectLocation != null) {
                System.out.println("Redirect: " + url + " (Status Code: " + statusCode + ") -> " + redirectLocation);
            } else {
                System.out.println("Redirect (no location): " + url + " (Status Code: " + statusCode + ")");
            }
        } else if (statusCode >= 400 && statusCode < 500) {
            if (!hide400) {
                System.out.println("Not Found: " + url + " (Status Code: " + statusCode + ")");
            }
        } else if (statusCode >= 500 && statusCode < 600) {
            if (!hide500) {
                System.out.println("Server Error: " + url + " (Status Code: " + statusCode + ")");
            }
        } else {
            System.out.println("Other Status: " + url + " (Status Code: " + statusCode + ")");
        }
    }

    private static int getHttpStatusCode(String url, String userAgent) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            if (userAgent != null) {
                connection.setRequestProperty("User-Agent", userAgent);
            }
            connection.connect();
            return connection.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
    }

    private static String getRedirectLocation(String url, String userAgent) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            if (userAgent != null) {
                connection.setRequestProperty("User-Agent", userAgent);
            }
            connection.setInstanceFollowRedirects(false); // Prevent automatic redirect following
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode >= 301 && statusCode <= 310) {
                return connection.getHeaderField("Location");
            }
        } catch (Exception e) {
            // Handle exceptions if needed
        }
        return null;
    }
}