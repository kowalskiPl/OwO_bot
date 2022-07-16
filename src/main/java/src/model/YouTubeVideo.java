package src.model;

public class YouTubeVideo {
    public String watchUrl;
    public String title;
    public String length;
    public String author;

    @Override
    public String toString() {
        return "YouTubeVideo{" +
                "watchUrl='" + watchUrl + '\'' +
                ", title='" + title + '\'' +
                ", length='" + length + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
