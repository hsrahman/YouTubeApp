package com.company.android.youtubeapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<VideoInfo>>, VideoAdapter.OnItemClickListener{

    // SEARCH RESPONSE INFORMATION: https://developers.google.com/youtube/v3/docs/search#properties

    private static final int VIDEO_LOADER_ID = 1;
    private String youtubeUrl = "https://www.googleapis.com/youtube/v3/search";

    private String searchTextString = "";
    LoaderManager loaderManager;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private ProgressBar progressBar;
    private ImageButton search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search_btn);
        final EditText searchText = findViewById(R.id.search_et);
        progressBar = findViewById(R.id.progress_bar);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchText.getText().toString().trim().equals("")) {
                    searchTextString = searchText.getText().toString().trim();
                    search ();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a search term", Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView = findViewById(R.id.search_result);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void buttonToggle (boolean enable) {
        search.setEnabled(enable);
        if (enable) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void search () {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // permission

        if (networkInfo != null && networkInfo.isConnected()) {
            buttonToggle(false);
            loaderManager = getLoaderManager();
            loaderManager.restartLoader(VIDEO_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, "NETWORK ERROR", Toast.LENGTH_LONG).show();
        }
    }

    private Uri.Builder createUrl () {
        Uri baseUri = Uri.parse(youtubeUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", searchTextString);
        uriBuilder.appendQueryParameter("key", NetworkQuery.API_KEY);
        uriBuilder.appendQueryParameter("maxResults", "10");
        uriBuilder.appendQueryParameter("part", "snippet");
        return uriBuilder;
    }

    @Override
    public Loader<List<VideoInfo>> onCreateLoader(int i, Bundle bundle) {
        return new YouTubeLoader(this, createUrl().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<VideoInfo>> loader, List<VideoInfo> videoInfoList) {
        if (videoInfoList != null && !videoInfoList.isEmpty()) {
            videoAdapter = new VideoAdapter(this, videoInfoList, this);
            recyclerView.setAdapter(videoAdapter);
        } else {
            Toast.makeText(this, "No Results found", Toast.LENGTH_LONG).show();
        }
        buttonToggle(true);
    }

    @Override
    public void onLoaderReset(Loader<List<VideoInfo>> loader) {
        if (videoAdapter != null) {
            videoAdapter.clear();
            videoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClicked(VideoInfo info) {
        Intent player = new Intent(MainActivity.this, YouTubePlayerActivity.class);
        player.putExtra("vid", info.vid);
        startActivity(player );
    }
}
