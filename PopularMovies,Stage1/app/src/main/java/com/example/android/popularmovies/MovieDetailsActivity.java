    package com.example.android.popularmovies;

    import android.content.ContentValues;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Parcelable;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.support.v7.widget.LinearLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import com.example.android.popularmovies.data.FavouriteMoviesContract;
    import com.example.android.popularmovies.videos.Video;
    import com.example.android.popularmovies.videos.VideosAdapter;
    import com.example.android.popularmovies.videos.VideosListAsyncTask;
    import com.squareup.picasso.Picasso;

    import java.util.List;

    public class MovieDetailsActivity extends AppCompatActivity implements VideosAdapter.VideoOnClickHandler {

        public static final String EXTRA_MOVIE = "extra_movie";
        private static final String LAYOUT_MANAGER_KEY = "layoutManager";
        public static final int RESULT_CODE_OK = 121;

        private TextView textView;
        private ImageView mPosterImageView;
        private TextView mReleaseDateTextView;
        private TextView mAverageScoreTextView;
        private TextView mOverviewTextView;
        private ImageView mFavouriteMovieImageView;
        private Movie movie;
        private RecyclerView mRecyclerView;
        private VideosAdapter mVideosAdapter;
        private LinearLayoutManager mLayoutManager;
        private ProgressBar mProgressBar;
        private TextView favouritesTextView;


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

            favouritesTextView = (TextView) findViewById(R.id.tv_details_favourites);
            mFavouriteMovieImageView = (ImageView) findViewById(R.id.iv_details_favourites);
            setFavouriteViews(movie);
            mFavouriteMovieImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movie.setFavourite(!movie.isFavourite());
                    setMovieResult(movie);
                    setFavouriteViews(movie);
                    changeFavoriteMovie(movie);
                }
            });

            mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

            mOverviewTextView = (TextView) findViewById(R.id.tv_details_overview);
            mOverviewTextView.setText(movie.getOverview());

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_videos);
            mRecyclerView.setAdapter(mVideosAdapter = new VideosAdapter(this));
            mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
            restoreLayoutManagerStateIfNecessary(savedInstanceState);
            new VideosListAsyncTask(new VideoLoadingActions()).execute(ThemoviedbApiKey.API_KEY, String.valueOf(movie.getId()));
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(LAYOUT_MANAGER_KEY, mLayoutManager.onSaveInstanceState());
        }

        private void restoreLayoutManagerStateIfNecessary(Bundle savedInstanceState) {
            if (savedInstanceState != null && savedInstanceState.containsKey(LAYOUT_MANAGER_KEY)) {
                Parcelable parcelable = savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY);
                mLayoutManager.onRestoreInstanceState(parcelable);
            }
        }

        private void setMovieResult(Movie movie) {
            Intent intent = new Intent();
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
            setResult(RESULT_CODE_OK, intent);
        }

        private void setFavouriteViews(Movie movie) {
            if (movie.isFavourite()) {
                mFavouriteMovieImageView.setImageResource(R.drawable.ic_power_pink_80px);
                favouritesTextView.setText(getString(R.string.remove_from_favourites));
            } else {
                mFavouriteMovieImageView.setImageResource(R.drawable.ic_power_grey_80px);
                favouritesTextView.setText(getString(R.string.add_to_favourites));
            }
        }

        private void changeFavoriteMovie(Movie movie) {
            FavouriteMovieUpdater favouriteMovieUpdater = new FavouriteMovieUpdater();
            if (movie.isFavourite()) {
                favouriteMovieUpdater.addToFavourites(movie);
            } else {
                favouriteMovieUpdater.removeFromFavourites(movie);
            }
        }

        private void showVideosLoadingError() {
//            mErrorView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }

        private void showLoadedVideos() {
//            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        private void showVideosLoadingProgressBar() {
//            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVideoClick(Video video) {
            watchYoutubeVideo(video.getKey());
        }

        public static void watchYoutubeVideo(String key){
//            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
//            Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://www.youtube.com/watch?v=" + id));
//            try {
//                startActivity(appIntent);
//            } catch (ActivityNotFoundException ex) {
//                startActivity(webIntent);
//            }
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

        private class VideoLoadingActions implements AsyncLoadingListActions<Video> {

            @Override
            public void loadingStarted() {
                showVideosLoadingProgressBar();
            }

            @Override
            public void loadingCorrupted() {
                showVideosLoadingError();
            }

            @Override
            public void loadingFinished(List<Video> results) {
                mVideosAdapter.setVideos(results);
                showLoadedVideos();
            }
        }
    }
