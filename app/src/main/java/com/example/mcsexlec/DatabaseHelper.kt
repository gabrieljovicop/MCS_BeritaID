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
        return db.query(TABLE_USERS, null, "$KEY_PHONE = ?", arrayOf(phone), null, null, null)
    }

    fun addUser(phone: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PHONE, phone)
            put(KEY_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values).also { db.close() }
    }

    fun isUserRegistered(phone: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$KEY_PHONE = ?", arrayOf(phone), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun validateUser(phone: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$KEY_PHONE = ? AND $KEY_PASSWORD = ?", arrayOf(phone, password), null, null, null)
        val valid = cursor.count > 0
        cursor.close()
        return valid
    }

    fun getUserById(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_USERS, null, "$KEY_USER_ID = ?", arrayOf(userId.toString()), null, null, null)
    }

    // ======================== READ LATER ========================

    fun addToReadLater(title: String, publishedAt: String?, urlToImage: String?): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NEWS_TITLE, title)
            put(KEY_NEWS_DATE, publishedAt)
            put(KEY_NEWS_IMAGE, urlToImage)
        }
        return db.insert(TABLE_READ_LATER, null, values).also { db.close() }
    }

    fun removeFromReadLater(title: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_READ_LATER, "$KEY_NEWS_TITLE = ?", arrayOf(title)).also { db.close() }
    }

    fun isNewsSaved(title: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_READ_LATER, null, "$KEY_NEWS_TITLE = ?", arrayOf(title), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllReadLater(): List<NewsArticle> {
        val list = mutableListOf<NewsArticle>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_READ_LATER", null)
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TITLE))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DATE))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGE))
                list.add(NewsArticle(title, date, image, true))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
