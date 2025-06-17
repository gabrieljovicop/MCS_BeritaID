package com.example.mcsexlec

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class HomeActivity : AppCompatActivity() {

    private lateinit var kebabMenu: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        kebabMenu = findViewById(R.id.kebabMenu)
        recyclerView = findViewById(R.id.rvNewsList)

        kebabMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        newsAdapter = NewsAdapter(mutableListOf()) { article ->
            // Cek apakah sudah disimpan
            val isSaved = databaseHelper.isNewsSaved(article.title)

            if (!isSaved) {
                databaseHelper.addToReadLater(article.title, article.publishedAt, article.urlToImage)
                article.isSaved = true
                Toast.makeText(this, "Saved to Read Later", Toast.LENGTH_SHORT).show()
            } else {
                databaseHelper.removeFromReadLater(article.title)
                article.isSaved = false
                Toast.makeText(this, "Removed from Read Later", Toast.LENGTH_SHORT).show()
            }

            newsAdapter.notifyDataSetChanged()
        }

        recyclerView.adapter = newsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchNews()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_popup, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> true
                R.id.menu_read_later -> {
                    startActivity(Intent(this, ReadLaterActivity::class.java))
                    true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun fetchNews() {
        // API Key GNews baru Anda
        val gnewsApiKey = "779beec2d161051a25d716245ce04b01"

        // URL untuk GNews API
        val url = "https://gnews.io/api/v4/top-headlines?category=general&lang=en&country=us&max=10&apikey=$gnewsApiKey"

        val requestQueue = Volley.newRequestQueue(this)

        // Inisialisasi DatabaseHelper di luar request agar bisa diakses
        if (!::databaseHelper.isInitialized) {
            databaseHelper = DatabaseHelper(this)
        }

        // --- OPTIMISASI DIMULAI DI SINI ---
        // 1. Ambil semua judul berita yang sudah disimpan dalam satu kali query
        val savedNewsTitles = databaseHelper.getAllReadLater().map { it.title }.toSet()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val articles = response.getJSONArray("articles")
                val newsList = mutableListOf<NewsArticle>()

                for (i in 0 until articles.length()) {
                    val item = articles.getJSONObject(i)
                    val title = item.getString("title")
                    // GNews menggunakan "publishedAt"
                    val publishedAt = item.getString("publishedAt")
                    // GNews menggunakan "image" untuk URL gambar
                    val imageUrl = item.getString("image")

                    // 2. Cek ke Set (sangat cepat), bukan ke database di dalam loop
                    val isSaved = savedNewsTitles.contains(title)

                    newsList.add(NewsArticle(title, publishedAt, imageUrl, isSaved))
                }
                // --- OPTIMISASI SELESAI ---

                newsAdapter.updateData(newsList)
            },
            { error ->
                // Menampilkan pesan error yang lebih jelas di Toast
                val errorMessage = error.message ?: "Unknown error"
                Toast.makeText(this, "Error fetching news: $errorMessage", Toast.LENGTH_LONG).show()
            })

        requestQueue.add(jsonObjectRequest)
    }
}