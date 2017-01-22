package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kacper on 22.01.17.
 */
public class MoviesApiUtils {

    private static final String TAG = MoviesApiUtils.class.getSimpleName();


    public static List<Movie> fetchFirstPage(String apiKey, SortingMode sortingMode) throws MovieProvidingException {
        URL firstMoviesPageUrl = buildFirstMoviesPageUrl(apiKey, sortingMode);
        try {
            String response = getResponseFromHttpUrl(firstMoviesPageUrl);
            return parseJsonToMovies(response);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Cannot receive correct movie data from api", e);
            throw new MovieProvidingException(e);
        }
    }

    private static String resolveSortingModeToPath(SortingMode sortingMode) {
        if (sortingMode == SortingMode.MOST_POPULAR) return "popular";
        if (sortingMode == SortingMode.TOP_RATED) return "top_rated";
        throw new IllegalArgumentException("Given sorting mode is illegal: " + sortingMode);
    }

    private static URL buildFirstMoviesPageUrl(String apiKey, SortingMode sortingMode) {
        Uri moviesUri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .path("/3/movie/")
                .appendEncodedPath(resolveSortingModeToPath(sortingMode))
                .appendQueryParameter("api_key", apiKey)
                .build();
        String moviesUriValue = moviesUri.toString();
        try {
            return new URL(moviesUriValue);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Cannot create url", e);
            throw new RuntimeException("Cannot create url, should never happen", e);
        }
    }

    private static List<Movie> parseJsonToMovies(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");
        List<Movie> movies = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            movies.add(jsonObjectToMovie(results.getJSONObject(i)));
        }
        return movies;
    }

    private static Movie jsonObjectToMovie(JSONObject singleMovieJsonObject) throws JSONException {
        Movie movie = new Movie();
        movie.setOriginalTitle(singleMovieJsonObject.getString("title"));
        movie.setPosterPath(singleMovieJsonObject.getString("poster_path"));
        movie.setOverview(singleMovieJsonObject.getString("overview"));
        movie.setReleaseDate(singleMovieJsonObject.getString("release_date"));
        movie.setVoteAverage(singleMovieJsonObject.getDouble("vote_average"));
        return movie;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri buildPosterImageUri(String posterName) {
        Uri uri = new Uri.Builder()
                .scheme("http").authority("image.tmdb.org")
                .path("/t/p/w185/").appendEncodedPath(posterName).build();
        return uri;

    }

    public static class MovieProvidingException extends Exception {
        public MovieProvidingException(Throwable cause) {
            super(cause);
        }
    }
}
