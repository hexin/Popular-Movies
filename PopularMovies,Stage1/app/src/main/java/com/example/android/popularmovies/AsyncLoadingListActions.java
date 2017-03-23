package com.example.android.popularmovies;

import java.util.List;

public interface AsyncLoadingListActions<T> {

    void loadingStarted();

    void loadingCorrupted();

    void loadingFinished(List<T> results);
}
