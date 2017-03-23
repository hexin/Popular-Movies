package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kacper on 23.03.17.
 */

public class FavouriteMoviesProvider extends ContentProvider {

    public static final int CODE_FAVOURITE_MOVIES = 100;
    public static final int CODE_FAVOURITE_MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavouriteMoviesDBHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavouriteMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, FavouriteMoviesContract.PATH_FAVOURITE, CODE_FAVOURITE_MOVIES);
        matcher.addURI(authority, FavouriteMoviesContract.PATH_FAVOURITE + "/#", CODE_FAVOURITE_MOVIES_WITH_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new FavouriteMoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE_MOVIES:
                long id = mOpenHelper.getWritableDatabase()
                        .insert(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    result = createInsertedMovieUri(values);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.e(FavouriteMoviesProvider.class.getSimpleName(), result.toString());
        return result;
    }

    private Uri createInsertedMovieUri(ContentValues values) {
        return ContentUris.withAppendedId(
                        FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                        values.getAsLong(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE_MOVIES_WITH_ID:
                selection = FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ";
                String id = uri.getLastPathSegment();
                int affectedRowsAmount = mOpenHelper.getWritableDatabase()
                        .delete(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                                selection, new String[]{id});
//                validateDeletedRowsAmount(affectedRowsAmount, "Failed to delete favourites movie row for id " + id + ", affected row were " + affectedRowsAmount);
                getContext().getContentResolver().notifyChange(uri, null);
                return affectedRowsAmount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void validateDeletedRowsAmount(int affectedRowsAmount, String errorMessage) {
        if (affectedRowsAmount != 1) {
//            throw new android.database.SQLException(errorMessage);
            Log.e(FavouriteMoviesProvider.class.getSimpleName(), errorMessage);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not supported for uri: " + uri);
    }
}
