package com.example.android.popularmovies.videos;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.AsyncLoadingListActions;
import com.example.android.popularmovies.ListResultHolder;

import java.util.Arrays;
import java.util.List;

public class VideosListAsyncTask extends AsyncTask<String, Void, ListResultHolder<Video>> {

    private final AsyncLoadingListActions<Video> loadingVideosActions;

    public VideosListAsyncTask(AsyncLoadingListActions<Video> loadingVideosActions) {

        this.loadingVideosActions = loadingVideosActions;
    }

    @Override
    protected void onPreExecute() {
        loadingVideosActions.loadingStarted();
    }

    @Override
    protected ListResultHolder<Video> doInBackground(String... params) {
        if (params == null || params.length < 2) {
            return null;
        }
        String apiKey = params[0];
        String movieId = params[1];
        try {
            List<Video> videos = VideosApiUtils.fetchVideosForMovie(apiKey, movieId);
            ListResultHolder<Video> moviesResultHolder = new ListResultHolder<>(videos);
            Log.e(VideosListAsyncTask.class.getSimpleName(), Arrays.toString(videos.toArray()));
            return moviesResultHolder;
        } catch (VideosApiUtils.VideoProvidingException e) {
            return new ListResultHolder<>(e);
        }
    }

    @Override
    protected void onPostExecute(ListResultHolder<Video> resultHolder) {
        if (resultHolder.isError()) {
            loadingVideosActions.loadingCorrupted();
        } else {
            loadingVideosActions.loadingFinished(resultHolder.results());
        }
    }
}
