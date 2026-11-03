package com.example.walkroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walkroute.ui.theme.WalkRouteTheme
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            // Location permission granted, enable location features
        } else {
            var permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    // Optionally show a dialog explaining why the app needs location permission
                }
                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        // Permission granted, enable location features
                    } else {
                        // Permission denied, show a message or disable location features
                    }
                }
            })
            // Trigger the permission request
            permissionsManager.requestLocationPermissions(this)
        }
        enableEdgeToEdge()
        setContent {
            WalkRouteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        MapScreen(
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onRequestPermissionsResult: Nothing?.(Int, Array<out String?>, IntArray) -> Unit
    ) {
        val permissionsManager = null
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    var zoomLevel by remember { mutableDoubleStateOf(10.0) }
    var currentStyle by remember { mutableStateOf(Style.STANDARD) }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions(
            CameraOptions.Builder()
                .center(Point.fromLngLat(85.3240, 27.7172))
                .zoom(zoomLevel)
                .build(),
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.weight(1f),
            mapViewportState = mapViewportState,
            style = {
                MapStyle(style = currentStyle)
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = {
                    zoomLevel++
                    mapViewportState.setCameraOptions(
                        CameraOptions.Builder().zoom(zoomLevel).build(),
                    )
                },
            ) {
                Text("Zoom In")
            }

            Button(
                onClick = {
                    zoomLevel--
                    mapViewportState.setCameraOptions(
                        CameraOptions.Builder().zoom(zoomLevel).build(),
                    )
                },
            ) {
                Text("Zoom Out")
            }
            Button(
                onClick = {
                    currentStyle = if (currentStyle == Style.STANDARD) {
                        Style.STANDARD_SATELLITE
                    } else {
                        Style.STANDARD
                    }
                },
            ) {
                Text(if (currentStyle == Style.STANDARD) "Satellite" else "Standard")
            }
        }
    }
}


