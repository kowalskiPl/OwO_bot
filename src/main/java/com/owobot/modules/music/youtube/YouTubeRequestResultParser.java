package com.owobot.modules.music.youtube;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.owobot.modules.music.SongRequestProcessingException;
import com.owobot.modules.music.model.YouTubeVideo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class YouTubeRequestResultParser {

    private static final Pattern searchContentJsonPattern = Pattern.compile("(ytInitialData) = (\\{.*})");
    private static final Pattern searchThumbnailUrl = Pattern.compile("<link itemprop=\"thumbnailUrl\" href=\"(.*?)\">");

    public static List<YouTubeVideo> getVideoUrlFromSearch(Document document) throws SongRequestProcessingException {
        Element body = document.body();
        String html = body.html();
        Matcher m = searchContentJsonPattern.matcher(html);
        List<YouTubeVideo> videoResults = new ArrayList<>();
        if (m.find()) {
            String json = m.group(2);
            JsonElement element = JsonParser.parseString(json);
            JsonObject jsonObject = element.getAsJsonObject();

            var tracks = jsonObject.get("contents").getAsJsonObject()
                    .get("twoColumnSearchResultsRenderer").getAsJsonObject()
                    .get("primaryContents").getAsJsonObject()
                    .get("sectionListRenderer").getAsJsonObject()
                    .get("contents").getAsJsonArray().get(0).getAsJsonObject()
                    .get("itemSectionRenderer").getAsJsonObject()
                    .get("contents").getAsJsonArray();
            for (int i = 0; i < 9; i++) {
                try {
                    var singleTrack = tracks.get(i).getAsJsonObject();
                    var videoElement = singleTrack.get("videoRenderer").getAsJsonObject();
                    YouTubeVideo video = new YouTubeVideo();
                    video.title = videoElement.get("title").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                    video.length = videoElement.get("lengthText").getAsJsonObject().get("simpleText").getAsString();
                    video.thumbnailUrl = videoElement.get("thumbnail").getAsJsonObject().get("thumbnails").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
                    video.author = videoElement.get("ownerText").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                    video.watchUrl = videoElement.get("navigationEndpoint").getAsJsonObject().get("commandMetadata").getAsJsonObject()
                            .get("webCommandMetadata").getAsJsonObject().get("url").getAsString();
                    videoResults.add(video);
                } catch (NullPointerException e) {
                    throw new SongRequestProcessingException("Failed to acquire some song results!");
                }
            }
        }
        return videoResults.stream().limit(10).collect(Collectors.toList());
    }

    public static String getThumbnailUrlFromYouTubeUrl(Document document) {
        String html = document.html();
        Matcher m = searchThumbnailUrl.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

}
