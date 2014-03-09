package com.davidtpate.xkcdviewer.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.davidtpate.xkcdviewer.R;
import com.davidtpate.xkcdviewer.model.Comic;
import com.davidtpate.xkcdviewer.ui.base.BaseActivity;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class CurrentComic extends BaseActivity {
    @InjectView(R.id.tv_title) TextView title;
    @InjectView(R.id.tv_subtitle) TextView subTitle;
    @InjectView(R.id.iv_comic) ImageView comicImage;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_comic);
        GetCurrentComicTask getCurrentComic = new GetCurrentComicTask();
        getCurrentComic.execute((Void) null);
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
            HttpRequest request = HttpRequest.get("http://xkcd.com/info.0.json");
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
                title.setText(comic.getTitle());
                subTitle.setText(comic.getSubTitle());
                Picasso.with(CurrentComic.this).load(comic.getImageUrl()).into(comicImage);
            }
        }
    }
}
