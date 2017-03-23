package com.example.android.popularmovies.videos;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.MoviesApiUtils;
import com.example.android.popularmovies.SortingMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kacper on 23.03.17.
 */

public class VideosApiUtils {

    private static final String TAG = VideosApiUtils.class.getSimpleName();


    public static List<Video> fetchVideosForMovie(String apiKey, String movieId) throws VideoProvidingException {
        URL videosUrl = buildVideosForMovieUrl(apiKey, movieId);
        try {
            String response = MoviesApiUtils.getResponseFromHttpUrl(videosUrl);
            return parseJsonToVideos(response);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Cannot receive correct movie data from api", e);
            throw new VideoProvidingException(e);
        }
    }

    private static URL buildVideosForMovieUrl(String apiKey, String movieId) {
        Uri videosUri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .path("/3/movie/")
                .appendEncodedPath(movieId)
                .appendEncodedPath("videos")
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

    private static List<Video> parseJsonToVideos(String json) throws JSONException {
        Log.e(VideosApiUtils.TAG ,json);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");
        List<Video> videos = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            videos.add(jsonObjectToVideo(results.getJSONObject(i)));
        }
        return videos;
    }

    private static Video jsonObjectToVideo(JSONObject singleVideoJsonObject) throws JSONException {
        Video video = new Video();
        video.setKey(singleVideoJsonObject.getString("key"));
        video.setName(singleVideoJsonObject.getString("name"));
        video.setSite(singleVideoJsonObject.getString("site"));
        return video;
    }

    public static class VideoProvidingException extends Exception {
        public VideoProvidingException(Throwable cause) {
            super(cause);
        }
    }
}
