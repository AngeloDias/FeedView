package br.ufpe.cin.android.podcast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        scope.launch {
            val channel = withContext(Dispatchers.IO) {
                parser.getChannel(PODCAST_FEED)
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
}