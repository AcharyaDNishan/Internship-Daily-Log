
package com.example.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.map.ui.theme.MapTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.compose.style.sources.generated.rememberVectorSourceState
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.StringValue
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapTheme {
                val vectorSource = rememberVectorSourceState(
                    sourceId = "my-vector-source"
                )
                vectorSource.url = StringValue("mapbox://nishann.3dbfi3ma2q25")
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    style={
                        // defined in the strongly typed MapboxStandardStyle and MapboxStandardSatelliteStyle composable function.
                        MapboxStandardStyle(
                            middleSlot = {
                                LineLayer(
                                    sourceState = rememberGeoJsonSourceState {
                                        data = GeoJSONData("asset://line-source.geojson")
                                    }
                                ) {
                                    lineColor = ColorValue(Color(255, 156, 0))
                                }
                            }
                        )
                    },
                    mapViewportState = rememberMapViewportState {
                        setCameraOptions {
                            center(
                                Point.fromLngLat(
                                    85.3240,
                                    27.7172
                                )
                            )
                            zoom(12.0)
                        }
                    }
                ) {
                    CircleLayer(
                        sourceState = vectorSource,
                        layerId = "my-circle-layer"
                    ) {
                        sourceLayer = StringValue("some-source-layer")
                        circleColor = ColorValue(Color.Red)
                    }
                }
            }
        }
    }
}