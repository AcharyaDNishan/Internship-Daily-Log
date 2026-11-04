package com.example.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.map.ui.theme.MapTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapTheme {
                MapboxMap(

                    modifier = Modifier.fillMaxSize(),

                    mapViewportState = rememberMapViewportState {

                        setCameraOptions {

                            // Kathmandu coordinates
                            center(
                                Point.fromLngLat(
                                    85.3240,
                                    27.7172
                                )
                            )

                            // Zoom level
                            zoom(12.0)

                        }

                    }

                )
            }

        }
    }
}