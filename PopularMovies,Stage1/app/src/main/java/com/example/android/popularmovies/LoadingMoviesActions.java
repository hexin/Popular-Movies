package com.example.android.popularmovies;

import java.util.List;

interface LoadingMoviesActions {

    void loadingStarted();

    void loadingCorrupted();

    void loadingFinished(List<Movie> movies);
}
