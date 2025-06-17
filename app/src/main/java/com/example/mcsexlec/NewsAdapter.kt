package com.example.mcsexlec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class NewsAdapter(
    private var newsList: MutableList<NewsArticle>,
    private val onReadLaterClick: (NewsArticle) -> Unit,
    private val onItemClick: (NewsArticle) -> Unit // <-- 1. TAMBAHKAN PARAMETER INI
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // 2. MODIFIKASI ViewHolder untuk mendeteksi klik
    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val date: TextView = itemView.findViewById(R.id.txtReleaseDate)
        val readTime: TextView = itemView.findViewById(R.id.txtEstimateReadTime)
        val image: ImageView = itemView.findViewById(R.id.ivNews)
        val readLaterBtn: ImageButton = itemView.findViewById(R.id.btnReadLater)

        init {
            // Set listener pada seluruh item view
            itemView.setOnClickListener {
                // Pastikan posisi valid sebelum memanggil listener
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(newsList[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.title.text = article.title
        // Format tanggal agar lebih mudah dibaca (opsional)
        holder.date.text = article.publishedAt?.substring(0, 10) ?: ""
        holder.readTime.text = "5 min read" // Static, bisa disesuaikan

        if (!article.urlToImage.isNullOrEmpty()) {
            Picasso.get()
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.image)
        }

        val bookmarkIcon = if (article.isSaved)
            R.drawable.ic_bookmarked
        else
            R.drawable.ic_bookmark

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