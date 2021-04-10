package br.ufpe.cin.android.podcast

import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioDatabase
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.download.DownloadService
import br.ufpe.cin.android.podcast.view.EpisodiosAdapter
import br.ufpe.cin.android.podcast.view.OnEpisodeTitleClickListener

class MainActivity : AppCompatActivity(), OnEpisodeTitleClickListener {
    private lateinit var binding : ActivityMainBinding
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

    companion object {
        val PODCAST_FEED = "https://jovemnerd.com.br/feed-nerdcast/"

        const val EXTRA_EPISODE_DESCRIPTION = "EPISODE_DESCRIPTION"
        const val EXTRA_EPISODE_LINK = "EPISODE_LINK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val episodiosAdapter = EpisodiosAdapter(mutableListOf(), this)
        val intent = Intent(this, DownloadService::class.java)

        DownloadService.enqueueWork(this, intent)

        viewModel.episodios.observe(
            this,
            Observer {
                episodiosAdapter.submitList(it.toList())
            }
        )
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadService.DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        unregisterReceiver(onDownloadComplete)
        super.onPause()
    }

    override fun onClick(episode: Episodio) {
        val intent = Intent(this, EpisodeDetailActivity::class.java)

        intent.putExtra(EXTRA_EPISODE_DESCRIPTION, episode.descricao)
        intent.putExtra(EXTRA_EPISODE_LINK, episode.linkEpisodio)

        startActivity(intent)
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