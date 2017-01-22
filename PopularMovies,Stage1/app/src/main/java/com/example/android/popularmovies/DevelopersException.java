package com.example.android.popularmovies;

/**
 * Created by kacper on 22.01.17.
 */

public class DevelopersException extends RuntimeException{

    public DevelopersException(Throwable cause) {
        super(cause);
    }

    public DevelopersException(String message, Throwable cause) {
        super(message, cause);
    }
}
