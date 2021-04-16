package br.ufpe.cin.android.podcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File

class PlayAudioFileService: Service() {
    private lateinit var mPlayer: MediaPlayer
    private val musicBinder: IBinder = MusicBinder()
    private lateinit var lastFilePath: String
    private var positionInAudio = -1

    companion object {
        const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
        const val VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose notifications"
    }

    override fun onCreate() {
        super.onCreate()

        lastFilePath = ""
        mPlayer = MediaPlayer()

        mPlayer.setOnCompletionListener {
            val file = File(lastFilePath)

            file.delete()
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

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.iconfinder_play)
            .setOngoing(true)
            .setContentTitle("Music service running")
            .setContentText("Clique para voltar à lista de episódios")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun playOrPauseMusic(filePath: String, audioPosition: Int) {
        if(!mPlayer.isPlaying) {

            if(filePath != lastFilePath) {
                mPlayer.reset()
                mPlayer.setDataSource(filePath)
                mPlayer.prepare()
                lastFilePath = filePath
            }

            mPlayer.start()

            if(audioPosition > 0) {
                mPlayer.seekTo(audioPosition)
            }

        } else {
            positionInAudio = mPlayer.currentPosition
            mPlayer.pause()
        }
    }

    fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    fun audioPositionWhenReproducing(): Int {
        return positionInAudio
    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    inner class MusicBinder: Binder() {
        val service: PlayAudioFileService
            get() = this@PlayAudioFileService
    }

}