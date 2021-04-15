package br.ufpe.cin.android.podcast

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioDatabase
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.download.DownloadEpisodiosService
import br.ufpe.cin.android.podcast.download.DownloadAudioFileService
import br.ufpe.cin.android.podcast.view.EpisodiosAdapter
import br.ufpe.cin.android.podcast.view.OnEpisodeTitleClickListener

class MainActivity : AppCompatActivity(), OnEpisodeTitleClickListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var episodiosAdapter: EpisodiosAdapter
    private val viewModel: EpisodioViewModel by viewModels {
        EpisodioViewModelFactory(EpisodioRepository(EpisodioDatabase.getInstance(this).dao()))
    }

    private val onDownloadComplete = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mutableListEpisodios = mutableListOf<Episodio>()
            viewModel.episodios.value?.forEach {
                mutableListEpisodios.add(it)
            }

            binding.articlesRecycler.adapter = EpisodiosAdapter(
                mutableListEpisodios,
                this@MainActivity)

            binding.articlesRecycler.layoutManager = LinearLayoutManager(this@MainActivity)

            Toast.makeText(context, "Download completed successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private val onAudioFileDownload = object: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(
                context,
                "Audio file download completed",
                Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        val PODCAST_FEED = "https://jovemnerd.com.br/feed-nerdcast/"

        const val EXTRA_EPISODE_DESCRIPTION = "EPISODE_DESCRIPTION"
        const val EXTRA_EPISODE_LINK = "EPISODE_LINK"
        const val EXTRA_EPISODE_AUDIO_TO_DOWNLOAD_TITLE = "EXTRA_EPISODE_AUDIO_TO_DOWNLOAD_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        episodiosAdapter = EpisodiosAdapter(mutableListOf(), this)

        DownloadEpisodiosService.enqueueWork(this, Intent(this, DownloadEpisodiosService::class.java))

        viewModel.episodios.observe(
            this,
            Observer {
                episodiosAdapter.submitList(it.toList())
            }
        )
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadEpisodiosService.DOWNLOAD_EPISODIOS_COMPLETE))
        registerReceiver(onAudioFileDownload, IntentFilter(DownloadAudioFileService.DOWNLOAD_AUDIO_FILE_COMPLETE))
    }

    override fun onPause() {
        unregisterReceiver(onDownloadComplete)
        unregisterReceiver(onAudioFileDownload)
        super.onPause()
    }

    override fun onClick(episode: Episodio, itemView: View) {
        when(itemView.id) {
            R.id.item_title -> {
                val intent = Intent(this, EpisodeDetailActivity::class.java)

                intent.putExtra(EXTRA_EPISODE_DESCRIPTION, episode.descricao)
                intent.putExtra(EXTRA_EPISODE_LINK, episode.linkArquivo)

                startActivity(intent)
            }

            R.id.item_action -> {
                val fileLink = episode.linkArquivo
                val intent: Intent

                if(URLUtil.isHttpsUrl(fileLink) || URLUtil.isHttpUrl(fileLink)) {
                    intent = Intent(this, DownloadAudioFileService::class.java)

                    intent.data = Uri.parse(episode.linkArquivo)

                    intent.putExtra(EXTRA_EPISODE_AUDIO_TO_DOWNLOAD_TITLE, episode.titulo)

                    Toast.makeText(applicationContext, "Audio download started", Toast.LENGTH_SHORT).show()
                    DownloadAudioFileService.enqueueDownloadFileWork(this, intent)
                } else if(fileLink.isNotEmpty() && fileLink.isNotBlank()){
                    intent = Intent(this, PlayAudioFileService::class.java)

                    intent.putExtra(
                        PlayAudioFileService.AUDIO_FILE_PATH_TO_DOWNLOAD,
                        fileLink)

                    startService(intent)
                }
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.preferences_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit_shared_preferences -> {
                startActivity(Intent(this, PreferenciasActivity::class.java))

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}