package br.ufpe.cin.android.podcast.view

import br.ufpe.cin.android.podcast.data.Episodio

interface OnEpisodeTitleClickListener {
    fun onClick(episode: Episodio)
}