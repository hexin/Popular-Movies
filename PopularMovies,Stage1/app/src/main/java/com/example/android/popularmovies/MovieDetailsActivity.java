package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        TextView textView = (TextView) findViewById(R.id.tv_debug);
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        textView.setText(movie.getOriginalTitle());
        ImageView mPosterImageView = (ImageView) findViewById(R.id.image_details_poster);
        Picasso.with(this)
                .load(MoviesApiUtils.buildPosterImageUri(movie.getPosterPath()))
                .placeholder(R.drawable.loading)
                .into(mPosterImageView);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_details_date);
        mReleaseDateTextView.setText(movie.getReleaseDate());
        TextView mAverageScoreTextView = (TextView) findViewById(R.id.tv_details_avgscore);
        mAverageScoreTextView.setText(Double.toString(movie.getVoteAverage()));
        TextView mOverviewTextView = (TextView) findViewById(R.id.tv_details_overview);
        mOverviewTextView.setText(movie.getOverview());
    }
}
