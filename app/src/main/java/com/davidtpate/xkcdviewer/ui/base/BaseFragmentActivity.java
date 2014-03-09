package com.davidtpate.xkcdviewer.ui.base;

import android.os.Bundle;
import butterknife.ButterKnife;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.debug.hv.ViewServer;
import com.davidtpate.xkcdviewer.BuildConfig;
import com.davidtpate.xkcdviewer.Injector;

/**
 * Base class for all Activities that need fragments.
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        if (BuildConfig.DEBUG) {
            ViewServer.get(this).addWindow(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).removeWindow(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).setFocusedWindow(this);
        }
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        // Used to inject views with the Butterknife library
        ButterKnife.inject(this);
    }

}
