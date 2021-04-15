package br.ufpe.cin.android.podcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PlayAudioFileService: Service() {
    private lateinit var mPlayer: MediaPlayer

    companion object {
        const val AUDIO_FILE_PATH_TO_DOWNLOAD = "AUDIO FILE PATH TO DOWNLOAD"
        const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
        const val VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose notifications"
    }

    override fun onCreate() {
        super.onCreate()

        mPlayer = MediaPlayer()

        mPlayer.setOnCompletionListener {
            stopSelf()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                VERBOSE_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.iconfinder_youtube_stream_video)
            .setOngoing(true)
            .setContentTitle("Music service running")
            .setContentText("Clique para acessar player")
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(mPlayer.isPlaying) {
            mPlayer.seekTo(0)
        } else if(intent != null){
            mPlayer.setDataSource(intent.getStringExtra(AUDIO_FILE_PATH_TO_DOWNLOAD))
            mPlayer.prepare()
            mPlayer.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

}