package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.edit
import com.example.newsapp.ui.theme.NewsAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                LoginActivity(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun LoginActivity(modifier: Modifier) {
    val context = LocalContext.current
    val prefs = remember{context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}

    var password by remember {mutableStateOf("")}
    var username by remember {
        (mutableStateOf(prefs.getString("username", "")?:"Enter username"))
    }

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        //Username
        Text(
            text="Username",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start).padding(60.dp,0.dp,0.dp,10.dp),
        )
        TextField(
            value = username,
            onValueChange = {username = it},
            label = { Text("") },
            placeholder = {Text("Enter Username")},
            singleLine = true,
            shape = RoundedCornerShape(15.dp)
        )
        Spacer(Modifier.height(15.dp))
        //Password
        Text(
            text="Password",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start).padding(60.dp,0.dp,0.dp,10.dp),
        )
        TextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("") },
            shape = RoundedCornerShape(15.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(15.dp))
        //Enter Button
        Button(
            onClick = {
                prefs.edit{putString("username",username)}
                val intent = Intent(context,HomeActivity()::class.java)
                context.startActivity(intent)
            },
            enabled = checkUsernamePassword(username,password)
        ) {
            //functionality
            Text(text= "Enter")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    NewsAppTheme {
        LoginActivity(modifier = Modifier)
    }
}

fun checkUsernamePassword(username: String, password: String): Boolean{
    return username.length >= 5 && password.length >= 8
}