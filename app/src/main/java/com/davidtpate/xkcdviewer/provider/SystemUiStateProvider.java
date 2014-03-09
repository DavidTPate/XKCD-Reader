package com.davidtpate.xkcdviewer.provider;

/**
 * Used to determine whether we are in fullscreen or windowed mode
 *
 */
public interface SystemUiStateProvider {
    public boolean isSystemUiVisible();
}

