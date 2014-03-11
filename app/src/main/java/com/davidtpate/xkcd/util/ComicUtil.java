package com.davidtpate.xkcd.util;

import com.davidtpate.xkcd.model.Constants;

/**
 * General Util class for XKCD comics.
 */
public class ComicUtil {

    public static String getComicApiUrl(int comicNumber) {
        // If we have an extravagant case, default it to the Current Comic since it is invalid.
        if (comicNumber == Constants.LATEST_COMIC_NUMBER || comicNumber <= 0) {
            return Constants.API.LATEST_COMIC_ENDPOINT;
        } else {
            return String.format(Constants.API.SPECIFIC_COMIC_ENDPOINT, comicNumber);
        }
    }
}
