    package com.example.android.popularmovies;

    import android.content.ContentValues;
    import android.content.Intent;
    import android.net.Uri;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.example.android.popularmovies.data.FavouriteMoviesContract;
    import com.squareup.picasso.Picasso;

    public class MovieDetailsActivity extends AppCompatActivity {

        public static final String EXTRA_MOVIE = "extra_movie";
        public static final int RESULT_CODE_OK = 121;

            TextView textView;
            ImageView mPosterImageView;
            TextView mReleaseDateTextView;
            TextView mAverageScoreTextView;
            TextView mOverviewTextView;
            ImageView mFavouriteMovieImageView;
            Movie movie;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_movie_details);
            movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            Log.e(MovieDetailsActivity.class.getSimpleName(), movie.toString());

            textView = (TextView) findViewById(R.id.tv_title);
            textView.setText(movie.getOriginalTitle());

            mPosterImageView = (ImageView) findViewById(R.id.image_details_poster);
            Picasso.with(this)
                    .load(MoviesApiUtils.buildPosterImageUri(movie.getPosterPath()))
                    .placeholder(R.drawable.loading)
                    .into(mPosterImageView);

            mReleaseDateTextView = (TextView) findViewById(R.id.tv_details_date);
            mReleaseDateTextView.setText(movie.getReleaseDate());

            mAverageScoreTextView = (TextView) findViewById(R.id.tv_details_avgscore);
            mAverageScoreTextView.setText(Double.toString(movie.getVoteAverage()));

            mFavouriteMovieImageView = (ImageView) findViewById(R.id.iv_details_favourites);
            setFavouriteImageView(movie);
            mFavouriteMovieImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movie.setFavourite(!movie.isFavourite());
                    setMovieResult(movie);
                    setFavouriteImageView(movie);
                }
            });

            mOverviewTextView = (TextView) findViewById(R.id.tv_details_overview);
            mOverviewTextView.setText(movie.getOverview());
        }

//        @Override
//        public void onBackPressed() {
//            Intent intent = new Intent();
//            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
//            setResult(RESULT_CODE_OK, intent);
//            onBackPressed();
//        }

        private void setMovieResult(Movie movie) {
            Intent intent = new Intent();
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
            setResult(RESULT_CODE_OK, intent);
        }

        private void setFavouriteImageView(Movie movie) {
            FavouriteMovieUpdater favouriteMovieUpdater = new FavouriteMovieUpdater();
            if (movie.isFavourite()) {
                mFavouriteMovieImageView.setImageResource(R.drawable.ic_power_pink_80px);
                favouriteMovieUpdater.addToFavourites(movie);
            } else {
                mFavouriteMovieImageView.setImageResource(R.drawable.ic_power_grey_80px);
                favouriteMovieUpdater.removeFromFavourites(movie);
            }
        }

        private class FavouriteMovieUpdater {

            public void addToFavourites(Movie movie) {
                ContentValues values = new ContentValues();
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_TITLE, movie.getOriginalTitle());
                getContentResolver().insert(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, values);
            }

            public void removeFromFavourites(Movie movie) {
                String stringId = Long.toString(movie.getId());
                Uri uri = FavouriteMoviesContract.FavouriteMoviesEntry
                        .CONTENT_URI.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);
            }
        }
    }
