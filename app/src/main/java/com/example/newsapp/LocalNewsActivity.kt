package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.newsapp.ui.theme.NewsAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocalNewsActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                LocalNewsScreen()
            }
        }
    }

    @Composable
    fun LocalNewsScreen() {
        val context = LocalContext.current
        val apiKey = context.getString(R.string.NewsKey)
        val newsManager = remember { NewsManager() }

        var markerPosition by remember { mutableStateOf<LatLng?>(null) }
        var selectedLocation by remember { mutableStateOf("") }
        var locationName by remember { mutableStateOf<String?>(null) }
        var myLocalArticlesList by remember { mutableStateOf<List<Article>>(emptyList()) }

        // Default Location to Washington DC
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(38.8951, -77.0364),
                4f
            ) // Default: Washington, DC
        }

        // Save location to preferences
        val prefs = context.getSharedPreferences(
            "my_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("selectedLocation", selectedLocation) } //save source name to preferences

        //Box with Map UI, Maker State Functionality, News Manager Retrieval, and Article Display Surface
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng ->
                    markerPosition = latLng
                    // Geocoder functionality to translate coordinate into name
                    locationName = getPlaceNameFromCoordinates(latLng.latitude, latLng.longitude)
                    selectedLocation = "Lat ${latLng.latitude}, Lng ${latLng.longitude}"
                }
            ) {
                // Marker position state
                markerPosition?.let { pos ->
                    Marker(
                        state = MarkerState(position = pos),
                        title = "Selected Location",
                        snippet = selectedLocation
                    )
                }
            }

            // Retrieve Articles News Manager
            LaunchedEffect(locationName) {
                myLocalArticlesList = withContext(Dispatchers.IO) {
                    newsManager.retrieveArticlesForLocation(apiKey, locationName)
                }
            }

            // Inform user bottom of the screen if list == empty
            if (myLocalArticlesList.isEmpty() && selectedLocation.isNotEmpty()) {
                Text(
                    text = "No news found for $selectedLocation",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }

            // Show LocalNews Overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(500.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "News for: $locationName",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(myLocalArticlesList) { currentArticle ->
                            ArticleBusinessCard(
                                article = currentArticle,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
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

    // Convert Coordinates to Place Name
    fun getPlaceNameFromCoordinates(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
            } else null
        } catch (e: Exception) {
            null
        }
    }
}