package com.example.newsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.edit
import com.example.newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections.list

class SourceActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val searchedQuery: String? = intent.getStringExtra("searchedQuery")

        setContent {
            NewsAppTheme {
                SourceScreen(modifier = Modifier.fillMaxSize(), searchedQuery)
            }
        }
    }
}

@Composable
fun SourceScreen(modifier: Modifier, searchedQuery: String?){
    val context = LocalContext.current
    val prefs = remember{context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}
    var category by remember { mutableStateOf("general") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Searched Query Text
        Text(
            text="Search for: $searchedQuery",
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
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
                        category = "business"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Entertainment") },
                    onClick = {
                        category = "entertainment"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("General") },
                    onClick = {
                        category = "general"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Health") },
                    onClick = {
                        category = "health"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Science") },
                    onClick = {
                        category = "science"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sports") },
                    onClick = {
                        category = "sports"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Technology") },
                    onClick = {
                        category = "technology"
                        expanded = false
                    }
                )
            }
        }
        //Skip Sources
        ElevatedButton(
            onClick = {
                prefs.edit { putString("searchedQuery", searchedQuery) }
            },
            modifier = Modifier.fillMaxWidth(0.4f),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text="Skip Sources")
        }

        Spacer(Modifier.height(20.dp))

        //Source List
        DisplaySources(modifier,category, searchedQuery)
    }
}

@Composable
fun DisplaySources(modifier: Modifier, category: String, searchedQuery: String?){
    val context = LocalContext.current
    val apiKey = context.getString(R.string.NewsKey)
    val newsManager = remember { NewsManager() }

    //getter
    var mySourceList by remember {mutableStateOf<List<Source>>(emptyList())}

    // Launch NewsManager and retrieve all sources + save them to list
    LaunchedEffect(category){
        val result = withContext(Dispatchers.IO){
            newsManager.retrieveSources(apiKey,category)
        }
        mySourceList = result
    }

    // Lazy Column for all business cards
    LazyColumn(modifier = Modifier){
        items(mySourceList){currentSource->
            SourceBusinessCard(
                source = currentSource,
                searchedQuery = searchedQuery,
                modifier = Modifier.padding(4.dp)
            )

        }
    }

}

@Composable
fun SourceBusinessCard(source: Source, searchedQuery: String?, modifier: Modifier = Modifier){
    val context = LocalContext.current
    Card(
        modifier=modifier
        .padding(1.dp)
        .clickable{
            // Save source name
            val prefs = context.getSharedPreferences(
                "my_prefs", Context.MODE_PRIVATE)
            prefs.edit { putString("selectedSource", source.name) } //save source name to preferences

            // Launch result screen
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra("searchedQuery", searchedQuery)
                putExtra("selectedSource", source.name)
            }
            context.startActivity(intent)
        }
    ){
        //Source Card UI
        Row(modifier=Modifier.padding(2.dp)) {
            Column {
                Text(
                    source.name,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier=Modifier.width(2.dp))
                Text(source.description)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SourcePreview() {
    val searchedQuery: String? = ""
    NewsAppTheme {
        SourceScreen(modifier = Modifier, searchedQuery)
    }
}