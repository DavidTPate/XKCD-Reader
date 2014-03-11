package com.davidtpate.xkcd.model;

public class Constants {
    public static final int LATEST_COMIC_NUMBER = -42;

    public static class API {
        public static final String LATEST_COMIC_ENDPOINT = "http://xkcd.com/info.0.json";
        public static final String SPECIFIC_COMIC_ENDPOINT = "http://xkcd.com/%d/info.0.json";
    }

    public static class Extra {
        public static final String EXTRA_COMIC_NUMBER = "com.davidtpate.xkcdviewer.extras.COMIC_NUMBER";
        public static final String EXTRA_COMIC = "com.davidtpate.xkcdviewer.extras.COMIC";
        public static final String EXTRA_SYSTEM_UI_VISIBILITY = "com.davidtpate.xkcdviewer.extra.SYSTEM_UI_VISIBILITY";
    }

    public static class Intent {
        public static final String BROADCAST_TOGGLE_FULLSCREEN = "com.davidtpate.xkcdviewer.broadcast.TOGGLE_FULLSCREEN";
    }

    public static class MagicNumbers {
        public static final int MAX_COMIC_OFFSCREEN_LIMIT = 2;
    }

    public static class Preferences {
        public static final String MAX_COMICS = "maxComics";
    }
}
