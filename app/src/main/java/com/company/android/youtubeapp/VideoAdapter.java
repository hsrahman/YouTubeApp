package com.company.android.youtubeapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoInfo> videoInfos;

    public interface OnItemClickListener {
        void onItemClicked(VideoInfo info);
    }

    private OnItemClickListener listener;
    private Context context;

    public VideoAdapter(Context c, List<VideoInfo> videoInfos, OnItemClickListener listener) {
        context = c;
        this.videoInfos = videoInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_list_item, parent, false);
        return new VideoInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoInfoViewHolder)holder).bind(videoInfos.get(position), listener, context);
    }

    @Override
    public int getItemCount() {
        return videoInfos.size();
    }

    public void clear () {
        videoInfos.clear();
    }

    private static class VideoInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView tite, channelTitle, description, date;
        private ImageView thumbnail;

        public VideoInfoViewHolder(View itemView) {
            super(itemView);
            tite = itemView.findViewById(R.id.title_tv);
            channelTitle = itemView.findViewById(R.id.channel_tv);
            description = itemView.findViewById(R.id.desc_tv);
            date = itemView.findViewById(R.id.date_tv);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }

        public void bind(final VideoInfo videoInfo, final OnItemClickListener listener, Context context) {
            tite.setText(videoInfo.title);
            channelTitle.setText(videoInfo.channelTitle);
            channelTitle.setTag(videoInfo.channelId);
            description.setText(videoInfo.description);
            date.setText(videoInfo.date);
            Glide.with(context).load(videoInfo.thumbnail).into(thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(videoInfo);
                }
            });
        }
    }
}
