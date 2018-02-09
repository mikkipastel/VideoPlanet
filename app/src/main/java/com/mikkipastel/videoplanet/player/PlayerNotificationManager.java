package com.mikkipastel.videoplanet.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.mikkipastel.videoplanet.MainActivity;
import com.mikkipastel.videoplanet.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PlayerNotificationManager {

    private static final int NOTIFICATION_ID = 555;

    private PlayerService service;

    private String mAppname;

    private Resources resources;

    public PlayerNotificationManager(PlayerService service) {
        this.service = service;
        this.resources = service.getResources();

        mAppname = resources.getString(R.string.app_name);
    }

    public void startNotify(String playbackStatus) {

        int icon = R.drawable.ic_pause;
        Intent playbackAction = new Intent(service, PlayerService.class);
        playbackAction.setAction(PlayerService.ACTION_PAUSE);
        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0);

        if (playbackStatus.equals(PlaybackStatus.PAUSED)) {
            icon = R.drawable.ic_play_arrow;
            playbackAction.setAction(PlayerService.ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, 0);
        }

        Intent stopIntent = new Intent(service, PlayerService.class);
        stopIntent.setAction(PlayerService.ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);

        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);

        Notification.Builder builder = new Notification.Builder(service)
                .setSmallIcon(R.drawable.exo_edit_mode_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(mAppname)
                .setContentText("Hello World! Testing video service")
                .setContentIntent(pendingIntent)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.ic_stop, "stop", stopAction);

        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotify() {
        service.stopForeground(true);
    }
}
