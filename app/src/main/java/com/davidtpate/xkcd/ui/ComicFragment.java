package com.davidtpate.xkcd.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.davidtpate.xkcd.R;
import com.davidtpate.xkcd.model.Comic;
import com.davidtpate.xkcd.model.Constants;
import com.davidtpate.xkcd.provider.SystemUiStateProvider;
import com.davidtpate.xkcd.ui.base.BaseFragment;
import com.davidtpate.xkcd.util.ComicUtil;
import com.davidtpate.xkcd.util.Ln;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class ComicFragment extends BaseFragment {
    @InjectView(R.id.ll_comic_information) LinearLayout mComicInformation;
    @InjectView(R.id.tv_title) TextView mTitle;
    @InjectView(R.id.tv_subtitle) TextView mSubTitle;
    @InjectView(R.id.tv_comic_details) TextView mComicDetails;
    @InjectView(R.id.iv_comic) PhotoView mComicImage;

    protected int mActionBarHeight;
    protected int mComicNumber = Constants.LATEST_COMIC_NUMBER;
    protected Comic mComic = null;
    protected SystemUiStateProvider mSystemUiStateProvider;

    protected BroadcastReceiver mToggleFullscreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSystemUiVisible =
                intent.getBooleanExtra(Constants.Extra.EXTRA_SYSTEM_UI_VISIBILITY, false);
            if (isSystemUiVisible) {
                goFullscreen();
            } else {
                exitFullscreen();
            }
        }
    };

    protected PhotoViewAttacher.OnPhotoTapListener mPhotoTapListener =
        new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                Intent intent = new Intent(Constants.Intent.BROADCAST_TOGGLE_FULLSCREEN);
                intent.putExtra(Constants.Extra.EXTRA_SYSTEM_UI_VISIBILITY,
                    mSystemUiStateProvider.isSystemUiVisible());
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        };

    /**
     * Factory method to generate a new instance of the fragment given a comic number.
     *
     * @param comicNumber The number representing the comic to load
     * @return A new instance of ComicFragment with extras
     */
    public static ComicFragment newInstance(int comicNumber) {
        final ComicFragment f = new ComicFragment();

        final Bundle args = new Bundle();
        args.putInt(Constants.Extra.EXTRA_COMIC_NUMBER, comicNumber);
        f.setArguments(args);

        return f;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        loadSavedInstanceState(getArguments());
        loadSavedInstanceState(savedInstanceState);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link android.app.Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override public void onResume() {
        super.onResume();
        if (mSystemUiStateProvider.isSystemUiVisible()) {
            exitFullscreen();
        } else {
            goFullscreen();
        }
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadSavedInstanceState(savedInstanceState);

        try {
            mSystemUiStateProvider = (SystemUiStateProvider) getActivity();
        } catch (ClassCastException e) {
            Ln.e(e, "The parent activity must implement the SystemUiStateProvider interface");
        }

        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                getActivity().getResources().getDisplayMetrics());
        }

        LocalBroadcastManager.getInstance(getActivity())
            .registerReceiver(mToggleFullscreenReceiver,
                new IntentFilter(Constants.Intent.BROADCAST_TOGGLE_FULLSCREEN));

        mComicImage.setOnPhotoTapListener(mPhotoTapListener);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comic_fragment, container, false);
        ResolveComicTask resolveComicTask = new ResolveComicTask();
        resolveComicTask.execute(mComicNumber);
        return view;
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(android.os.Bundle)},
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
     * android.os.Bundle)}, and
     * {@link #onActivityCreated(android.os.Bundle)}.
     *
     * <p>This corresponds to {@link android.app.Activity#onSaveInstanceState(android.os.Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.Extra.EXTRA_COMIC_NUMBER, mComicNumber);

        if (mComic != null) {
            outState.putParcelable(Constants.Extra.EXTRA_COMIC, mComic);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity())
            .unregisterReceiver(mToggleFullscreenReceiver);
        super.onDestroy();
    }

    protected void loadSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mComicNumber = savedInstanceState.getInt(Constants.Extra.EXTRA_COMIC_NUMBER,
                Constants.LATEST_COMIC_NUMBER);

            if (savedInstanceState.containsKey(Constants.Extra.EXTRA_COMIC)) {
                mComic = savedInstanceState.getParcelable(Constants.Extra.EXTRA_COMIC);
            }
        }
    }

    protected void goFullscreen() {
        animate(mComicInformation).setDuration(500).y(-400);
        mComicInformation.postDelayed(new Runnable() {
            @Override public void run() {
                mComicInformation.setVisibility(View.GONE);
            }
        }, 600);

    }

    protected void exitFullscreen() {
        mComicInformation.setVisibility(View.VISIBLE);
        animate(mComicInformation).setDuration(500).y(mActionBarHeight);
    }

    private class ResolveComicTask extends AsyncTask<Integer, Void, Comic> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         *
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param comicNumber The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override protected Comic doInBackground(Integer... comicNumber) {
            // Should only ever be a single comic number, so let's enforce it.
            if (comicNumber != null && comicNumber.length == 1) {
                HttpRequest request = HttpRequest.get(ComicUtil.getComicApiUrl(comicNumber[0]));
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
            return null;
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
            if (comic != null && mTitle != null) {
                mComic = comic;
                mTitle.setText(comic.getTitle() + " (#" + comic.getNumber() + ")");
                mSubTitle.setText(comic.getSubTitle());
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.valueOf(comic.getYear()), Integer.valueOf(comic.getMonth()),
                    Integer.valueOf(comic.getDay()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("'Posted: 'EEE, MMM dd yyyy");
                mComicDetails.setText(dateFormat.format(cal.getTime()));
                Picasso.with(getActivity())
                    .load(comic.getImageUrl())
                    .placeholder(R.drawable.loading_spinner_76)
                    .into(mComicImage);
            }
        }
    }
}
