import org.junit.jupiter.api.Test;
import src.youtube.HttpYouTubeRequester;
import src.youtube.YouTubeRequestResultParser;

public class YouTubeTests {
    @Test
    public void queryTest(){
        System.out.println("Test start!");
        var result = HttpYouTubeRequester.performSearchQuery("PUR Kowalski");
        if (result.isPresent()){
            var videos = YouTubeRequestResultParser.getVideoUrlFromSearch(result.get());
            videos.forEach(System.out::println);
        }
    }
}
