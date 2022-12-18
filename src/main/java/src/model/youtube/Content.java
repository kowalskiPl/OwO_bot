package src.model.youtube;

import java.util.List;

public class Content {
    public VideoRenderer videoRenderer;

    public class Title{
        public List<Run> runs;
        public class Run{
            public String text;
        }
    }

    public class LengthText{
        public String simpleText;
    }

    public class NavigationEndpoint{
        public String clickTrackingParams;
        public CommandMetadata commandMetadata;

        public class CommandMetadata{
            public WebCommandMetadata webCommandMetadata;
            public class WebCommandMetadata{
                public String url;
                public String webPageType;
                public int rootVe;
            }
        }
    }

    public class OwnerText {
        public List<OwnerRuns> runs;
        public class OwnerRuns{
            public String text;
        }
    }

    public class Thumbnail{
        public List<InnerThumbnail> thumbnails;
        public class InnerThumbnail{
            public String url;
            public int width;
            public int height;
        }
    }

    public class VideoRenderer{
        public Title title;
        public LengthText lengthText;
        public NavigationEndpoint navigationEndpoint;
        public OwnerText ownerText;
        public Thumbnail thumbnail;
    }
}
