package com.davidtpate.xkcd.adapter;

import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.davidtpate.xkcd.model.Constants;
import com.davidtpate.xkcd.ui.ComicFragment;
import com.davidtpate.xkcd.util.Ln;

/**
 * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
 * could be a large number of items in the ViewPager and we don't want to retain them all in
 * memory at once but create/destroy them on the fly.
 */
public class ComicPagerAdapter extends FixedFragmentStatePagerAdapter {
    protected int mMaxComicNumber;
    public ComicPagerAdapter(FragmentManager fm, int mMaxComicNumber) {
        super(fm);
        this.mMaxComicNumber = mMaxComicNumber;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    @Override public Fragment getItem(int position) {
        // Arrays are 0 based, but the comic is 1 based, so always add 1.
        position++;

        Ln.d("Creating Fragment %d", position);
        // We want this to start from the most recent, so
        return ComicFragment.newInstance(position);
    }

    /**
     * Return the number of views available.
     */
    @Override public int getCount() {
        // If we don't know the maximum value yet, or had a bad value passed limit this to only the current comic until it is updated.
        if (mMaxComicNumber == Constants.LATEST_COMIC_NUMBER || mMaxComicNumber <= 0) {
            return 1;
        }
        return mMaxComicNumber;
    }

    public void updateMaxComicNumber(int mMaxComicNumber) {
        Ln.d("Got New Max Comic Number %d, Updating Adapter", mMaxComicNumber);
        this.mMaxComicNumber = mMaxComicNumber;
        notifyDataSetChanged();
    }
}
