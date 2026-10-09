package com.example.project

import android.app.ComponentCaller
import android.content.ContentUris
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.project.ui.theme.ProjectTheme
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    private lateinit var workManager: WorkManager
    private val mm by viewModels<ImageC>()
    private val ViewModel by viewModels<PhotoVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val milisYesterday= Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR,-1)
        }
        val selection = "${MediaStore.Images.Media.DATE_TAKEN}>=?e"
        val selectionArgs= arrayOf(milisYesterday.toString())
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs
        )?.use{
            cursor->
            val idColumn=cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val image=mutableListOf<Image>()
            while(cursor.moveToNext()){
                val id=cursor.getLong(idColumn)
                val name=cursor.getString(nameColumn)
                val uri= ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                )
                image.add(Image(id,name,uri))
                mm.updateImages(image)
        }
        workManager = WorkManager.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            ProjectTheme {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items
                }


                val workerResult=ViewModel.workId?.let{id->
                    workManager.getWorkInfoByIdLiveData(id)
                        .observeAsState()
                        .value

                }
                LaunchedEffect(key1= workerResult?.outputData){
                    if(workerResult?.outputData!=null){
                        val filePath = workerResult.outputData.getString(
                            PhotoComp.KEY_RESULT_PATH
                        )
                        filePath?.let{
                            val bitmap  = BitmapFactory.decodeFile(it)
                            ViewModel.updatedCompressedBitmap(bitmap)
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                    ViewModel.uncompressedUri?.let {
                        Text("Uncompressed Photo")
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier. height(16.dp))
                    ViewModel.compressedBitmap?.let{
                        Text("Compressed Photo")
                        AsyncImage(
                            model = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ViewModel.compressedBitmap?.let{
                        Text("Compressed Photo")
                    }
                }
            }}

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri=if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        }?:return
        ViewModel.updateUncompressedUri(uri)
        val request= OneTimeWorkRequestBuilder<PhotoComp>()
            .setInputData(
                workDataOf(
                    PhotoComp.KEY_CONTENT_URI to uri.toString(),
                    PhotoComp.KEY_COMMPRESSION_THRESHOLD to 1024 * 20L
                )
            )
            .setConstraints(
                Constraints(requiresStorageNotLow=true)
            )
            .build()
        ViewModel.updateWorkId(request.id)
         workManager.enqueue(request)

    }
}

data class Image(
    val id: Long,
    val name: String,
    val uri: Uri
    )