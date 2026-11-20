package com.nishan.visitingcard

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nishan.visitingcard.ui.theme.VisitingCardTheme

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
    Box(
        modifier = modifier
            .padding(16.dp)
            .width(300.dp)
            .height(200.dp)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable {
                downloadFile(context, "/", "Visiting Card.docx")
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

fun downloadFile(context: Context, url: String, fileName: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(fileName)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}
