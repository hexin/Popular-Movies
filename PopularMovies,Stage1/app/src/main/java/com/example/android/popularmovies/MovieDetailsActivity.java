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
    import com.example.android.popularmovies.reviews.Review;
    import com.example.android.popularmovies.reviews.ReviewsAdapter;
    import com.example.android.popularmovies.reviews.ReviewsListAsyncTask;
    import com.example.android.popularmovies.videos.Video;
    import com.example.android.popularmovies.videos.VideosAdapter;
    import com.example.android.popularmovies.videos.VideosListAsyncTask;
    import com.squareup.picasso.Picasso;

    import java.util.List;

    public class MovieDetailsActivity extends AppCompatActivity implements VideosAdapter.VideoOnClickHandler {

        public static final String EXTRA_MOVIE = "extra_movie";
        private static final String VIDEOS_LAYOUT_MANAGER_KEY = "videosLayoutManager";
        private static final String REVIEWS_LAYOUT_MANAGER_KEY = "reviewsLayoutManager";
        private static final String MOVIE_KEY = "movie";
        public static final int RESULT_CODE_OK = 121;

        private TextView textView;
        private ImageView mPosterImageView;
        private TextView mReleaseDateTextView;
        private TextView mAverageScoreTextView;
        private TextView mOverviewTextView;
        private ImageView mFavouriteMovieImageView;
        private Movie movie;
        private RecyclerView mVideosRecyclerView;
        private VideosAdapter mVideosAdapter;
        private LinearLayoutManager mVideosLayoutManager;
        private ProgressBar mLoadingVideosProgressBar;
        private TextView favouritesTextView;
        private RecyclerView mReviewsRecyclerView;
        private ReviewsAdapter mReviewsAdapter;
        private LinearLayoutManager mReviewsLayoutManager;
        private ProgressBar mLoadingReviewsProgressBar;


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

            mLoadingVideosProgressBar = (ProgressBar) findViewById(R.id.progressbar_videos_loading);

            mOverviewTextView = (TextView) findViewById(R.id.tv_details_overview);
            mOverviewTextView.setText(movie.getOverview());

            mVideosRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_videos);
            mVideosRecyclerView.setAdapter(mVideosAdapter = new VideosAdapter(this));
            mVideosRecyclerView.setLayoutManager(mVideosLayoutManager = new LinearLayoutManager(this));

            mLoadingReviewsProgressBar = (ProgressBar) findViewById(R.id.progressbar_reviews_loading);
            mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
            mReviewsRecyclerView.setAdapter(mReviewsAdapter = new ReviewsAdapter());
            mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager = new LinearLayoutManager(this));

            restoreLayoutManagerStateIfNecessary(savedInstanceState);
            new VideosListAsyncTask(new VideoLoadingActions()).execute(ThemoviedbApiKey.API_KEY, String.valueOf(movie.getId()));
            new ReviewsListAsyncTask(new ReviewLoadingActions()).execute(ThemoviedbApiKey.API_KEY, String.valueOf(movie.getId()));
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(VIDEOS_LAYOUT_MANAGER_KEY, mVideosLayoutManager.onSaveInstanceState());
            outState.putParcelable(REVIEWS_LAYOUT_MANAGER_KEY, mReviewsLayoutManager.onSaveInstanceState());
            outState.putParcelable(MOVIE_KEY, movie);
        }

        private void restoreLayoutManagerStateIfNecessary(Bundle savedInstanceState) {
            if (savedInstanceState == null) {
                return;
            }
            if (savedInstanceState.containsKey(VIDEOS_LAYOUT_MANAGER_KEY)) {
                Parcelable videosParcelable = savedInstanceState.getParcelable(VIDEOS_LAYOUT_MANAGER_KEY);
                mVideosLayoutManager.onRestoreInstanceState(videosParcelable);

            }
            if (savedInstanceState.containsKey(REVIEWS_LAYOUT_MANAGER_KEY)){
                Parcelable reviewsParcelable = savedInstanceState.getParcelable(REVIEWS_LAYOUT_MANAGER_KEY);
                mReviewsLayoutManager.onRestoreInstanceState(reviewsParcelable);
            }
            if (savedInstanceState.containsKey(MOVIE_KEY)){
                movie = savedInstanceState.getParcelable(MOVIE_KEY);
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
            mVideosRecyclerView.setVisibility(View.GONE);
            mLoadingVideosProgressBar.setVisibility(View.GONE);
        }

        private void showLoadedVideos() {
//            mErrorView.setVisibility(View.GONE);
            mVideosRecyclerView.setVisibility(View.VISIBLE);
            mLoadingVideosProgressBar.setVisibility(View.GONE);
        }

        private void showVideosLoadingProgressBar() {
//            mErrorView.setVisibility(View.GONE);
            mVideosRecyclerView.setVisibility(View.GONE);
            mLoadingVideosProgressBar.setVisibility(View.VISIBLE);
        }

        private void showLoadedReviews() {
//            mErrorView.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
            mLoadingReviewsProgressBar.setVisibility(View.GONE);
        }

        private void showReviewLoadingError() {
//            mErrorView.setVisibility(View.GONE);
            mReviewsRecyclerView.setVisibility(View.GONE);
            mLoadingReviewsProgressBar.setVisibility(View.GONE);
        }

        private void showReviewLoadingProgressBar() {
//            mErrorView.setVisibility(View.GONE);
            mReviewsRecyclerView.setVisibility(View.GONE);
            mLoadingReviewsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVideoClick(Video video) {
            watchYoutubeVideo(video.getKey());
        }

        private void watchYoutubeVideo(String key){
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + key));
                startActivity(webIntent);
        }

        private class FavouriteMovieUpdater {

            public void addToFavourites(Movie movie) {
                ContentValues values = new ContentValues();
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
                values.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG, movie.getVoteAverage());
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

        private class ReviewLoadingActions implements AsyncLoadingListActions<Review> {

            @Override
            public void loadingStarted() {
                showReviewLoadingProgressBar();
            }

            @Override
            public void loadingCorrupted() {
                showReviewLoadingError();
            }

            @Override
            public void loadingFinished(List<Review> results) {
                mReviewsAdapter.setReviews(results);
                showLoadedReviews();
            }
        }
    }
