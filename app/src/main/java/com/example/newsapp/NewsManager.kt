package com.example.newsapp

import android.net.Uri
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class NewsManager {
    val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    suspend fun retrieveArticles(
        apiKey: String,
        searchedQuery: String?,
        selectedSourceID: String?,
        selectedSourceName: String?
    ): List<Article> {
        val urlBuilder = StringBuilder("https://newsapi.org/v2/everything?apiKey=$apiKey")

        // Add query parameters if they exist
        searchedQuery?.let {
            if (it.isNotEmpty()) urlBuilder.append("&q=${Uri.encode(it)}")
        }

        // Skip Sources Button
        if (selectedSourceID != "SKIP SOURCES") {
            // Add sources parameters if they exist
            selectedSourceID?.let {
                if (it.isNotEmpty()) urlBuilder.append("&sources=${Uri.encode(it)}")
            }
        }

        // Establish my request
        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("Authorization", "Bearer $apiKey")
            .get()
            .build()

        // Debug my response call in case I don't establish a connection
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()
        if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
            Log.e("NewsDebug", "HTTP error: ${response.code} - ${responseBody ?: "Empty"}")
            return emptyList()
        }

        try {
            // In case API does not fetch an article field
            val json = JSONObject(responseBody)
            if (!json.has("articles")) {
                Log.e("NewsDebug", "No 'articles' field in response: $responseBody")
                return emptyList()
            }

            // Get json information
            val articles = mutableListOf<Article>()
            val newsJSON = json.getJSONArray("articles")

            for (i in 0 until newsJSON.length()) {
                val articleObj = newsJSON.getJSONObject(i)
                val sourceObj = articleObj.getJSONObject("source")
                //Log.d("NewsDebug", "Created JSON sourceOBJ")

                val article = Article(
                    sourceId = sourceObj.optString("id", ""),
                    sourceName = sourceObj.optString("name", ""),
                    author = articleObj.optString("author", "Unknown"),
                    title = articleObj.optString("title", "No title"),
                    description = articleObj.optString("description", "No description"),
                    url = articleObj.optString("url", ""),
                    urlToImage = articleObj.optString("urlToImage", ""),
                    publishedDate = articleObj.optString("publishedAt", ""),
                    content = articleObj.optString("content", "")
                )
                // Add article to the source
                if (selectedSourceID == "SKIP SOURCES" || (selectedSourceName == article.sourceName || selectedSourceID == article.sourceId)) {
                    articles.add(article)
                }
            }

            Log.d("NewsDebug", "Fetched ${articles.size} articles successfully")
            return articles

        } catch (e: Exception) {
            Log.e("NewsDebug", "JSON parsing failed", e)
            return emptyList()
        }
    }

    suspend fun retrieveSources(
        apiKey: String,
        selectedCategory: String
    ): List<Source> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/sources?apiKey=$apiKey")
            .header("authorization", "Bearer $apiKey")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources = mutableListOf<Source>()
            val json = JSONObject(responseBody)
            val sourcesJSON = json.getJSONArray("sources")

            for (i in 0 until sourcesJSON.length()) {
                val currentSource = sourcesJSON.getJSONObject(i)
                val sourceCategory = currentSource.getString("category")

                //Log.d("NewsDebug", "Category selected: $categorySelection")

                if (selectedCategory.isEmpty() || selectedCategory == sourceCategory) {
                    sources.add(
                        Source(
                            id = currentSource.getString("id"),
                            name = currentSource.getString("name"),
                            description = currentSource.getString("description"),
                            category = sourceCategory
                        )
                    )
                }
            }
            return sources
        } else {
            return listOf()
        }
    }

    suspend fun retrieveTopHeadlines(
        apiKey: String,
        selectedHeadlineCategory: String?
    ): List<Article> {
        Log.d("NewsDebug", "Building URL String: ")
        val urlBuilder = StringBuilder("https://newsapi.org/v2/top-headlines?apiKey=$apiKey")

        // Add category parameter if provided
        selectedHeadlineCategory?.let {
            if (it.isNotEmpty()) urlBuilder.append("&category=${Uri.encode(it)}")
        }
        // Establish my request
        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("Authorization", "Bearer $apiKey")
            .get()
            .build()

        // Debug my response call in case I don't establish a connection
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()
        if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
            Log.e("NewsDebug", "retrieveTopHeadline HTTP error: ${response.code} - ${responseBody ?: "Empty"}")
            return emptyList()
        }

        try {
            // In case API does not fetch an article field
            val json = JSONObject(responseBody)
            if (!json.has("articles")) {
                Log.e("NewsDebug", "retrieveTopHeadline No 'articles' field in response: $responseBody")
                return emptyList()
            }

            // Get json information
            val articles = mutableListOf<Article>()
            val newsJSON = json.getJSONArray("articles")

            for (i in 0 until newsJSON.length()) {
                val articleObj = newsJSON.getJSONObject(i)
                val sourceObj = articleObj.getJSONObject("source")
                //Log.d("NewsDebug", "Created JSON sourceOBJ")

                val article = Article(
                    sourceId = sourceObj.optString("id", ""),
                    sourceName = sourceObj.optString("name", ""),
                    author = articleObj.optString("author", "Unknown"),
                    title = articleObj.optString("title", "No title"),
                    description = articleObj.optString("description", "No description"),
                    url = articleObj.optString("url", ""),
                    urlToImage = articleObj.optString("urlToImage", ""),
                    publishedDate = articleObj.optString("publishedAt", ""),
                    content = articleObj.optString("content", "")
                )

                // Add article to the source
                articles.add(article)
            }

            Log.d("NewsDebug", "retrieveTopHeadline Fetched ${articles.size} articles successfully")
            return articles

        } catch (e: Exception) {
            Log.e("NewsDebug", "retrieveTopHeadline JSON parsing failed", e)
            return emptyList()
        }
    }
}