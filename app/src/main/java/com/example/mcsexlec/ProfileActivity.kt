package com.example.mcsexlec

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class ProfileActivity : AppCompatActivity() {

    private lateinit var kebabMenu: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var btnLogout: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Inisialisasi komponen UI
        kebabMenu = findViewById(R.id.kebabMenu)
        tvUsername = findViewById(R.id.tvUsername)
        btnLogout = findViewById(R.id.btnLogout)

        // Ambil userId dari SharedPreferences
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val db = DatabaseHelper(this)
            val cursor = db.getUserById(userId)

            cursor.use {
                if (it.moveToFirst()) {
                    val phone = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.KEY_PHONE))
                    tvUsername.text = "$phone"
                }
            }
        } else {
            tvUsername.text = "User tidak ditemukan"
        }

        // Logout
        btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Menu popup (kebab menu)
        kebabMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }
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
                R.id.menu_read_later -> {
                    startActivity(Intent(this, ReadLaterActivity::class.java))
                    true
                }
                R.id.menu_profile -> true
                else -> false
            }
        }

        popup.show()
    }
}
