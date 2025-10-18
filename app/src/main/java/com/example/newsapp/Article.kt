package com.example.newsapp

data class Article(
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedDate: String,
    val content: String
)
