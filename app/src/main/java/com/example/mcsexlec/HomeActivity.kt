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

        newsAdapter = NewsAdapter(
            mutableListOf(),
            // Blok untuk klik tombol bookmark
            onReadLaterClick = { article ->
                val isSaved = databaseHelper.isNewsSaved(article.title)

                if (!isSaved) {
                    // Panggil fungsi dengan parameter URL yang baru ditambahkan
                    databaseHelper.addToReadLater(article.title, article.publishedAt, article.urlToImage, article.url)
                    article.isSaved = true
                    Toast.makeText(this, "Saved to Read Later", Toast.LENGTH_SHORT).show()
                } else {
                    databaseHelper.removeFromReadLater(article.title)
                    article.isSaved = false
                    Toast.makeText(this, "Removed from Read Later", Toast.LENGTH_SHORT).show()
                }

                newsAdapter.notifyDataSetChanged()
            },
            // Blok untuk klik item berita (BARU)
            onItemClick = { article ->
                // Buat Intent untuk membuka NewsDetailActivity
                val intent = Intent(this, NewsDetailActivity::class.java).apply {
                    // Kirim URL dan Judul berita ke activity selanjutnya
                    putExtra("NEWS_URL", article.url)
                    putExtra("NEWS_TITLE", article.title)
                }
                startActivity(intent)
            }
        )

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
        // API Key untuk GNews.io
        val gnewsApiKey = "779beec2d161051a25d716245ce04b01"

        // URL endpoint GNews untuk mendapatkan berita utama
        val url = "https://gnews.io/api/v4/top-headlines?category=general&lang=en&country=us&max=10&apikey=$gnewsApiKey"

        val requestQueue = Volley.newRequestQueue(this)

        // Pastikan databaseHelper sudah diinisialisasi sebelum digunakan
        if (!::databaseHelper.isInitialized) {
            databaseHelper = DatabaseHelper(this)
        }

        // --- OPTIMISASI PERFORMA ---
        // 1. Ambil semua judul berita yang sudah disimpan dalam satu kali query ke database.
        //    Ini jauh lebih cepat daripada query ke database di dalam setiap iterasi loop.
        val savedNewsTitles = databaseHelper.getAllReadLater().map { it.title }.toSet()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val articles = response.getJSONArray("articles")
                    val newsList = mutableListOf<NewsArticle>()

                    for (i in 0 until articles.length()) {
                        val item = articles.getJSONObject(i)

                        // Ambil data dari JSON
                        val title = item.getString("title")
                        val publishedAt = item.getString("publishedAt")
                        val imageUrl = item.getString("image")
                        val articleUrl = item.getString("url") // URL untuk detail berita

                        // 2. Cek apakah berita sudah disimpan dengan membandingkan dengan Set di memori.
                        //    Ini adalah operasi yang sangat cepat.
                        val isSaved = savedNewsTitles.contains(title)

                        // Tambahkan berita ke dalam daftar
                        newsList.add(NewsArticle(title, publishedAt, imageUrl, articleUrl, isSaved))
                    }

                    // Perbarui adapter dengan data berita yang baru
                    newsAdapter.updateData(newsList)

                } catch (e: Exception) {
                    // Tangani jika ada error saat parsing JSON
                    Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                // Tangani jika ada error jaringan
                val errorMessage = error.message ?: "An unknown network error occurred"
                Toast.makeText(this, "Error fetching news: $errorMessage", Toast.LENGTH_LONG).show()
            })

        // Tambahkan request ke antrian untuk dieksekusi
        requestQueue.add(jsonObjectRequest)
    }
}