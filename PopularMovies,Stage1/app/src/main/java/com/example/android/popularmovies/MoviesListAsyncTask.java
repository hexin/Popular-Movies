package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

class MoviesListAsyncTask extends AsyncTask<String, Void, MoviesResultHolder> {

    private final LoadingMoviesActions loadingMoviesActions;
    private final SortingMode sortingMode;
    private final FavouritesMoviesFetcher moviesFetcher;

    public MoviesListAsyncTask(LoadingMoviesActions loadingMoviesActions, SortingMode sortingMode, FavouritesMoviesFetcher moviesFetcher) {
        this.loadingMoviesActions = loadingMoviesActions;
        this.sortingMode = sortingMode;
        this.moviesFetcher = moviesFetcher;
    }

    @Override
    protected void onPreExecute() {
        loadingMoviesActions.loadingStarted();
    }

    @Override
    protected MoviesResultHolder doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        String apiKey = params[0];
        try {
            List<Movie> movies = MoviesApiUtils.fetchFirstPage(apiKey, sortingMode);
            Set<FavouriteMovie> favouriteMovies = moviesFetcher.fetchByIds(moviesToIds(movies));
            MoviesResultHolder moviesResultHolder = new MoviesResultHolder(setAsFavourite(movies, favouriteMovies));
            logFavouriteMovies(favouriteMovies);//TODO remove
            return moviesResultHolder;
        } catch (MoviesApiUtils.MovieProvidingException e) {
            return new MoviesResultHolder(e);
        }
    }

    @Override
    protected void onPostExecute(MoviesResultHolder resultHolder) {
        if (resultHolder.isError()) {
            loadingMoviesActions.loadingCorrupted();
        } else {
            loadingMoviesActions.loadingFinished(resultHolder.getMovies());
        }
    }

    private Set<Long> moviesToIds(List<Movie> movies) {
        Iterable<Long> ids = Iterables.transform(movies, new Function<Movie, Long>() {

            @Override
            public Long apply(Movie input) {
                return input.getId();
            }
        });
        return Sets.newHashSet(ids);
    }

    private Set<Long> favouriteMoviesToIds(Set<FavouriteMovie> favouriteMovies) {
        Iterable<Long> ids = Iterables.transform(favouriteMovies, new Function<FavouriteMovie, Long>() {

            @Override
            public Long apply(FavouriteMovie input) {
                return input.getId();
            }
        });
        return Sets.newHashSet(ids);
    }

    private List<Movie> setAsFavourite(List<Movie> movies, Set<FavouriteMovie> favouriteMovies) {
        Set<Long> ids = favouriteMoviesToIds(favouriteMovies);
        for(Movie movie: movies) {
            if (ids.contains(movie.getId())) {
                movie.setFavourite(true);
            }
        }
        return movies;
    }

    private void logFavouriteMovies(Set<FavouriteMovie> favouriteMovies) {
        String toString = Arrays.toString(favouriteMovies.toArray());
        Log.e(MoviesListAsyncTask.class.getSimpleName(), toString);
    }
}
