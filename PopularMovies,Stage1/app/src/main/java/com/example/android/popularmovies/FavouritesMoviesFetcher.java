package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.android.popularmovies.data.FavouriteMoviesContract;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class FavouritesMoviesFetcher {

    private final ContentResolver contentResolver;

    public FavouritesMoviesFetcher(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Set<FavouriteMovie> fetchAll() {
        Cursor cursor = contentResolver.query(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, null, null, null, null);
        Set<FavouriteMovie> result = new HashSet<>();
        try {
            while (cursor.moveToNext()) {
                long movieId = cursor.getLong(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_TITLE));
                result.add(new FavouriteMovie(movieId, movieTitle));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public Set<FavouriteMovie> fetchByIds(final Set<Long> ids) {
        Set<FavouriteMovie> favouriteMovies = fetchAll();
        return Sets.newHashSet(
                Iterables.filter(favouriteMovies, new MovieIdsPredicate(ids)));
    }

    static class MovieIdsPredicate implements Predicate<FavouriteMovie> {

        private final Set<Long> ids;

        public MovieIdsPredicate(Set<Long> ids) {
            this.ids = ids;
        }

        @Override
        public boolean apply(FavouriteMovie input) {
            return ids.contains(input.getId());
        }
    }
}
