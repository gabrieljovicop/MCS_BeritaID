package com.example.mcsexlec

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread // Import library thread

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvRegisterLink: TextView
    private lateinit var tvError: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)
        tvError = findViewById(R.id.tvError)

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

        // Set up button click listener for login
        btnSignIn.setOnClickListener {
            val phoneNumber = etUsername.text.toString()
            val password = etPassword.text.toString()

            // Perform validation
            if (phoneNumber.isEmpty()) {
                showError("Username must be filled!")
            } else if (password.isEmpty()) {
                showError("Password must be filled!")
            } else {
                // Jalankan validasi di background thread
                thread {
                    val userId = getUserId(phoneNumber, password)

                    // Kembali ke UI thread untuk update tampilan
                    runOnUiThread {
                        if (userId != -1) {
                            // Store userId in SharedPreferences
                            val editor = sharedPreferences.edit()
                            editor.putInt("user_id", userId)
                            editor.apply()

                            // Navigate to OTP activity
                            val intent = Intent(this, OtpActivity::class.java)
                            startActivity(intent)
                            finish()  // Close the current LoginActivity
                        } else {
                            showError("Invalid credentials!")
                        }
                    }
                }
            }
        }

        // Set up click listener for the Register link
        tvRegisterLink.setOnClickListener {
            // Redirect to Register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Show error message and make it visible
    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    // Function to check if the user is registered using the database and return userId
    private fun getUserId(phone: String, password: String): Int {
        val isUserValid = databaseHelper.validateUser(phone, password)
        if (!isUserValid) {
            return -1
        }

        // Pastikan cursor ditutup dengan benar menggunakan try-finally
        val cursor = databaseHelper.getUserByPhone(phone)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Mengambil user ID dari cursor
                return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID))
            }
        } finally {
            // Selalu tutup cursor setelah selesai digunakan untuk menghindari memory leak
            cursor?.close()
        }

        return -1
    }
}