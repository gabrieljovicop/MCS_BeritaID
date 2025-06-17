package com.example.mcsexlec

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class ReadLaterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var savedNewsList: List<NewsArticle>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_later)

        recyclerView = findViewById(R.id.rvReadLater)
        databaseHelper = DatabaseHelper(this)

        // Ambil data berita yang disimpan
        savedNewsList = databaseHelper.getAllReadLater()

        // Siapkan adapter dengan listener kosong (atau sesuai kebutuhan)
        newsAdapter = NewsAdapter(savedNewsList.toMutableList(),
            onReadLaterClick = { article ->
                // Optional: bisa tambahkan tombol remove dari read later
                databaseHelper.removeFromReadLater(article.title)
                Toast.makeText(this, "Removed from Read Later", Toast.LENGTH_SHORT).show()
                refreshList()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = newsAdapter
    }

    private fun refreshList() {
        savedNewsList = databaseHelper.getAllReadLater()
        newsAdapter.updateData(savedNewsList)
    }
}


