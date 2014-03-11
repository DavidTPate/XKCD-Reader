package com.davidtpate.xkcd.ui.base;

import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.debug.hv.ViewServer;
import com.davidtpate.xkcd.BuildConfig;
import com.davidtpate.xkcd.ui.ComicFragmentActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Base class for all Activities that need fragments.
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // This is the home button in the top left corner of the screen.
                // Dont call finish! Because activity could have been started by an outside activity and the home button would not operated as expected!
                Intent homeIntent = new Intent(this, ComicFragmentActivity.class);
                homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
