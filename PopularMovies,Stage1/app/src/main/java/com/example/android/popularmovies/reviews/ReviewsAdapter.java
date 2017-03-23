package com.example.android.popularmovies.reviews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>{

    private List<Review> reviews;
//    private ReviewsAdapter.ReviewOnClickHandler mReviewOnClickHandler;

//    public ReviewsAdapter(ReviewsAdapter.ReviewOnClickHandler reviewOnClickHandler) {
//        this(new ArrayList<Review>(), reviewOnClickHandler);
//    }
//
//    public ReviewsAdapter(List<Review> reviews, ReviewsAdapter.ReviewOnClickHandler reviewOnClickHandler) {
//        this.reviews = new ArrayList<>(reviews);
//        this.mReviewOnClickHandler = reviewOnClickHandler;
//    }

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = new ArrayList<>(reviews);
    }

    public ReviewsAdapter() {
        this(new ArrayList<Review>());
    }

    @Override
    public ReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_list_item, parent, attachToRoot);
        return new ReviewsAdapter.ReviewViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> newReviews) {
        this.reviews = new ArrayList<>(newReviews);
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mReviewAuthorTextView;
        private TextView mReviewContentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_reviews_author);
            mReviewContentTextView = (TextView) itemView.findViewById(R.id.tv_reviews_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            mReviewOnClickHandler.onReviewClick(Reviews.get(adapterPosition));
        }

        private void bind(Review review) {
            mReviewAuthorTextView.setText(review.getAuthor());
            mReviewContentTextView.setText(review.getContent());
        }
    }

    public interface ReviewOnClickHandler {
        void onReviewClick(Review Review);
    }
}
