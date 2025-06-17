package com.example.mcsexlec

data class NewsArticle(
    val title: String,
    val publishedAt: String?,
    val urlToImage: String?,
    var isSaved: Boolean = false
)
