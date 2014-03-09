package com.davidtpate.xkcdviewer;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import com.github.kevinsawicki.http.HttpRequest;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;

/**
 * Android Bootstrap application
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;

    /**
     * Create main application
     */
    public BaseApplication() {

        // Disable http.keepAlive on Froyo and below
        if (SDK_INT <= FROYO) {
            HttpRequest.keepAlive(false);
        }
    }

    /**
     * Create main application
     *
     * @param context
     */
    public BaseApplication(final Context context) {
        this();
        attachBaseContext(context);

    }

    /**
     * Create main application
     *
     * @param instrumentation
     */
    public BaseApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

}