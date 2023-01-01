import org.junit.jupiter.api.Test;
import com.owobot.youtube.HttpYouTubeRequester;
import com.owobot.youtube.YouTubeRequestResultParser;

public class YouTubeTests {
    @Test
    public void queryTest() {
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.performSearchQuery("PUR Kowalski");
        if (result.isPresent()) {
            var videos = YouTubeRequestResultParser.getVideoUrlFromSearch(result.get());
            videos.forEach(System.out::println);
        }
    }

    @Test
    public void thumbNailTest() {
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.performSearchQuery("Devil trigger");
        if (result.isPresent()) {
            var videos = YouTubeRequestResultParser.getVideoUrlFromSearch(result.get());
            videos.forEach(youTubeVideo -> System.out.println(youTubeVideo.thumbnailUrl));
        }
    }

    @Test
    public void thumbnailFromUrlTest() {
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.queryYoutubeVideo("https://www.youtube.com/watch?v=vMIf7DKzWEM");
        if (result.isPresent()){
            var url = YouTubeRequestResultParser.getThumbnailUrlFromYouTubeUrl(result.get());
            System.out.println(url);
            assert !"".equals(url);
        }
    }
}
