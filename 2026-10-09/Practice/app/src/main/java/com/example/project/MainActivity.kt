package com.example.project

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.project.ui.theme.ProjectTheme

class MainActivity : ComponentActivity() {
    private lateinit var workManager: WorkManager
    private val mm by viewModels<ImageC>()
    private val ViewModel by viewModels<PhotoVM>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadLastImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            loadLastImage()
        } else {
            requestPermissionLauncher.launch(permission)
        }

        enableEdgeToEdge()
        setContent {
            ProjectTheme {
                val workerResult = ViewModel.workId?.let { id ->
                    workManager.getWorkInfoByIdLiveData(id)
                        .observeAsState()
                        .value
                }

                LaunchedEffect(key1 = workerResult?.outputData) {
                    if (workerResult?.outputData != null) {
                        val filePath = workerResult.outputData.getString(
                            PhotoComp.KEY_RESULT_PATH
                        )
                        filePath?.let {
                            val bitmap = BitmapFactory.decodeFile(it)
                            ViewModel.updatedCompressedBitmap(bitmap)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Last Picture in Gallery")
                    Spacer(modifier = Modifier.height(8.dp))

                    mm.images.firstOrNull()?.let { image ->
                        Text(image.name)
                        AsyncImage(
                            model = image.uri,
                            contentDescription = null,
                            modifier = Modifier.height(200.dp)
                        )
                    } ?: run {
                        Text("No images found in gallery")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ViewModel.uncompressedUri?.let {
                        Text("Uncompressed Photo")
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.height(150.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ViewModel.compressedBitmap?.let {
                        Text("Compressed Photo")
                        AsyncImage(
                            model = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.height(150.dp)
                        )
                    }
                }
            }
        }
    }

    private fun loadLastImage() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                mm.updateImages(listOf(Image(id, name, uri)))
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        } ?: return

        ViewModel.updateUncompressedUri(uri)
        val request = OneTimeWorkRequestBuilder<PhotoComp>()
            .setInputData(
                workDataOf(
                    PhotoComp.KEY_CONTENT_URI to uri.toString(),
                    PhotoComp.KEY_COMMPRESSION_THRESHOLD to 1024 * 20L
                )
            )
            .setConstraints(
                Constraints(requiresStorageNotLow = true)
            )
            .build()
        ViewModel.updateWorkId(request.id)
        workManager.enqueue(request)
    }
}
