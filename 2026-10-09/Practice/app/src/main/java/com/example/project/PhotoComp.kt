package com.example.project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

class PhotoComp(
    private val appContext: Context,
    private val params: WorkerParameters
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val stringUri = params.inputData.getString(KEY_CONTENT_URI)
            val commpressionThresholdInBytes = params.inputData.getLong(
                KEY_COMMPRESSION_THRESHOLD,
                0L
            )
            val uri = Uri.parse(stringUri)
            val bytes = appContext.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputStream = ByteArrayOutputStream()
            var outputByte: ByteArray
            var quality = 100
            do{
                val outputStream= ByteArrayOutputStream()
                outputStream.use{outputStream->
                     bitmap.compress(Bitmap.CompressFormat.JPEG,quality,outputStream)
                    outputByte=outputStream.toByteArray()
                    quality-= (quality*0.1).roundToInt()
                }
            } while (outputByte.size>commpressionThresholdInBytes && quality>5)
            val file= File(appContext.cacheDir, "${params.id}.jpg")
            file.writeBytes(outputByte)
            Result.success(
                workDataOf(
                    KEY_RESULT_PATH to file.absolutePath
                )
            )
        }
    }
    companion object {
            const val KEY_CONTENT_URI = "KEY_CONTENT_URI"
            const val KEY_COMMPRESSION_THRESHOLD = "KEY_COMMPRESSION_THRESHOLD"
            const val KEY_RESULT_PATH = "KEY_RESULT_PATH"
        }


}