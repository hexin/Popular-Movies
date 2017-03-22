package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
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

public class MoviesActivity extends AppCompatActivity implements MoviesAdapter.PosterOnClickHandler, LoadingMoviesActions {

    private static final String API_KEY = "";
    private static final String CURRENT_SORTING_MODE_KEY = "currentSortingMode";
    private static final String LAYOUT_MANAGER_KEY = "layoutManager";
    private static final int REQUEST_CODE = 1;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private SortingMode mCurrentSortingMode;
    private View mErrorView;
    private ProgressBar mProgressBar;
    private Button mRefreshButton;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mCurrentSortingMode = createSortingModeIfNecessary(savedInstanceState);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mRecyclerView.setAdapter(mMoviesAdapter = new MoviesAdapter(this));
        mRecyclerView.setLayoutManager(mLayoutManager = new GridLayoutManager(this, 2));
        restoreLayoutManagerStateIfNecessary(savedInstanceState);

        mErrorView = findViewById(R.id.error_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mRefreshButton = (Button) findViewById(R.id.btn_refresh);
        setOnRefreshClickListener(mRefreshButton);
        refreshMoviesFromApi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == MovieDetailsActivity.RESULT_CODE_OK) {
            Movie movie = data.getParcelableExtra(MovieDetailsActivity.EXTRA_MOVIE);
            mMoviesAdapter.updateMovieById(movie);
        }
    }

    private void restoreLayoutManagerStateIfNecessary(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LAYOUT_MANAGER_KEY)) {
            Parcelable parcelable = savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY);
            mLayoutManager.onRestoreInstanceState(parcelable);
        }
    }

    private SortingMode createSortingModeIfNecessary(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(CURRENT_SORTING_MODE_KEY)) {
            return SortingMode.TOP_RATED;
        } else {
            return (SortingMode) savedInstanceState.getSerializable(CURRENT_SORTING_MODE_KEY);
        }
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
            new MoviesListAsyncTask(this, mCurrentSortingMode, new FavouritesMoviesFetcher(getContentResolver())).execute(API_KEY);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_SORTING_MODE_KEY, mCurrentSortingMode);
        outState.putParcelable(LAYOUT_MANAGER_KEY, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onPosterClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivityForResult(intent, REQUEST_CODE);
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

    @Override
    public void loadingStarted() {
        showProgressBar();
    }

    @Override
    public void loadingCorrupted() {
        showError();
    }

    @Override
    public void loadingFinished(List<Movie> movies) {
        mMoviesAdapter.setMovies(movies);
        showPosters();
    }

}
