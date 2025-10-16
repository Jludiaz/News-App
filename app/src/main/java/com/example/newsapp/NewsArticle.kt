package com.example.newsapp

import java.util.Date

data class NewsArticle(
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedDate: String,
    val content: String
)
