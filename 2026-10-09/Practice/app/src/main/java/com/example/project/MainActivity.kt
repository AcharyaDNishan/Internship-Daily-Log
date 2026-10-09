package com.example.project

import android.app.ComponentCaller
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
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
    private val ViewModel by viewModels<PhotoVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            ProjectTheme {
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
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Arrangement.Center) {
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
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri=if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        }?:return
        viewModel.updateUncompressedUri(uri)
        val request= OneTimeWorkRequestBuilder<PhotoComp>()
            .setInputData(
                workDataOf(
                    PhotoComp.KEY_CONTENT_URI to uri.toString(),
                    PhotoComp.KEY_COMMPRESSION_THRESHOLD to 1024 * 20L
                )
            )
            .setConstraints(
                Constraints(requiresSorageNotLow=true)
            )
            .build()
        ViewModel.updateWorkId(request.id)
         workManager.enqueue(request)

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectTheme {
        Greeting("Android")
    }
}