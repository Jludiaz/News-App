package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.newsapp.ui.theme.NewsAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    @OptIn(UnstableApi::class)
    @Composable
    fun LocalNewsScreen() {
        val context = LocalContext.current
        val apiKey = context.getString(R.string.NewsKey)
        val newsManager = remember { NewsManager() }

        var markerPosition by remember { mutableStateOf<LatLng?>(null) }
        var selectedLocation by remember { mutableStateOf("") }
        var myLocalArticlesList by remember { mutableStateOf<List<Article>>(emptyList()) }

        // Default Location to Washington DC
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(38.8951, -77.0364),
                4f
            ) // Default: Washington, DC
        }

        // Fetch articles when user selects a new location
        LaunchedEffect(selectedLocation) {
            if (selectedLocation.isNotEmpty()) {
                Log.d("NewsDebug", "Fetching news for: $selectedLocation")
                val result = withContext(Dispatchers.IO) {
                    newsManager.retrieveArticlesForLocation(apiKey, selectedLocation)
                }
                myLocalArticlesList = result
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng ->
                    markerPosition = latLng
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

            // If there is no available location inform user bottom of the screen
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
                    .height(300.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "News for: $selectedLocation",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(myLocalArticlesList) { article ->
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}