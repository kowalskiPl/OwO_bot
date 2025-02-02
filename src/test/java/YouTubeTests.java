import com.owobot.modules.music.SongRequestProcessingException;
import com.owobot.modules.music.model.YouTubeVideo;
import com.owobot.modules.music.youtube.HttpYouTubeRequester;
import com.owobot.modules.music.youtube.YouTubeRequestResultParser;
import com.owobot.modules.music.youtube.YouTubeSearch;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class YouTubeTests {

    private static final Pattern searchContentJsonPattern = Pattern.compile("(ytInitialData) = (\\{.*\\})");

    @Test
    public void queryTest() {
        System.out.println("Test start!");

        try {
            var moreResult = YouTubeSearch.performVideoQuery("devil trigger");
            System.out.println("First test!");
            for (var res : moreResult){
                System.out.println(res);
            }
        } catch (SongRequestProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Test end!");

//        var result = HttpYouTubeRequester.performSearchQuery("bury the light");
//        if (result.isPresent()) {
//            List<YouTubeVideo> videos = null;
//            try {
//                videos = YouTubeRequestResultParser.getVideoUrlFromSearch(result.get());
//            } catch (SongRequestProcessingException e) {
//                throw new RuntimeException(e);
//            }
//            videos.forEach(System.out::println);
//        }
    }

    @Test
    public void thumbNailTest() {
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.performSearchQuery("Devil trigger");
        if (result.isPresent()) {
            List<YouTubeVideo> videos = null;
            try {
                videos = YouTubeRequestResultParser.getVideoUrlFromSearch(result.get());
            } catch (SongRequestProcessingException e) {
                throw new RuntimeException(e);
            }
            videos.forEach(youTubeVideo -> System.out.println(youTubeVideo.thumbnailUrl));
        }
    }

    @Test
    public void thumbnailFromUrlTest() {
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.queryYoutubeVideo("https://www.youtube.com/watch?v=-QW6BXwMXEs");
        if (result.isPresent()) {
            var url = YouTubeRequestResultParser.getThumbnailUrlFromYouTubeUrl(result.get());
            System.out.println(url);
            assert !"".equals(url);
        }
    }
}
