package com.mikkipastel.videoplanet.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloader;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

class VideoCaching extends AsyncTask<String, Void, Void> {

    private String userAgent;
    private Cache cache;

    private ProgressiveDownloader progressiveDownloader;

    public VideoCaching (Context context){
        userAgent = Util.getUserAgent(context, "exoplayer-codelab");
        cache = new SimpleCache(context.getCacheDir(), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 10));
    }

    @Override
    protected Void doInBackground(String... strings) {
//        StringBuilder buffer = new StringBuilder();
//        for (String each : strings)
//            buffer.append(",").append(each);
//        String joined = buffer.deleteCharAt(0).toString();
//
//        Log.d("__fileUrl", joined);

        progressiveDownloader = new ProgressiveDownloader(strings.toString(),
                null,
                new DownloaderConstructorHelper(cache, new DefaultHttpDataSourceFactory(userAgent)));

        progressiveDownloader.init();

        Downloader.ProgressListener listener = new Downloader.ProgressListener() {
            @Override
            public void onDownloadProgress(Downloader downloader, float downloadPercentage, long downloadedBytes) {
                Log.d("getDownloadedBytes", downloadedBytes + "");
                Log.d("getDownloadPercentage", downloadPercentage + "");
            }
        };

        try {
            progressiveDownloader.download(listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("__InterruptedException", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("__IOException", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("__getDownloadedBytes", progressiveDownloader.getDownloadedBytes() + "");
        Log.d("__getDownloadPercentage", progressiveDownloader.getDownloadPercentage() + "");
    }
}