package com.example.mcsexlec

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "beritaid.db"
        private const val DATABASE_VERSION = 1

        // Users Table
        const val TABLE_USERS = "users"
        const val KEY_USER_ID = "user_id"
        const val KEY_PHONE = "phone_number"
        const val KEY_PASSWORD = "password"

        // Read Later Table
        const val TABLE_READ_LATER = "read_later"
        const val KEY_READ_ID = "id"
        const val KEY_NEWS_TITLE = "title"
        const val KEY_NEWS_DATE = "published_at"
        const val KEY_NEWS_IMAGE = "url_to_image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = """
            CREATE TABLE $TABLE_USERS (
                $KEY_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_PHONE TEXT NOT NULL,
                $KEY_PASSWORD TEXT NOT NULL
            )
        """

        val CREATE_READ_LATER_TABLE = """
            CREATE TABLE $TABLE_READ_LATER (
                $KEY_READ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NEWS_TITLE TEXT NOT NULL,
                $KEY_NEWS_DATE TEXT,
                $KEY_NEWS_IMAGE TEXT
            )
        """

        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_READ_LATER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_READ_LATER")
        onCreate(db)
    }

    // ======================== USER ========================

    fun getUserByPhone(phone: String): Cursor? {
        val db = this.readableDatabase
        // Cursor akan ditutup di LoginActivity, jadi di sini tidak perlu ditutup
        return db.query(TABLE_USERS, null, "$KEY_PHONE = ?", arrayOf(phone), null, null, null)
    }

    fun addUser(phone: String, password: String): Long {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(KEY_PHONE, phone)
                put(KEY_PASSWORD, password)
            }
            db.insert(TABLE_USERS, null, values)
        }
    }

    fun isUserRegistered(phone: String): Boolean {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.readableDatabase.use { db ->
            db.query(TABLE_USERS, null, "$KEY_PHONE = ?", arrayOf(phone), null, null, null).use { cursor ->
                cursor.count > 0
            }
        }
    }

    fun validateUser(phone: String, password: String): Boolean {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.readableDatabase.use { db ->
            db.query(TABLE_USERS, null, "$KEY_PHONE = ? AND $KEY_PASSWORD = ?", arrayOf(phone, password), null, null, null).use { cursor ->
                cursor.count > 0
            }
        }
    }

    // Fungsi ini tidak digunakan di kode saat ini, tapi jika digunakan, pastikan cursor ditutup
    fun getUserById(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_USERS, null, "$KEY_USER_ID = ?", arrayOf(userId.toString()), null, null, null)
    }

    // ======================== READ LATER ========================

    fun addToReadLater(title: String, publishedAt: String?, urlToImage: String?): Long {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(KEY_NEWS_TITLE, title)
                put(KEY_NEWS_DATE, publishedAt)
                put(KEY_NEWS_IMAGE, urlToImage)
            }
            db.insert(TABLE_READ_LATER, null, values)
        }
    }

    fun removeFromReadLater(title: String): Int {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.writableDatabase.use { db ->
            db.delete(TABLE_READ_LATER, "$KEY_NEWS_TITLE = ?", arrayOf(title))
        }
    }

    fun isNewsSaved(title: String): Boolean {
        // Menggunakan .use untuk menutup database secara otomatis
        return this.readableDatabase.use { db ->
            db.query(TABLE_READ_LATER, null, "$KEY_NEWS_TITLE = ?", arrayOf(title), null, null, null).use { cursor ->
                cursor.count > 0
            }
        }
    }

    fun getAllReadLater(): List<NewsArticle> {
        val list = mutableListOf<NewsArticle>()
        // Menggunakan .use untuk menutup database dan cursor secara otomatis
        this.readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM $TABLE_READ_LATER", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TITLE))
                        val date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DATE))
                        val image = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGE))
                        list.add(NewsArticle(title, date, image, true))
                    } while (cursor.moveToNext())
                }
            }
        }
        return list
    }
}