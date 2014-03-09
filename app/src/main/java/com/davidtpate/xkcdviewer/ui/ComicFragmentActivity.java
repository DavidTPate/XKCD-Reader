package com.davidtpate.xkcdviewer.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.davidtpate.xkcdviewer.R;
import com.davidtpate.xkcdviewer.adapter.ComicPagerAdapter;
import com.davidtpate.xkcdviewer.model.Comic;
import com.davidtpate.xkcdviewer.model.Constants;
import com.davidtpate.xkcdviewer.preferences.SharedPreferencesHelper;
import com.davidtpate.xkcdviewer.ui.base.BaseFragmentActivity;
import com.davidtpate.xkcdviewer.ui.dialog.JumpToDialogFragment;
import com.davidtpate.xkcdviewer.util.Ln;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

public class ComicFragmentActivity extends BaseFragmentActivity implements
    JumpToDialogFragment.JumpToDialogListener {
    protected ComicPagerAdapter mAdapter;
    @InjectView(R.id.vp_pager)
    protected ViewPager mPager;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_fragment_activity);
        initializeAdapter();
        initializeActionBar();
        moveViewPagerToRequestedIndex();

        GetCurrentComicTask getCurrentComicTask = new GetCurrentComicTask();
        getCurrentComicTask.execute((Void) null);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.comic_fragment_activity, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_jump_to:
                //Show dialog fragment to get number.
                JumpToDialogFragment jumpToDialogFragment = JumpToDialogFragment.newInstance();
                jumpToDialogFragment.show(getSupportFragmentManager(), "JumpTo");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void initializeAdapter() {
        mAdapter = new ComicPagerAdapter(getSupportFragmentManager(),
            SharedPreferencesHelper.getMaxComics(this));
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(Constants.MagicNumbers.MAX_COMIC_OFFSCREEN_LIMIT);
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

    @Override public void onJumpTo(int jumpToValue) {
        //The comic is 1 based while arrays are 0 based, so decrement to make it place properly.
        mPager.setCurrentItem(jumpToValue - 1);
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
                SharedPreferencesHelper.setMaxComics(ComicFragmentActivity.this, comic.getNumber());
                mAdapter.updateMaxComicNumber(comic.getNumber());
            }
        }
    }
}
