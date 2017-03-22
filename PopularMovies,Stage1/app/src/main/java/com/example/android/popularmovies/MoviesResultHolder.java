package com.example.android.popularmovies;

import java.util.List;

class MoviesResultHolder {

    private Exception exception;
    private List<Movie> movies;

    public MoviesResultHolder(Exception exception) {
        this.exception = exception;
    }

    public MoviesResultHolder(List<Movie> movies) {
        this.movies = movies;
    }

    boolean isError() {
        return exception != null;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
