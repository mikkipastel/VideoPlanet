package com.mikkipastel.videoplanet.player;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;

import com.mikkipastel.videoplanet.MainActivity;
import com.mikkipastel.videoplanet.R;

public class PlayerNotificationManager {

    private static final int NOTIFICATION_ID = 555;

    private static final int REQUEST_CODE_PAUSE = 1;
    private static final int REQUEST_CODE_PLAY = 2;
    private static final int REQUEST_CODE_STOP = 3;

    private PlayerService service;

    private String mAppname;

    private Resources resources;

    public PlayerNotificationManager(PlayerService service) {
        this.service = service;
        this.resources = service.getResources();

        mAppname = resources.getString(R.string.app_name);
    }

    public PendingIntent createAction(String action, int requestCode) {
        Intent intent = new Intent(service, PlayerService.class);
        intent.setAction(action);
        return PendingIntent.getService(service, requestCode, intent, 0);
    }

    public void startNotify(String playbackStatus) {

        int icon = R.drawable.ic_pause_noti;

        PendingIntent playPauseAction = createAction(PlayerService.ACTION_PAUSE, REQUEST_CODE_PAUSE);

        if (playbackStatus.equals(PlaybackStatus.PAUSED)) {
            icon = R.drawable.ic_play_noti;

            playPauseAction = createAction(PlayerService.ACTION_PLAY, REQUEST_CODE_PLAY);
        }

        PendingIntent stopAction = createAction(PlayerService.ACTION_STOP, REQUEST_CODE_STOP);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);

        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.exo_edit_mode_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(mAppname)
                .setContentText("Hello World! Testing video service")
                .setContentIntent(pendingIntent)
                .addAction(icon, "pause", playPauseAction)
                .addAction(R.drawable.ic_stop_noti, "stop", stopAction)
                .setStyle(new MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction));

        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotify() {
        service.stopForeground(true);
    }

}
