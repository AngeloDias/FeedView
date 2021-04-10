package br.ufpe.cin.android.podcast.view

import android.view.View
import br.ufpe.cin.android.podcast.data.Episodio

interface OnEpisodeTitleClickListener {
    fun onClick(episode: Episodio, itemView: View)
}