package com.owobot.model;

public class YouTubeVideo {
    public String watchUrl;
    public String title;
    public String length;
    public String author;
    public String thumbnailUrl;

    @Override
    public String toString() {
        return "YouTubeVideo{" +
                "watchUrl='" + watchUrl + '\'' +
                ", title='" + title + '\'' +
                ", length='" + length + '\'' +
                ", author='" + author + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
