package br.ufpe.cin.android.podcast

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.EpisodioDatabase
import br.ufpe.cin.android.podcast.database.EpisodioRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.view.EpisodiosAdapter
import br.ufpe.cin.android.podcast.view.OnEpisodeTitleClickListener
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnEpisodeTitleClickListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var parser : Parser
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private lateinit var sharedPref: SharedPreferences
    private val viewModel: EpisodioViewModel by viewModels {
        EpisodioViewModelFactory(EpisodioRepository(EpisodioDatabase.getInstance(this).dao()))
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

        parser = Parser.Builder()
            .context(this)
            .cacheExpirationMillis(24L * 60L * 60L * 100L)
            .build()

        viewModel.episodios.observe(
            this,
            Observer {
                episodiosAdapter.submitList(it.toList())
            }
        )
    }

    override fun onStart() {
        super.onStart()

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        scope.launch {
            val channel = withContext(Dispatchers.IO) {
                val feedLink = sharedPref.getString(
                    getString(R.string.shared_preferences_key),
                    getString(R.string.link_inicial))

                parser.getChannel(feedLink!!)
            }

            val episodios = mutableListOf<Episodio>()

            channel.articles.forEach {
                val linkEpisodio = it.link ?: ""
                val titulo = it.title ?: ""
                val descricao = it.description ?: ""
                val linkArquivo = it.sourceUrl ?: ""
                val dataPublicacao = it.pubDate ?: ""

                val episodio = Episodio(linkEpisodio, titulo, descricao, linkArquivo, dataPublicacao)

                episodios.add(episodio)

                viewModel.insert(episodio)
            }

            if(channel.articles.isEmpty() && viewModel.episodios.value?.isNotEmpty() == true) {
                episodios.addAll(viewModel.episodios.value!!)
            }

            binding.articlesRecycler.adapter = EpisodiosAdapter(episodios, this@MainActivity)
            binding.articlesRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
        }

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