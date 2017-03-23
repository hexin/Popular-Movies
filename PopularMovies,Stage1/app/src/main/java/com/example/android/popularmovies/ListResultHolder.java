package com.example.android.popularmovies;

import java.util.List;

public class ListResultHolder<T> {

    private Exception exception;
    private List<T> results;

    public ListResultHolder(Exception exception) {
        this.exception = exception;
    }

    public ListResultHolder(List<T> results) {
        this.results = results;
    }

    public boolean isError() {
        return exception != null;
    }

    public List<T> results() {
        return results;
    }
}
