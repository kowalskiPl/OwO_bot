package src.youtube;

import com.google.gson.Gson;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import src.model.YouTubeVideo;
import src.model.youtube.Content;
import src.model.youtube.ItemSectionRenderer;
import src.model.youtube.SearchQueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class YouTubeRequestResultParser {

    private static final Pattern searchContentJsonPattern = Pattern.compile("(ytInitialData) = (\\{.*})");
    private static final Pattern searchThumbnailUrl = Pattern.compile("<link itemprop=\"thumbnailUrl\" href=\"(.*?)\">");

    public static List<YouTubeVideo> getVideoUrlFromSearch(Document document) {
        Element body = document.body();
        String html = body.html();
        Matcher m = searchContentJsonPattern.matcher(html);
        List<YouTubeVideo> videoResults = new ArrayList<>();
        if (m.find()) {
            String json = m.group(2);
            Gson gson = new Gson();
            SearchQueryResult result = gson.fromJson(json, SearchQueryResult.class);
            result.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents.forEach(rendererContents -> {
                ItemSectionRenderer renderer = rendererContents.itemSectionRenderer;
                if (renderer != null){
                    renderer.contents.forEach(content -> {
                        if (content.videoRenderer != null) {
                            YouTubeVideo video = new YouTubeVideo();
                            video.author = content.videoRenderer.ownerText.runs.get(0).text;
                            video.length = content.videoRenderer.lengthText.simpleText;
                            video.watchUrl = content.videoRenderer.navigationEndpoint.commandMetadata.webCommandMetadata.url;
                            video.title = content.videoRenderer.title.runs.get(0).text;
                            video.thumbnailUrl = content.videoRenderer.thumbnail.thumbnails.get(0).url;
                            videoResults.add(video);
                        }
                    });
                }
            });
        }
        return videoResults.stream().limit(10).collect(Collectors.toList());
    }

    public static String getThumbnailUrlFromYouTubeUrl(Document document) {
        String html = document.html();
        Matcher m = searchThumbnailUrl.matcher(html);
        if (m.find()){
            return m.group(1);
        }
        return "";
    }

}
