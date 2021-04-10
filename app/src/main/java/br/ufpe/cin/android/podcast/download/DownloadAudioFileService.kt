package br.ufpe.cin.android.podcast.download

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService
import br.ufpe.cin.android.podcast.MainActivity.Companion.EXTRA_EPISODE_AUDIO_TO_DOWNLOAD_TITLE
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioDatabase
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DownloadAudioFileService: JobIntentService() {

    companion object {
        private val JOB_ID = 9876
        val DOWNLOAD_AUDIO_FILE_COMPLETE = "br.ufpe.cin.android.podcast.download.DownloadFileService"

        fun enqueueDownloadFileWork(context: Context, intent: Intent) {
            enqueueWork(context, DownloadAudioFileService::class.java, JOB_ID, intent)
        }
    }

    /**
     * Trecho de código copiado da aula de download e adaptado a este projeto.
     * */
    override fun onHandleWork(intent: Intent) {
        try {
            val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            root.mkdirs()

            val intentData = intent.data
            val output = File(root, intentData!!.lastPathSegment)

            if (output.exists()) {
                output.delete()
            }

            val url = URL(intentData.toString())
            val c = url.openConnection() as HttpURLConnection
            val fos = FileOutputStream(output.path)
            val out = BufferedOutputStream(fos)

            try {
                val `in` = c.inputStream
                val buffer = ByteArray(8192)
                var len: Int
                while (`in`.read(buffer).also { len = it } >= 0) {
                    out.write(buffer, 0, len)
                }

                out.flush()
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }

            val scope = CoroutineScope(Dispatchers.Main.immediate)

            scope.launch {
                val repo = EpisodioRepository(EpisodioDatabase.getInstance(applicationContext).dao())

                val ep = repo.searchByTitulo(intent.getStringExtra(EXTRA_EPISODE_AUDIO_TO_DOWNLOAD_TITLE)!!)
                val episodio = Episodio(
                    titulo = ep!!.titulo,
                    linkArquivo = output.path,
                    linkEpisodio = ep.linkEpisodio,
                    dataPublicacao = ep.dataPublicacao,
                    descricao = ep.descricao)

                repo.update(episodio)
            }

            sendBroadcast(Intent(DOWNLOAD_AUDIO_FILE_COMPLETE))

        } catch (e: IOException) {
            Log.e(javaClass.name, "Exception durante download", e)
        }
    }

}