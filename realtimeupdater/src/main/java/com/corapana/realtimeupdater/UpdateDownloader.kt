package com.corapana.realtimeupdater

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object UpdateDownloader {

    interface Listener {
        fun onProgress(progress: Int)
        fun onDownloaded(file: File)
        fun onError(e: Exception)
    }

    fun start(context: Context, info: UpdateInfo, listener: Listener) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(info.apkUrl)
                val connection = url.openConnection()
                val length = connection.contentLength
                val input = BufferedInputStream(url.openStream())
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
                val output = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var total: Long = 0
                var count: Int

                while (input.read(buffer).also { count = it } != -1) {
                    total += count
                    output.write(buffer, 0, count)
                    val progress = ((total * 100) / length).toInt()
                    withContext(Dispatchers.Main) {
                        listener.onProgress(progress)
                    }
                }

                output.flush()
                output.close()
                input.close()

                withContext(Dispatchers.Main) {
                    listener.onDownloaded(file)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError(e)
                }
            }
        }
    }
}

