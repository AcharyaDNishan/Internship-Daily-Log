package com.example.project

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
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