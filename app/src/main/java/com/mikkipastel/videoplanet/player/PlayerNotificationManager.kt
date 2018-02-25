package com.mikkipastel.videoplanet.player

import android.app.PendingIntent
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import com.mikkipastel.videoplanet.MainActivity
import com.mikkipastel.videoplanet.R

class PlayerNotificationManager(private val service: PlayerService) {

    private val mAppname: String

    private val resources: Resources = service.resources

    init {
        mAppname = resources.getString(R.string.app_name)
    }

    fun createAction(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(service, PlayerService::class.java)
        intent.action = action
        return PendingIntent.getService(service, requestCode, intent, 0)
    }

    fun startNotify(playbackStatus: String) {

        var icon = R.drawable.ic_pause_noti

        var playPauseAction = createAction(PlayerService.ACTION_PAUSE, REQUEST_CODE_PAUSE)

        if (playbackStatus == PlaybackStatus.PAUSED) {
            icon = R.drawable.ic_play_noti

            playPauseAction = createAction(PlayerService.ACTION_PLAY, REQUEST_CODE_PLAY)
        }

        val stopAction = createAction(PlayerService.ACTION_STOP, REQUEST_CODE_STOP)

        val intent = Intent(service, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(service, 0, intent, 0)

        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID)

        val builder = NotificationCompat.Builder(service, NOTIFICATION_ID.toString())
                .setSmallIcon(R.drawable.exo_edit_mode_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(mAppname)
                .setContentText("Hello World! Testing video service")
                .setContentIntent(pendingIntent)
                .addAction(icon, "pause", playPauseAction)
                .addAction(R.drawable.ic_stop_noti, "stop", stopAction)
                .setStyle(MediaStyle()
                        .setMediaSession(service.mediaSession.sessionToken)
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction))

        service.startForeground(NOTIFICATION_ID, builder.build())
    }

    fun cancelNotify() {
        service.stopForeground(true)
    }

    companion object {

        private val NOTIFICATION_ID = 555

        private val REQUEST_CODE_PAUSE = 1
        private val REQUEST_CODE_PLAY = 2
        private val REQUEST_CODE_STOP = 3
    }

}