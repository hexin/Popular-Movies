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

    public Set<Movie> fetchAll() {
        Cursor cursor = contentResolver.query(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, null, null, null, null);
        Set<Movie> result = new HashSet<>();
        try {
            while (cursor.moveToNext()) {
                result.add(cursorToMovie(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private Movie cursorToMovie(Cursor cursor) {
        long movieId = cursor.getLong(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID));
        String originalTitle = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE));
        String posterPath = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH));
        String overview = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_OVERVIEW));
        String releaseDate = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE));
        double voteAverage = cursor.getDouble(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG));
        boolean favourite = true;
        Movie movie = new Movie();
        movie.setFavourite(favourite);
        movie.setVoteAverage(voteAverage);
        movie.setReleaseDate(releaseDate);
        movie.setPosterPath(posterPath);
        movie.setOverview(overview);
        movie.setOriginalTitle(originalTitle);
        movie.setId(movieId);
        return movie;
    }

    public Set<Movie> fetchByIds(final Set<Long> ids) {
        Set<Movie> favouriteMovies = fetchAll();
        return Sets.newHashSet(
                Iterables.filter(favouriteMovies, new MovieIdsPredicate(ids)));
    }

    static class MovieIdsPredicate implements Predicate<Movie> {

        private final Set<Long> ids;

        public MovieIdsPredicate(Set<Long> ids) {
            this.ids = ids;
        }

        @Override
        public boolean apply(Movie input) {
            return ids.contains(input.getId());
        }
    }
}
