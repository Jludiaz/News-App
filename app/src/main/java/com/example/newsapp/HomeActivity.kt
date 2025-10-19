package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

class HomeActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                HomeActivity(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun HomeActivity(modifier: Modifier){
    val context = LocalContext.current
    val prefs = remember{context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}

    var searchedQuery by remember {
        (mutableStateOf(prefs.getString("searchedQuery", "")?:"Search Query"))
    }
    var searchOnClick: () -> Unit
    var isButtonEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Search Bar and Button to Search Query
        Row {
            TextField(
                value = searchedQuery,
                onValueChange = {searchedQuery = it},
                label = { Text("Search news, topics, and more")},
                placeholder = {Text("Enter Username")},
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
            )
            Button(
                onClick = {
                    prefs.edit { putString("searchedQuery", searchedQuery) }
                    val intentSource = Intent(context,SourceActivity()::class.java)
                    intentSource.putExtra("searchedQuery", searchedQuery.toString())
                    context.startActivity(intentSource)
                },
                enabled = isButtonEnabled,
                modifier = Modifier.size(width = 85.dp, height = 55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonColors(Color.Green,Color.Black,Color.Black,Color.Red)
            ) {
                Text(text="Enter")
            }
        }

        Spacer(Modifier.height(100.dp))

        //TOP HEADLINES Activity Button
        TextButton(onClick = {
            val intent = Intent(context, TopHeadlinesActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(
                text = "TOP HEADLINES",
                color = Color.Black,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 45.sp,
                )
            )
        }

        Spacer(Modifier.height(100.dp))

        // LOCAL NEWS Activity Button
        TextButton(onClick =
            {
                val intent = Intent(context, LocalNewsActivity::class.java)
                context.startActivity(intent)
        }) {
            Text(
                text = "LOCAL NEWS",
                color = Color.Black,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 45.sp,
                )
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NewsAppTheme {
        HomeActivity(modifier = Modifier)
    }
}

