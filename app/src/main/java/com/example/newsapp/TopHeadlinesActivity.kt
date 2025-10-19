package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import coil.compose.AsyncImage

class TopHeadlinesActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewsAppTheme {
                Log.d("NewsDebug", "ResultsActivity created")
                TopHeadlinesScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    fun TopHeadlinesScreen(
        modifier: Modifier
    ){
        val context = LocalContext.current

        // Save to Headline Category to Preferences
        var selectedHeadlineCategory by remember { mutableStateOf("general") }
        val prefs = context.getSharedPreferences(
            "my_prefs", Context.MODE_PRIVATE)
        prefs.edit {
            putString("selectedHeadlineCategory", selectedHeadlineCategory)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Results Header: "[source] results for [query]
            Text(
                text = "Results For $selectedHeadlineCategory ",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            //Categories Dropdown Menu
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { expanded = !expanded},
                    modifier = Modifier.fillMaxWidth(0.4f).align(Alignment.CenterEnd)
                ) {
                    Text(text="Categories")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Categories")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Business") },
                        onClick = {
                            selectedHeadlineCategory = "business"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Entertainment") },
                        onClick = {
                            selectedHeadlineCategory = "entertainment"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("General") },
                        onClick = {
                            selectedHeadlineCategory = "general"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Health") },
                        onClick = {
                            selectedHeadlineCategory = "health"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Science") },
                        onClick = {
                            selectedHeadlineCategory = "science"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sports") },
                        onClick = {
                            selectedHeadlineCategory = "sports"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Technology") },
                        onClick = {
                            selectedHeadlineCategory = "technology"
                            expanded = false
                        }
                    )
                }
            }

            //Display News Articles
            DisplayTopHeadlines(
                modifier = Modifier.fillMaxSize(),
                selectedHeadlineCategory
            )
        }
    }

    @Composable
    fun DisplayTopHeadlines(modifier: Modifier, selectedHeadlineCategory: String?) {
        val context = LocalContext.current
        val apiKey = context.getString(R.string.NewsKey)
        val newsManager = remember { NewsManager() }

        var myArticleList by remember {mutableStateOf<List<Article>>(emptyList())}

        //Launch NewsManager
        LaunchedEffect(selectedHeadlineCategory){
            Log.d("NewsDebug", "Launching retrieveTopHeadlines for category = $selectedHeadlineCategory")

            val result = withContext(Dispatchers.IO){
                newsManager.retrieveTopHeadlines(apiKey,selectedHeadlineCategory)
            }

            Log.d("NewsDebug", "Got ${result.size} top headline articles")
            myArticleList = result
        }

        // Lazy Column for all business cards
        LazyColumn(modifier = Modifier){
            items(myArticleList){currentArticle->
                ArticleBusinessCard(
                    article = currentArticle,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }

    @Composable
    fun ArticleBusinessCard(article: Article, modifier: Modifier = Modifier){
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
                    AsyncImage(
                        model = article.urlToImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(400.dp)
                            .padding(1.dp)
                    )
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