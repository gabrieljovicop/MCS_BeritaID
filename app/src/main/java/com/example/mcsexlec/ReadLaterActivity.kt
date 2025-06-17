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

class ReadLaterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var savedNewsList: List<NewsArticle>
    private lateinit var kebabMenu: ImageView  // Tambahan untuk kebab menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_later)

        recyclerView = findViewById(R.id.rvReadLater)
        kebabMenu = findViewById(R.id.kebabMenu) // Hubungkan dengan ImageView appbar
        databaseHelper = DatabaseHelper(this)

        // Tampilkan kebab menu
        kebabMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Ambil data berita yang disimpan
        savedNewsList = databaseHelper.getAllReadLater()

        // Siapkan adapter dengan aksi hapus saat diklik
        newsAdapter = NewsAdapter(savedNewsList.toMutableList()) { article ->
            databaseHelper.removeFromReadLater(article.title)
            Toast.makeText(this, "Removed from Read Later", Toast.LENGTH_SHORT).show()
            refreshList()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = newsAdapter
    }

    private fun refreshList() {
        savedNewsList = databaseHelper.getAllReadLater()
        newsAdapter.updateData(savedNewsList)
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_popup, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.menu_read_later -> true // Sudah di halaman ini
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
