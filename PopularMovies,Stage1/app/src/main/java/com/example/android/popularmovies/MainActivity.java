package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.PosterOnClickHandler {

    public static final String API_KEY = "";

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private SortingMode mCurrentSortingMode = SortingMode.TOP_RATED;
    private View mErrorView;
    private ProgressBar mProgressBar;
    private Button mRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mRecyclerView.setAdapter(mMoviesAdapter = new MoviesAdapter(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mErrorView = findViewById(R.id.error_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mRefreshButton = (Button) findViewById(R.id.btn_refresh);
        setOnRefreshClickListener(mRefreshButton);
        refreshMoviesFromApi();
    }

    private void setOnRefreshClickListener(Button refreshButton) {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMoviesFromApi();
            }
        });
    }

    private void refreshMoviesFromApi() {
        if (isOnline()) {
            new MoviesListAsyncTask().execute(API_KEY);
        } else {
            showError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings_sorting) {
            MenuItem mostPopularMenuItem = item.getSubMenu().findItem(R.id.menu_settings_sorting_mostpopular);
            mostPopularMenuItem.setEnabled(mCurrentSortingMode != SortingMode.MOST_POPULAR);
            MenuItem topRatedMenuItem = item.getSubMenu().findItem(R.id.menu_settings_sorting_toprated);
            topRatedMenuItem.setEnabled(mCurrentSortingMode != SortingMode.TOP_RATED);
        } else if (itemId == R.id.menu_settings_sorting_mostpopular) {
            mCurrentSortingMode = SortingMode.MOST_POPULAR;
            refreshMoviesFromApi();
        } else if (itemId == R.id.menu_settings_sorting_toprated) {
            mCurrentSortingMode = SortingMode.TOP_RATED;
            refreshMoviesFromApi();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosterClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showPosters() {
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class MoviesListAsyncTask extends AsyncTask<String, Void, MoviesResultHolder> {

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected MoviesResultHolder doInBackground(String... params) {
            if (params == null || params.length == 0) {
                return null;
            }
            String apiKey = params[0];
            try {
                return new MoviesResultHolder(MoviesApiUtils.fetchFirstPage(apiKey, mCurrentSortingMode));
            } catch (MoviesApiUtils.MovieProvidingException e) {
                return new MoviesResultHolder(e);
            }
        }

        @Override
        protected void onPostExecute(MoviesResultHolder resultHolder) {
            if (resultHolder.isError()) {
                showError();
            } else {
                mMoviesAdapter.setMovies(resultHolder.getMovies());
                showPosters();
            }
        }
    }

    private class MoviesResultHolder {

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
}
