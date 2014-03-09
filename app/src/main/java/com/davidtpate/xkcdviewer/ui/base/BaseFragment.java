package com.davidtpate.xkcdviewer.ui.base;

import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import com.actionbarsherlock.app.SherlockFragment;
import com.davidtpate.xkcdviewer.Injector;

/**
 * Base class for all non-specialized Fragments.
 */
public abstract class BaseFragment extends SherlockFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Injector.inject(this);
    }

    public void showProgressBar() {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressBar() {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    /**
     * Is this fragment still part of an activity and usable from the UI-thread?
     *
     * @return true if usable on the UI-thread, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }
}
