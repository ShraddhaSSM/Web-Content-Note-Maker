import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebPageDownloadManager {
    public static void main(String[] args) {
        List<String> urls = new ArrayList<>();
        urls.add("https://images.app.goo.gl/aQMuAfc9NznvqYQZ9");
        urls.add("https://images.app.goo.gl/f7KSRp44YajbeATB8");
        // urls.add("");

        int numThreads = 3; // Number of threads for concurrent downloads

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long startTime = System.currentTimeMillis();

        for (String url : urls) {
            executor.submit(() -> {
                try {
                    long threadStartTime = System.currentTimeMillis();
                    String content = downloadWebPage(url);
                    long threadEndTime = System.currentTimeMillis();
                    System.out.println("Downloaded " + url + " in " + (threadEndTime - threadStartTime) + "ms");

                    // Save the content to a local file
                    saveToFile(url, content);
                } catch (IOException e) {
                    System.err.println("Failed to download " + url);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total download time: " + (endTime - startTime) + "ms");
    }

    private static String downloadWebPage(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            // Successfully downloaded the web page
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } else {
            throw new IOException("HTTP Error: " + responseCode);
        }
    }

    private static void saveToFile(String url, String content) {
        String fileName = url.substring(url.lastIndexOf("/") + 1) + ".html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("Saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to save to file: " + fileName);
        }
    }
}
