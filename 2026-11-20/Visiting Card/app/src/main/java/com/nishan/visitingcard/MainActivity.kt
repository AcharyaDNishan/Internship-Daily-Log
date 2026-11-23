package com.nishan.visitingcard

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nishan.visitingcard.ui.theme.VisitingCardTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisitingCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InfoGather(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun InfoGather(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var cname by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var emailadd by remember { mutableStateOf("") }
    var phonenum by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VisitingCard(
            name = name,
            companyName = cname,
            position = position,
            email = emailadd,
            phone = phonenum,
            location = loc,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = cname,
                onValueChange = { cname = it },
                label = { Text(text = "Company Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text(text = "Position") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = emailadd,
                onValueChange = { emailadd = it },
                label = { Text(text = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = phonenum,
                onValueChange = { phonenum = it },
                label = { Text(text = "Phone No.") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = loc,
                onValueChange = { loc = it },
                label = { Text(text = "Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun VisitingCard(
    modifier: Modifier = Modifier,
    name: String = "",
    companyName: String = "",
    position: String = "",
    email: String = "",
    phone: String = "",
    location: String = "",
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = modifier
            .padding(16.dp)
            .width(300.dp)
            .height(200.dp)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawContent()
            }
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable {
                coroutineScope.launch {
                    try {
                        val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                        saveCardAsJpeg(context, bitmap)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to capture card: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.ifEmpty { "Name" },
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = companyName.ifEmpty { "Company Name" },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = position.ifEmpty { "Position" },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = email.ifEmpty { "Email" },
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = phone.ifEmpty { "Phone No" },
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = location.ifEmpty { "Location" },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

fun saveCardAsJpeg(context: Context, bitmap: Bitmap) {
    val filename = "VisitingCard_${System.currentTimeMillis()}.jpg"
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VisitingCards")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (imageUri != null) {
        try {
            resolver.openOutputStream(imageUri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            Toast.makeText(context, "Saved $filename to Pictures/VisitingCards", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save card: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show()
    }
}
