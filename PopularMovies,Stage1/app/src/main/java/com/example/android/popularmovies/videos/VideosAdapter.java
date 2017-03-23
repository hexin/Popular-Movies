package com.example.android.popularmovies.videos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.popularmovies.R;
import java.util.ArrayList;
import java.util.List;

public class VideosAdapter  extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder>{

    private List<Video> videos;
    private VideosAdapter.VideoOnClickHandler mVideoOnClickHandler;

    public VideosAdapter(VideosAdapter.VideoOnClickHandler videoOnClickHandler) {
        this(new ArrayList<Video>(), videoOnClickHandler);
    }

    public VideosAdapter(List<Video> videos, VideosAdapter.VideoOnClickHandler videoOnClickHandler) {
        this.videos = new ArrayList<>(videos);
        this.mVideoOnClickHandler = videoOnClickHandler;
    }

    @Override
    public VideosAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_list_item, parent, attachToRoot);
        return new VideosAdapter.VideoViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(VideosAdapter.VideoViewHolder holder, int position) {
        holder.bind(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video> newVideos) {
        this.videos = new ArrayList<>(newVideos);
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mVideoTitleTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoTitleTextView = (TextView) itemView.findViewById(R.id.tv_videos_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mVideoOnClickHandler.onVideoClick(videos.get(adapterPosition));
        }

        private void bind(Video video) {
            mVideoTitleTextView.setText(video.getName());
        }
    }

    public interface VideoOnClickHandler {
        void onVideoClick(Video video);
    }
}
