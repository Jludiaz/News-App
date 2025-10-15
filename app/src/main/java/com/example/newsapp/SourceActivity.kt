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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import java.util.Collections.list

class SourceActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val searchedQuery: String? = intent.getStringExtra("searchedQuery")

        setContent {
            NewsAppTheme {
                SourceActivity(modifier = Modifier.fillMaxSize(),
                    searchedQuery)
            }
        }
    }
}

@Composable
fun SourceActivity(modifier: Modifier, searchedQuery: String?){
    var context = LocalContext.current
    val prefs = remember{context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Searched Query Text
        Text(
            text="Search for: $searchedQuery"
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
                    text = { Text("Option 1") },
                    onClick = { /* Do something... */ }
                )
                DropdownMenuItem(
                    text = { Text("Option 2") },
                    onClick = { /* Do something... */ }
                )
            }
        }
        //Skip Sources
        Button(
            onClick = {
                prefs.edit { putString("searchedQuery", searchedQuery) }
            },
            modifier = Modifier.size(width = 150.dp, height = 55.dp),
            shape = RoundedCornerShape(15.dp),
        ) {
            Text(text="Skip Sources")
        }

        Spacer(Modifier.height(20.dp))

        //Source List
        displaySources()

    }
}

@Composable
fun displaySources(){
    val mySourceList= getFakeData()
    LazyColumn {
        items(mySourceList){currentSource->
            SourceBusinessCard(
                source = currentSource,
                modifier=Modifier.padding(1.dp)
            )
        }
    }
}

@Composable
fun SourceBusinessCard(source: Source, modifier:Modifier=Modifier){
    Card(modifier=Modifier.fillMaxWidth()
        .padding(1.dp)){
        Row(modifier=Modifier.padding(2.dp)) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(1.dp)
            )
            Spacer(modifier=Modifier.width(5.dp))
            Column {
                Text(source.name)
                Text(source.description)
            }
        }
    }
}


fun getFakeData(): List<Source>{
    return listOf(
        Source("TechCrunch", "The latest technology news and information on startups."),
        Source("BBC News", "Trusted global news coverage and analysis."),
        Source("ESPN", "Sports news, scores, and highlights."),
        Source("National Geographic", "Exploring science, nature, and culture worldwide."),
        Source("Reuters", "Breaking international business and financial news.")
    )
}

@Preview(showBackground = true)
@Composable
fun SourcePreview() {
    val searchedQuery: String? = ""
    NewsAppTheme {
        SourceActivity(modifier = Modifier, searchedQuery)
    }
}