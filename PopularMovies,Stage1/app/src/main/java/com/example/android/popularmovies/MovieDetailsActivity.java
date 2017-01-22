package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

        TextView textView;
        ImageView mPosterImageView;
        TextView mReleaseDateTextView;
        TextView mAverageScoreTextView;
        TextView mOverviewTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

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

        mOverviewTextView = (TextView) findViewById(R.id.tv_details_overview);
        mOverviewTextView.setText(movie.getOverview());
    }
}
