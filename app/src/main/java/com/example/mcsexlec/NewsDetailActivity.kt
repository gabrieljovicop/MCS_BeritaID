package com.example.mcsexlec

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Inisialisasi Views dari layout
        val backButton: ImageView = findViewById(R.id.backButton)
        val kebabMenu: ImageView = findViewById(R.id.kebabMenu)
        val headerTitle: TextView = findViewById(R.id.headerTitle)
        val webView: WebView = findViewById(R.id.webView)

        // Mengambil data yang dikirim dari activity sebelumnya
        val newsUrl = intent.getStringExtra("NEWS_URL")
        val newsTitle = intent.getStringExtra("NEWS_TITLE")

        // Set judul header dengan judul berita (dipotong jika terlalu panjang)
        headerTitle.text = newsTitle?.let { if (it.length > 20) it.substring(0, 19) + "..." else it }

        // Atur fungsi tombol kembali
        backButton.setOnClickListener {
            // Langsung panggil onBackPressed() atau finish()
            finish()
        }

        // Atur fungsi kebab menu
        kebabMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Konfigurasi dan muat WebView
        if (newsUrl != null) {
            webView.webViewClient = WebViewClient() // Agar link terbuka di dalam aplikasi
            webView.settings.javaScriptEnabled = true // Aktifkan JavaScript
            webView.loadUrl(newsUrl) // Muat URL berita
        } else {
            // Jika URL tidak ada, tampilkan pesan error
            Toast.makeText(this, "Error: News link not found", Toast.LENGTH_LONG).show()
            finish() // Kembali ke halaman sebelumnya jika tidak ada link
        }
    }

    // Fungsi untuk menampilkan popup menu
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        // Menggunakan menu yang sama dengan halaman lain
        popup.menuInflater.inflate(R.menu.menu_popup, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
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
}