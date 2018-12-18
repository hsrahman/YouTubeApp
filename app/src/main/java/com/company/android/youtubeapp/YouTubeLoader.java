package com.company.android.youtubeapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class YouTubeLoader extends AsyncTaskLoader<List<VideoInfo>> {

    /** Query URL */
    private String mUrl;

    public YouTubeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<VideoInfo> loadInBackground() {
        return NetworkQuery.fetchYouTubeData(mUrl);
    }
}
