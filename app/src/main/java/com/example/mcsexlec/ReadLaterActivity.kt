package com.example.mcsexlec

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReadLaterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var databaseHelper: DatabaseHelper
    // Ganti jadi var agar bisa di-update
    private var savedNewsList: List<NewsArticle> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_later)

        recyclerView = findViewById(R.id.rvReadLater)
        databaseHelper = DatabaseHelper(this)

        // Ambil data berita yang disimpan
        savedNewsList = databaseHelper.getAllReadLater()

        // -- PERBARUI BLOK INI --
        newsAdapter = NewsAdapter(
            savedNewsList.toMutableList(),
            // Logika untuk klik tombol bookmark (untuk menghapus dari daftar)
            onReadLaterClick = { article ->
                databaseHelper.removeFromReadLater(article.title)
                Toast.makeText(this, "Removed from Read Later", Toast.LENGTH_SHORT).show()
                refreshList() // Panggil fungsi untuk memuat ulang daftar
            },
            // Logika untuk klik item berita (untuk melihat detail)
            onItemClick = { article ->
                val intent = Intent(this, NewsDetailActivity::class.java).apply {
                    putExtra("NEWS_URL", article.url)
                    putExtra("NEWS_TITLE", article.title)
                }
                startActivity(intent)
            }
        )
        // -- AKHIR PERUBAHAN --

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = newsAdapter
    }

    // Fungsi untuk memperbarui RecyclerView setelah item dihapus
    private fun refreshList() {
        savedNewsList = databaseHelper.getAllReadLater()
        newsAdapter.updateData(savedNewsList)
    }
}