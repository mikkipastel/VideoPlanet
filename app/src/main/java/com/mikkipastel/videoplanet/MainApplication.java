package com.mikkipastel.videoplanet;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.mikkipastel.videoplanet.cache.Utils;

public class MainApplication extends Application {

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MainApplication app = (MainApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(this))
//                .maxCacheSize(150)
//                .maxCacheFilesCount(15)
                .build();
    }
}
