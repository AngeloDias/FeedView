package br.ufpe.cin.android.podcast.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.R
import com.prof.rssparser.Article

class ArticlesAdapter(private val articles: MutableList<Article>): RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemTitleTxtView: TextView = view.findViewById(R.id.item_title)
        val itemDateTxtView: TextView = view.findViewById(R.id.item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemfeed, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitleTxtView.text = articles[position].title
        holder.itemDateTxtView.text = articles[position].pubDate
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}