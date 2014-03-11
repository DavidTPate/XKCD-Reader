package com.davidtpate.xkcd.ui;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.davidtpate.xkcd.R;
import com.davidtpate.xkcd.ui.base.BaseActivity;

public class About extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_fragment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
