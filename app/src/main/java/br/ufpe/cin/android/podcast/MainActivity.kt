package br.ufpe.cin.android.podcast

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.view.ArticlesAdapter
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

    companion object {
        val PODCAST_FEED = "https://jovemnerd.com.br/feed-nerdcast/"

        const val EXTRA_EPISODE_DESCRIPTION = "EPISODE_DESCRIPTION"
        const val EXTRA_EPISODE_LINK = "EPISODE_LINK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parser = Parser.Builder()
            .context(this)
            .cacheExpirationMillis(24L * 60L * 60L * 100L)
            .build()
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

            binding.articlesRecycler.adapter = ArticlesAdapter(channel.articles, this@MainActivity)
            binding.articlesRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }

    override fun onClick(articleEpisode: Article) {
        val intent = Intent(this, EpisodeDetailActivity::class.java)

        intent.putExtra(EXTRA_EPISODE_DESCRIPTION, articleEpisode.description)
        intent.putExtra(EXTRA_EPISODE_LINK, articleEpisode.link)

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