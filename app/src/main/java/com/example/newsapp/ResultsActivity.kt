package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class ResultsActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val searchedQuery = intent.getStringExtra("searchedQuery") // Query Search
        val selectedSourceId = intent.getStringExtra("selectedSourceId") // Source ID
        val selectedSourceName = intent.getStringExtra("selectedSourceName") // Source Name

        setContent {
            NewsAppTheme {
                Log.d("NewsDebug", "ResultsActivity created")
                ResultsScreen(
                    modifier = Modifier.fillMaxSize(),
                    searchedQuery = searchedQuery,
                    selectedSourceID = selectedSourceId,
                    selectedSourceName = selectedSourceName
                )
            }
        }
    }

    @Composable
    fun ResultsScreen(
        modifier: Modifier,
        searchedQuery: String?,
        selectedSourceID: String?,
        selectedSourceName: String?
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Results Header: "[source] results for [query]
            Text(
                text = "${selectedSourceName ?: ""} " +
                        "Results For ${searchedQuery ?: "NO QUERY"} ",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            //Display News Articles
            DisplayNews(
                modifier = Modifier.fillMaxSize(),
                searchedQuery,
                selectedSourceID,
                selectedSourceName)
        }
    }

    @Composable
    fun DisplayNews(modifier: Modifier, searchedQuery: String?, selectedSourceID: String?, selectedSourceName: String?) {
        val context = LocalContext.current
        val apiKey = context.getString(R.string.NewsKey)
        val newsManager = remember { NewsManager() }

        var myArticleList by remember {mutableStateOf<List<Article>>(emptyList())}

        //Launch NewsManager
        LaunchedEffect(searchedQuery, selectedSourceID){
            Log.d("NewsDebug", "Launching retrieveArticles for query=$searchedQuery source=$selectedSourceID")

            val result = withContext(Dispatchers.IO){
                newsManager.retrieveArticles(apiKey,searchedQuery, selectedSourceID, selectedSourceName)
            }

            Log.d("NewsDebug", "Got ${result.size} articles")
            myArticleList = result
        }

        // Lazy Column for all business cards
        LazyColumn(modifier = Modifier){
            items(myArticleList){currentArticle->
                ArticleBusinessCard(
                    article = currentArticle,
                    searchedQuery = searchedQuery,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }

    @Composable
    fun ArticleBusinessCard(article: Article, searchedQuery: String?, modifier: Modifier = Modifier){
        val context = LocalContext.current
        Card(
            modifier = modifier
                .padding(1.dp)
                .clickable{
                    val articleCardIntent = Intent(Intent.ACTION_VIEW).apply{
                        data= article.url.toUri()
                    }
                    context.startActivity(articleCardIntent)
                }
        ) {
            Row(modifier=Modifier.padding(2.dp)){
                Column{
                    Text(
                        article.title,
                        fontWeight= FontWeight.Bold
                    )
                    Spacer(modifier=Modifier.width(2.dp))
                    Text(
                        article.sourceName,
                        fontWeight= FontWeight.Medium
                    )
                    Spacer(modifier=Modifier.width(2.dp))
                    Text(
                        article.description,
                        fontWeight= FontWeight.Light
                    )
                }
            }
        }
    }
}