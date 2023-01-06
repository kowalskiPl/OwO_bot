package com.owobot.modules.music.youtube;

import com.owobot.modules.music.SongRequestProcessingException;
import com.owobot.modules.music.model.YouTubeVideo;

import java.util.List;

public class YouTubeSearch {
    public static List<YouTubeVideo> performVideoQuery(String queryString) throws SongRequestProcessingException {
        var document = HttpYouTubeRequester.performSearchQuery(queryString);
        if (document.isPresent()){
            return YouTubeRequestResultParser.getVideoUrlFromSearch(document.get());
        }
        throw new SongRequestProcessingException();
    }
}
