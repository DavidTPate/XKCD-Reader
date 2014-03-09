package com.davidtpate.xkcdviewer.preferences;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.davidtpate.xkcdviewer.model.Constants;
import com.davidtpate.xkcdviewer.util.AndroidUtil;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesHelper {

    /**
     * Save an array to {@link SharedPreferences}
     *
     * @param array The array of strings to save
     * @param prefsName The name of the preferences file
     * @param arrayName The name to save the array as
     * @param context The context
     */
    public static void saveArray(List<String> array, String prefsName, String arrayName,
        Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        editor.putInt(arrayName + "_size", array.size());

        for (int i = 0; i < array.size(); i++) {
            String s = array.get(i);
            editor.putString(arrayName + "_" + i, s);
        }

        save(editor);
    }

    /**
     * Load an array of Strings from {@link SharedPreferences}
     *
     * @param prefsName The name of the preferences file
     * @param arrayName The name of the array
     * @param context The context
     * @return An {@link ArrayList} of strings containing the data loaded from
     * {@link SharedPreferences}
     */
    public static List<String> loadArray(String prefsName, String arrayName, Context context) {
        List<String> returnList = new ArrayList<String>();
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        int size = prefs.getInt(arrayName + "_size", 0);

        for (int i = 0; i < size; i++)
            returnList.add(prefs.getString(arrayName + "_" + i, null));

        return returnList;
    }

    /**
     * Add a single item to the array saved in {@link SharedPreferences}
     *
     * @param valueToAdd The String to add
     * @param prefsName The preferences file name
     * @param arrayName The name of the array
     * @param context The context
     */
    public static void addToArray(String valueToAdd, String prefsName, String arrayName,
        Context context) {
        List<String> array = loadArray(prefsName, arrayName, context);
        array.add(valueToAdd);

        SharedPreferences prefs = context.getSharedPreferences(prefsName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        editor.putInt(arrayName + "_size", array.size());

        for (int i = 0; i < array.size(); i++) {
            String s = array.get(i);
            editor.putString(arrayName + "_" + i, s);
        }

        save(editor);
    }

    /**
     * Commit the preferences asynchronously in Gingerbread and later, otherwise we have to do it
     * synchronously
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void save(SharedPreferences.Editor editor) {
        if (AndroidUtil.hasGingerbread()) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * Get the last recorded value for the maximum comic number.
     *
     * @param context The context
     * @return The last recorded maximum value for comic number, if not present returns
     * Constants.LATEST_COMIC_NUMBER
     */
    public static int getMaxComics(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(Constants.Preferences.MAX_COMICS, Constants.LATEST_COMIC_NUMBER);
    }

    /**
     * Get the last recorded value for the maximum comic number.
     *
     * @param context The context
     * @param maxComics The maximum value for comic number to record
     */
    public static void setMaxComics(Context context, int maxComics) {
        SharedPreferences.Editor editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(Constants.Preferences.MAX_COMICS, maxComics);
        save(editor);
    }
}
