package com.example.mcsexlec

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class NewsAdapter(
    private var newsList: MutableList<NewsArticle>,
    private val onReadLaterClick: (NewsArticle) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val date: TextView = itemView.findViewById(R.id.txtReleaseDate)
        val readTime: TextView = itemView.findViewById(R.id.txtEstimateReadTime)
        val image: ImageView = itemView.findViewById(R.id.ivNews)
        val readLaterBtn: ImageButton = itemView.findViewById(R.id.btnReadLater)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.title.text = article.title
        holder.date.text = article.publishedAt ?: ""
        holder.readTime.text = "4 min read" // Static or you can make it dynamic

        if (!article.urlToImage.isNullOrEmpty()) {
            Picasso.get()
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background) // opsional
                .error(R.drawable.ic_launcher_foreground)       // opsional
                .into(holder.image)
        }

        val bookmarkIcon = if (article.isSaved)
            R.drawable.ic_bookmarked // misalnya icon bookmark penuh
        else
            R.drawable.ic_bookmark  // icon kosong

        holder.readLaterBtn.setImageResource(bookmarkIcon)

        holder.readLaterBtn.setOnClickListener {
            onReadLaterClick(article)
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newData: List<NewsArticle>) {
        newsList.clear()
        newsList.addAll(newData)
        notifyDataSetChanged()
    }
}