package com.example.newsapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.Date

class NewsManager {
    val okHttpClient: OkHttpClient

    init {
        val builder= OkHttpClient.Builder()
        val loggingInterceptor= HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor (loggingInterceptor )

        okHttpClient=builder.build()
    }

    suspend fun retrieveNews(apikey: String): List<NewsArticle>{
        val request= Request.Builder()
            .url("https://newsapi.org/v2/everything")
            .header("authorization","Bearer $apikey")
            .get()
            .build()

        val response: Response =okHttpClient.newCall(request).execute()
        val responseBody=response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()){
            val news=mutableListOf<NewsArticle>()
            val json= JSONObject(responseBody)
            val newsJSON=json.getJSONArray("everything")
            for (i in 0 until newsJSON.length()){
                val getCurrentArticle = newsJSON.getJSONObject(i)
                val author = getCurrentArticle.getString("author")
                val title = getCurrentArticle.getString("title")
                val description = getCurrentArticle.getString("description")
                val url = getCurrentArticle.getString("url")
                val urlToImage = getCurrentArticle.getString("urlToImage")
                val publishedDate = getCurrentArticle.getString("publishedDate")
                val content = getCurrentArticle.getString("content")

                val article=NewsArticle(
                    author = author,
                    title = title,
                    description = description,
                    url = url,
                    urlToImage = urlToImage,
                    publishedDate = publishedDate,
                    content = content
                )
                news.add(article)
            }
            return news
        }else{
            return listOf()
        }
    }

    suspend fun retrieveSources(apiKey: String): List<Source>{
        val request = Request.Builder()
            .url("https://newsapi.org/v2/sources?apiKey=$apiKey")
            .header("authorization","Bearer $apiKey")
            .get()
            .build()

        val response: Response =okHttpClient.newCall(request).execute()
        val responseBody=response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()){
            val sources=mutableListOf<Source>()
            val json= JSONObject(responseBody)
            val sourcesJSON=json.getJSONArray("sources")
            for (i in 0 until sourcesJSON.length()){
                val getCurrentSource = sourcesJSON.getJSONObject(i)
                val sourceName = getCurrentSource.getString("name")
                val sourceDescription = getCurrentSource.getString("description")

                val source=Source(
                    name = sourceName,
                    description = sourceDescription
                )
                sources.add(source)
            }

            return sources
        }else{
            return listOf()
        }
    }
}