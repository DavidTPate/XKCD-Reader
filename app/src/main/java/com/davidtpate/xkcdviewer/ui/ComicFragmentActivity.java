package com.davidtpate.xkcdviewer.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.davidtpate.xkcdviewer.R;
import com.davidtpate.xkcdviewer.adapter.ComicPagerAdapter;
import com.davidtpate.xkcdviewer.model.Comic;
import com.davidtpate.xkcdviewer.model.Constants;
import com.davidtpate.xkcdviewer.preferences.SharedPreferencesHelper;
import com.davidtpate.xkcdviewer.provider.SystemUiStateProvider;
import com.davidtpate.xkcdviewer.ui.base.BaseFragmentActivity;
import com.davidtpate.xkcdviewer.ui.dialog.JumpToDialogFragment;
import com.davidtpate.xkcdviewer.util.AndroidUtil;
import com.davidtpate.xkcdviewer.util.Ln;
import com.davidtpate.xkcdviewer.util.MathUtil;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

public class ComicFragmentActivity extends BaseFragmentActivity
    implements JumpToDialogFragment.JumpToDialogListener, SystemUiStateProvider {
    protected MenuItem mExpandMenuItem;
    protected MenuItem mCollapseMenuItem;
    protected ComicPagerAdapter mAdapter;
    @InjectView(R.id.vp_pager)
    protected ViewPager mPager;
    protected int mMaxComics = Constants.LATEST_COMIC_NUMBER;

    protected View.OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
    protected BroadcastReceiver mToggleFullscreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSystemUiVisible =
                intent.getBooleanExtra(Constants.Extra.EXTRA_SYSTEM_UI_VISIBILITY, false);
            if (mPager != null) {
                if (isSystemUiVisible) {
                    goFullscreen();
                } else {
                    exitFullscreen();
                }
            }
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_fragment_activity);
        initializeAdapter();
        initializeViewPager();
        initializeActionBar();
        moveViewPagerToRequestedIndex();
        registerLocalBroadcastReceivers();

        GetCurrentComicTask getCurrentComicTask = new GetCurrentComicTask();
        getCurrentComicTask.execute((Void) null);
    }

    @Override protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mToggleFullscreenReceiver);
        super.onDestroy();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.comic_fragment_activity, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        mExpandMenuItem = menu.findItem(R.id.menu_expand);
        mCollapseMenuItem = menu.findItem(R.id.menu_collapse);
        updateMenuView();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_jump_to:
                //Show dialog fragment to get number.
                JumpToDialogFragment jumpToDialogFragment = JumpToDialogFragment.newInstance();
                jumpToDialogFragment.show(getSupportFragmentManager(), "JumpTo");
                return true;
            case R.id.menu_expand:
                Intent expandIntent = new Intent(Constants.Intent.BROADCAST_TOGGLE_FULLSCREEN);
                expandIntent.putExtra(Constants.Extra.EXTRA_SYSTEM_UI_VISIBILITY,
                    isSystemUiVisible());
                LocalBroadcastManager.getInstance(this).sendBroadcast(expandIntent);
                return true;
            case R.id.menu_collapse:
                Intent collapseIntent = new Intent(Constants.Intent.BROADCAST_TOGGLE_FULLSCREEN);
                collapseIntent.putExtra(Constants.Extra.EXTRA_SYSTEM_UI_VISIBILITY, isSystemUiVisible());
                LocalBroadcastManager.getInstance(this).sendBroadcast(collapseIntent);
                return true;
            case R.id.menu_random:
                if (mPager != null) {
                    mPager.setCurrentItem(MathUtil.randInt(1, SharedPreferencesHelper.getMaxComics(this)));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Extra.EXTRA_COMIC_NUMBER, mMaxComics);
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMaxComics = savedInstanceState.getInt(Constants.Extra.EXTRA_COMIC_NUMBER,
            Constants.LATEST_COMIC_NUMBER);
    }

    protected void registerLocalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mToggleFullscreenReceiver,
                new IntentFilter(Constants.Intent.BROADCAST_TOGGLE_FULLSCREEN));
    }

    protected void initializeAdapter() {
        mMaxComics = SharedPreferencesHelper.getMaxComics(this);
        mAdapter = new ComicPagerAdapter(getSupportFragmentManager(), mMaxComics);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) protected void initializeViewPager() {
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(Constants.MagicNumbers.MAX_COMIC_OFFSCREEN_LIMIT);
        // Hide and show the ActionBar as the visibility changes
        if (AndroidUtil.hasHoneycomb()) {
            mPager.setOnSystemUiVisibilityChangeListener(getSystemUiVisibilityChangeListener());
        }
    }

    protected void initializeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    protected void moveViewPagerToRequestedIndex() {
        final int currentItem = getIntent().getIntExtra(Constants.Extra.EXTRA_COMIC_NUMBER,
            Constants.LATEST_COMIC_NUMBER);
        if (currentItem == Constants.LATEST_COMIC_NUMBER || currentItem <= 0) {
            mPager.setCurrentItem(mAdapter.getCount());
        } else {
            mPager.setCurrentItem(currentItem);
        }
    }

    protected void updateMenuView() {
        if (mExpandMenuItem != null && mCollapseMenuItem != null) {
            if (isSystemUiVisible()) {
                mExpandMenuItem.setVisible(true);
                mCollapseMenuItem.setVisible(false);
            } else {
                mExpandMenuItem.setVisible(false);
                mCollapseMenuItem.setVisible(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected View.OnSystemUiVisibilityChangeListener getSystemUiVisibilityChangeListener() {
        if (mOnSystemUiVisibilityChangeListener == null) {
            mOnSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                        getSupportActionBar().hide();
                    } else {
                        getSupportActionBar().show();
                    }
                }
            };
        }
        return mOnSystemUiVisibilityChangeListener;
    }

    @Override public void onJumpTo(int jumpToValue) {
        //The comic is 1 based while arrays are 0 based, so decrement to make it place properly.
        mPager.setCurrentItem(jumpToValue - 1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) protected void goFullscreen() {
        if (AndroidUtil.hasHoneycomb()) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        } else {
            getSupportActionBar().hide();
        }
        updateMenuView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) protected void exitFullscreen() {
        if (AndroidUtil.hasHoneycomb()) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getSupportActionBar().show();
        }
        updateMenuView();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) public boolean isSystemUiVisible() {
        if (AndroidUtil.hasHoneycomb()) {
            final int vis = mPager.getSystemUiVisibility();
            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) return false;
        } else {
            return getSupportActionBar().isShowing();
        }

        return true;
    }

    private class GetCurrentComicTask extends AsyncTask<Void, Void, Comic> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         *
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override protected Comic doInBackground(Void... params) {
            HttpRequest request = HttpRequest.get(Constants.API.LATEST_COMIC_ENDPOINT);
            if (request.code() == 200) {
                String response = request.body();
                request.disconnect();
                Gson gson = new Gson();
                Comic comicResponse = gson.fromJson(response, Comic.class);

                return comicResponse;
            } else {
                request.disconnect();
                return null;
            }
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param comic The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override protected void onPostExecute(Comic comic) {
            super.onPostExecute(comic);
            if (comic != null) {
                Ln.d("Got Max Comic Number: %d", comic.getNumber());
                mMaxComics = comic.getNumber();
                SharedPreferencesHelper.setMaxComics(ComicFragmentActivity.this, mMaxComics);
                mAdapter.updateMaxComicNumber(mMaxComics);
                mPager.setCurrentItem(mMaxComics);
            }
        }
    }
}
