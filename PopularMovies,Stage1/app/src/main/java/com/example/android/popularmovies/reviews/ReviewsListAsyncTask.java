package com.example.android.popularmovies.reviews;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.AsyncLoadingListActions;
import com.example.android.popularmovies.ListResultHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kacper on 24.03.17.
 */

public class ReviewsListAsyncTask extends AsyncTask<String, Void, ListResultHolder<Review>> {

    private final AsyncLoadingListActions<Review> loadingReviewsActions;

    public ReviewsListAsyncTask(AsyncLoadingListActions<Review> loadingReviewsActions) {

        this.loadingReviewsActions = loadingReviewsActions;
    }

    @Override
    protected void onPreExecute() {
        loadingReviewsActions.loadingStarted();
    }

    @Override
    protected ListResultHolder<Review> doInBackground(String... params) {
        if (params == null || params.length < 2) {
            return null;
        }
        String apiKey = params[0];
        String movieId = params[1];
        try {
            List<Review> reviews = ReviewsApiUtils.fetchVideosForMovie(apiKey, movieId);
            ListResultHolder<Review> moviesResultHolder = new ListResultHolder<>(reviews);
            Log.e(ReviewsListAsyncTask.class.getSimpleName(), Arrays.toString(reviews.toArray()));
            return moviesResultHolder;
        } catch (ReviewsApiUtils.ReviewProvidingException e) {
            return new ListResultHolder<>(e);
        }
    }

    @Override
    protected void onPostExecute(ListResultHolder<Review> resultHolder) {
        if (resultHolder.isError()) {
            loadingReviewsActions.loadingCorrupted();
        } else {
            loadingReviewsActions.loadingFinished(resultHolder.results());
        }
    }
}
