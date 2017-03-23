package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry;

public class FavouriteMoviesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favouriteMovies.db";
    private static final int DATABASE_VERSION = 2;

    public FavouriteMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + FavouriteMoviesEntry.TABLE_NAME + " (" +
                        FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        FavouriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        FavouriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        FavouriteMoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG + " REAL NOT NULL, " +
                        " UNIQUE (" + FavouriteMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
