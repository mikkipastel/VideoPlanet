package com.mikkipastel.videoplanet.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;

import com.danikula.videocache.CacheListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener, Player.EventListener, CacheListener {

    public static final String ACTION_PLAY = "com.fungjai.player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.fungjai.player.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.fungjai.player.ACTION_STOP";

    private final IBinder playerBind = new PlayerBinder();

    public SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;

    private PlayerNotificationManager notificationManager;
    private MediaControllerCompat.TransportControls transportControls;

    private AudioManager audioManager;

    private String status;

    private String streamUrl;

    protected class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();

            pause();
        }

        @Override
        public void onStop() {
            super.onStop();

            stop();

            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();

            resume();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        notificationManager = new PlayerNotificationManager(this);

        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediasSessionCallback);

        RenderersFactory renderersFactory = new DefaultRenderersFactory(getApplicationContext());
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DefaultLoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        exoPlayer.addListener(this);

        status = PlaybackStatus.IDLE;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return playerBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if(TextUtils.isEmpty(action))
            return START_NOT_STICKY;

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            stop();

            return START_NOT_STICKY;
        }

        if(action.equalsIgnoreCase(ACTION_PLAY)){
            transportControls.play();

        } else if(action.equalsIgnoreCase(ACTION_PAUSE)) {

            if (PlaybackStatus.STOPPED == status) {
                transportControls.stop();
            } else {
                transportControls.pause();
            }

        } else if(action.equalsIgnoreCase(ACTION_STOP)){
            pause();

            notificationManager.cancelNotify();
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();

        // TODO: clear cache
//        MainApplication.getProxy(getApplicationContext()).unregisterCacheListener(this);
//        try {
//            Utils.cleanVideoCacheDir(getApplicationContext());
//            Log.d("__onDestroy", "clear cache");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("__IOException", e.toString());
//        }

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        pause();

        exoPlayer.release();
        exoPlayer.removeListener(this);

        notificationManager.cancelNotify();

        mediaSession.release();

        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                exoPlayer.setVolume(0.8f);
                resume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying())
                    exoPlayer.setVolume(0.1f);
                break;
        }

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = PlaybackStatus.STOPPED;
                break;
            case Player.STATE_IDLE:
                status = PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
                break;
            default:
                status = PlaybackStatus.IDLE;
                break;
        }

        if(!status.equals(PlaybackStatus.IDLE))
            notificationManager.startNotify(status);

        EventBus.getDefault().post(status);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        EventBus.getDefault().post(PlaybackStatus.ERROR);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.d("__onCacheAvailable", percentsAvailable + "");
    }

    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(getApplicationContext(), "VideoPlanet");

        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                    new DefaultHttpDataSourceFactory("ua", new DefaultBandwidthMeter()));
            DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).
                    createMediaSource(uri);
        }
    }

    public String getStatus(){
        return status;
    }

    public void play() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);

        audioManager.abandonAudioFocus(this);
    }

    public void resume() {
        if (streamUrl != null)
            play();
    }

    public void stop() {
        exoPlayer.stop();

        audioManager.abandonAudioFocus(this);
    }

    public void init(String streamUrl) {
        this.streamUrl = streamUrl;

        // TODO: add caching
//        HttpProxyCacheServer proxy = MainApplication.getProxy(getApplicationContext());
//        proxy.registerCacheListener(this, streamUrl);
//        String proxyUrl = proxy.getProxyUrl(streamUrl);
//
//        MediaSource mediaSource;
//        if (streamUrl.contains(".mp")) {
//            mediaSource = buildMediaSource(Uri.parse(proxyUrl));
//        } else {
//            mediaSource = buildMediaSource(Uri.parse(streamUrl));
//        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(streamUrl));
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    public void playOrPause(String url) {
        if (streamUrl != null && streamUrl.equals(url)) {
            play();
        } else {
            init(url);
        }
    }

    public boolean isPlaying(){
        return this.status.equals(PlaybackStatus.PLAYING);
    }

    public MediaSessionCompat getMediaSession(){
        return mediaSession;
    }

}