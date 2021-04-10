package br.ufpe.cin.android.podcast.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.data.Episodio

class EpisodiosAdapter(private val episodios: MutableList<Episodio>, var onTitleClicked: OnEpisodeTitleClickListener): ListAdapter<Episodio, EpisodiosAdapter.ViewHolder>(ArticleDiffer) {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemTitleTxtView: TextView = view.findViewById(R.id.item_title)
        val itemDateTxtView: TextView = view.findViewById(R.id.item_date)
        val itemDownloadButton: Button = view.findViewById(R.id.item_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemfeed, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitleTxtView.text = episodios[position].titulo
        holder.itemDateTxtView.text = episodios[position].dataPublicacao

        holder.itemTitleTxtView.setOnClickListener{
            onTitleClicked.onClick(episodios[position], it)
        }

        holder.itemDownloadButton.setOnClickListener {
            onTitleClicked.onClick(episodios[position], it)
        }
    }

    override fun getItemCount(): Int {
        return episodios.size
    }

    private object ArticleDiffer: DiffUtil.ItemCallback<Episodio>() {
        override fun areItemsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            val verifyTitle = oldItem.titulo == newItem.titulo
            val verifyLink = oldItem.linkEpisodio == newItem.linkEpisodio
            val verifyDesc = oldItem.descricao == newItem.descricao
            val verifySourceFile = oldItem.linkArquivo == newItem.linkArquivo
            val verifyDatePub = oldItem.dataPublicacao == newItem.dataPublicacao

            return verifyTitle && verifyLink && verifyDesc && verifySourceFile && verifyDatePub
        }

    }
}