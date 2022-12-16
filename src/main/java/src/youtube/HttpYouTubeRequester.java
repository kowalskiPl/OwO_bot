package src.youtube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpYouTubeRequester {
    private static final String ytUrl = "https://www.youtube.com";
    private static final String searchUrlPart = "/results?search_query=";

    private static final Logger log = LoggerFactory.getLogger(HttpYouTubeRequester.class);

    public static Optional<Document> performSearchQuery(String queryString) {
        String encodedQuery = URLEncoder.encode(queryString, StandardCharsets.UTF_8);
        String encodedUrl = ytUrl + searchUrlPart + encodedQuery;
        try {
            Document document = Jsoup.connect(encodedUrl)
                    .timeout(5000)
                    .header("User-Agent", "Chrome")
                    .get();
            return Optional.of(document);
        } catch (IOException e) {
            log.warn("Failed to perform YouTube query: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }
}