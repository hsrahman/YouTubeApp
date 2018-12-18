package com.company.android.youtubeapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NetworkQuery {
    public static final String API_KEY = "YOUR API KEY";
    private static final String LOG_TAG = "NetworkQuery";

    private NetworkQuery() {

    }

    private static List<VideoInfo> extractYouTubeDataFromJson(String youtubeJson){
        if(TextUtils.isEmpty(youtubeJson))
            return null;

        List<VideoInfo> youtubeVideos = new ArrayList<>();
        try{
            JSONObject baseJsonResponse = new JSONObject(youtubeJson);
            JSONArray allItems = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < allItems.length(); i++) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.vid = allItems.getJSONObject(i).getJSONObject("id").getString("videoId");

                videoInfo.channelTitle = allItems.getJSONObject(i).getJSONObject("snippet").getString("channelTitle");
                videoInfo.title = allItems.getJSONObject(i).getJSONObject("snippet").getString("title");
                videoInfo.channelId = allItems.getJSONObject(i).getJSONObject("snippet").getString("channelId");
                videoInfo.date = allItems.getJSONObject(i).getJSONObject("snippet").getString("publishedAt");
                videoInfo.description = allItems.getJSONObject(i).getJSONObject("snippet").getString("description");
                videoInfo.thumbnail = allItems.getJSONObject(i).getJSONObject("snippet")
                        .getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                youtubeVideos.add(videoInfo);

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the source JSON results", e);
        }

        return youtubeVideos;

    }

    public static List<VideoInfo> fetchYouTubeData(String requestUrl){
        return extractYouTubeDataFromJson(getHttpResultString(requestUrl));
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String createHttpRequest (URL url) throws IOException {
        String jsonResponse = "";
        if (url != null) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else{
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream (InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            do {
                line = reader.readLine();
                output.append(line);
            } while (line != null);
        }
        return output.toString();
    }

    private static String getHttpResultString (String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try{
            jsonResponse = createHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request");
        }
        return jsonResponse;
    }
}
