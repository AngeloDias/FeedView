package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.ufpe.cin.android.podcast.MainActivity.Companion.EXTRA_EPISODE_DESCRIPTION
import br.ufpe.cin.android.podcast.MainActivity.Companion.EXTRA_EPISODE_LINK
import br.ufpe.cin.android.podcast.databinding.ActivityEpisodeDetailBinding

class EpisodeDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEpisodeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEpisodeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras

        if (extras != null) {
            binding.episodeDescription.text = extras.getString(EXTRA_EPISODE_DESCRIPTION)
            binding.episodeLink.text = extras.getString(EXTRA_EPISODE_LINK)
        }

    }

}