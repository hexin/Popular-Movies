package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by kacper on 22.01.17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private List<Movie> movies;
    private PosterOnClickHandler mPosterOnClickHandler;

    public MoviesAdapter(PosterOnClickHandler posterOnClickHandler) {
        this(new ArrayList<Movie>(), posterOnClickHandler);
    }

    public MoviesAdapter(List<Movie> movies, PosterOnClickHandler posterOnClickHandler) {
        this.movies = new ArrayList<>(movies);
        this.mPosterOnClickHandler = posterOnClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_list_item, parent, attachToRoot);
        return new MovieViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> newMovies) {
        this.movies = new ArrayList<>(newMovies);
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

         private ImageView mPosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.image_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mPosterOnClickHandler.onPosterClick(movies.get(adapterPosition));
        }

        private void bind(Movie movie) {
            Picasso.with(itemView.getContext())
                    .load(MoviesApiUtils.buildPosterImageUri(movie.getPosterPath()))
                    .placeholder(R.drawable.loading)
                    .into(mPosterImageView);
        }
    }

    interface PosterOnClickHandler {
        void onPosterClick(Movie movie);
    }
}
