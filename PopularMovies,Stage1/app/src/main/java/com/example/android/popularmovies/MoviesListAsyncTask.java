package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class MoviesListAsyncTask extends AsyncTask<String, Void, ListResultHolder<Movie>> {

    private final AsyncLoadingListActions<Movie> asyncLoadingListActions;
    private final SortingMode sortingMode;
    private final FavouritesMoviesFetcher moviesFetcher;

    public MoviesListAsyncTask(AsyncLoadingListActions<Movie> asyncLoadingListActions, SortingMode sortingMode, FavouritesMoviesFetcher moviesFetcher) {
        this.asyncLoadingListActions = asyncLoadingListActions;
        this.sortingMode = sortingMode;
        this.moviesFetcher = moviesFetcher;
    }

    @Override
    protected void onPreExecute() {
        asyncLoadingListActions.loadingStarted();
    }

    @Override
    protected ListResultHolder<Movie> doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        String apiKey = params[0];
        try {
            if (sortingMode == SortingMode.FAVOURITES) {
                Set<Movie> movies = moviesFetcher.fetchAll();
                return new ListResultHolder<>(new ArrayList<>(movies));
            }
            List<Movie> movies = MoviesApiUtils.fetchFirstPage(apiKey, sortingMode);
            Set<Movie> favouriteMovies = moviesFetcher.fetchByIds(moviesToIds(movies));
            ListResultHolder<Movie> moviesResultHolder = new ListResultHolder<>(setAsFavourite(movies, favouriteMovies));
            return moviesResultHolder;
        } catch (MoviesApiUtils.MovieProvidingException e) {
            return new ListResultHolder<>(e);
        }
    }

    @Override
    protected void onPostExecute(ListResultHolder<Movie> resultHolder) {
        if (resultHolder.isError()) {
            asyncLoadingListActions.loadingCorrupted();
        } else {
            asyncLoadingListActions.loadingFinished(resultHolder.results());
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

    private Set<Long> favouriteMoviesToIds(Set<Movie> favouriteMovies) {
        Iterable<Long> ids = Iterables.transform(favouriteMovies, new Function<Movie, Long>() {

            @Override
            public Long apply(Movie input) {
                return input.getId();
            }
        });
        return Sets.newHashSet(ids);
    }

    private List<Movie> setAsFavourite(List<Movie> movies, Set<Movie> favouriteMovies) {
        Set<Long> ids = favouriteMoviesToIds(favouriteMovies);
        for(Movie movie: movies) {
            if (ids.contains(movie.getId())) {
                movie.setFavourite(true);
            }
        }
        return movies;
    }
}
