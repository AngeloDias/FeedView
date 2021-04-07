package br.ufpe.cin.android.podcast.view

import com.prof.rssparser.Article

interface OnEpisodeTitleClickListener {
    fun onClick(articleEpisode: Article)
}