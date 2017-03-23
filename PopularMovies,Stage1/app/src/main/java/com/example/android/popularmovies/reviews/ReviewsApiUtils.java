package com.example.android.popularmovies.reviews;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.MoviesApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsApiUtils {

    private static final String TAG = ReviewsApiUtils.class.getSimpleName();


    public static List<Review> fetchVideosForMovie(String apiKey, String movieId) throws ReviewProvidingException {
        URL videosUrl = buildReviewsForMovieUrl(apiKey, movieId);
        try {
            String response = MoviesApiUtils.getResponseFromHttpUrl(videosUrl);
            return parseJsonToReviews(response);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Cannot receive correct movie data from api", e);
            throw new ReviewProvidingException(e);
        }
    }

    private static URL buildReviewsForMovieUrl(String apiKey, String movieId) {
        Uri videosUri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .path("/3/movie/")
                .appendEncodedPath(movieId)
                .appendEncodedPath("reviews")
                .appendQueryParameter("api_key", apiKey)
                .build();
        String videosUriValue = videosUri.toString();
        try {
            return new URL(videosUriValue);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Cannot create url", e);
            throw new RuntimeException("Cannot create url, should never happen", e);
        }
    }

    private static List<Review> parseJsonToReviews(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");
        List<Review> reviews = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            reviews.add(jsonObjectToVideo(results.getJSONObject(i)));
        }
        return reviews;
    }

    private static Review jsonObjectToVideo(JSONObject singleVideoJsonObject) throws JSONException {
        Review review = new Review();
        review.setAuthor(singleVideoJsonObject.getString("author"));
        review.setContent(singleVideoJsonObject.getString("content"));
        return review;
    }

    public static class ReviewProvidingException extends Exception {
        public ReviewProvidingException(Throwable cause) {
            super(cause);
        }
    }
}
