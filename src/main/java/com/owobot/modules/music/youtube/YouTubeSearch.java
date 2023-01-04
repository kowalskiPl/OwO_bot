package com.owobot.modules.music.youtube;

import com.owobot.modules.music.model.YouTubeVideo;

import java.util.ArrayList;
import java.util.List;

public class YouTubeSearch {
    public static List<YouTubeVideo> performVideoQuery(String queryString) {
        var document = HttpYouTubeRequester.performSearchQuery(queryString);
        return document.map(YouTubeRequestResultParser::getVideoUrlFromSearch).orElseGet(ArrayList::new);
    }
}
