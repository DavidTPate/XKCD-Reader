package com.davidtpate.xkcdviewer.module;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module
    (
        includes = {
            AndroidModule.class,
            ApplicationModule.class,
            ServiceModule.class
        }
    )
public class RootModule {
}
