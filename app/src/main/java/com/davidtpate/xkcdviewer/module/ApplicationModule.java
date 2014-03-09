package com.davidtpate.xkcdviewer.module;

import com.davidtpate.xkcdviewer.BaseApplication;
import com.davidtpate.xkcdviewer.bus.MainThreadBus;
import com.davidtpate.xkcdviewer.ui.ComicFragment;
import com.davidtpate.xkcdviewer.ui.ComicFragmentActivity;
import com.davidtpate.xkcdviewer.ui.CurrentComic;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Dagger module for setting up provides statements. Register all of your entry points below.
 */
@Module(
    complete = false,
    injects = {
        CurrentComic.class,
        BaseApplication.class,
        ComicFragmentActivity.class,
        ComicFragment.class
    },
    library = true)
public class ApplicationModule {
    @Singleton
    @Provides Bus provideOttoBus() {
        return new MainThreadBus(new Bus());
    }
}
