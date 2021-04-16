package br.ufpe.cin.android.podcast.download

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.preference.PreferenceManager
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioDatabase
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadEpisodiosService: JobIntentService() {

    companion object {
        private val JOB_ID = 1234
        val DOWNLOAD_EPISODIOS_COMPLETE = "br.ufpe.cin.android.podcast.download.DownloadEpisodiosService"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, DownloadEpisodiosService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val scope = CoroutineScope(Dispatchers.Main.immediate)
        val parser = Parser.Builder()
            .context(applicationContext)
            .cacheExpirationMillis(24L * 60L * 60L * 100L)
            .build()

        scope.launch {
            val channel = withContext(Dispatchers.IO) {
                val feedLink = sharedPref.getString(
                    getString(R.string.shared_preferences_key),
                    getString(R.string.link_inicial)
                )

                parser.getChannel(feedLink!!)
            }

            val repo = EpisodioRepository(EpisodioDatabase.getInstance(applicationContext).dao())

            channel.articles.forEach {
                val linkEpisodio = it.link ?: ""
                val titulo = it.title ?: ""
                val descricao = it.description ?: ""
                val linkArquivo = it.audio ?: ""
                val dataPublicacao = it.pubDate ?: ""

                val episodio =
                    Episodio(linkEpisodio, titulo, descricao, linkArquivo, dataPublicacao, 0)

                repo.insert(episodio)
            }

            sendBroadcast(Intent(DOWNLOAD_EPISODIOS_COMPLETE))
        }
    }

}