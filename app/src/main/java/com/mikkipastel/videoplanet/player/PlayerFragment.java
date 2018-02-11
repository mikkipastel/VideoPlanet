package com.mikkipastel.videoplanet.player;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.mikkipastel.videoplanet.BaseFragment;
import com.mikkipastel.videoplanet.R;

public class PlayerFragment extends BaseFragment {

    private static final String BUNDLE_URL = "bundle_url";

    private SimpleExoPlayerView playerView;

    private String videoUrl;

    public static PlayerFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_URL, url);

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {
        playerView = rootView.findViewById(R.id.video_view);

        if (playerManager.getService() == null) {
            playerManager.bind();
        }

        playerView.setControllerHideOnTouch(false);
        playerView.setControllerShowTimeoutMs(0);
        playerView.showController();

    }

    private boolean hasArguments(Bundle bundle) {
        return bundle != null
                && bundle.getString(BUNDLE_URL) != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        managerBinding();
    }

    @Override
    public void onDestroy() {
        playerManager.unbind();

        super.onDestroy();
    }

    private void managerBinding() {
        Bundle bundle = getArguments();
        if (hasArguments(bundle)) {
            videoUrl = bundle.getString(BUNDLE_URL);
        } else {
            videoUrl = getString(R.string.media_url_mp4);
        }

        if (playerManager.isServiceBound()) {
            playerManager.playOrPause(videoUrl);
            playerView.setPlayer(playerManager.getService().exoPlayer);
        }

    }

    @SuppressLint("InlinedApi")
    private void hideSystemUiFullScreen() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUiFullScreen();
        }
        else {
            hideSystemUi();
        }
    }

}
