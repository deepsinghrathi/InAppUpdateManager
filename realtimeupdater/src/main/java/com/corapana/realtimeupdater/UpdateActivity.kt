package com.corapana.realtimeupdater

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri

class UpdateActivity : ComponentActivity() {

    private lateinit var info: UpdateInfo
    private lateinit var progressBar: ProgressBar
    private lateinit var updateButton: Button
    private lateinit var installButton: Button
    private lateinit var statusText: TextView
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        info = intent.getParcelableCompat("update_info")
            ?: run { finish(); return }

        val changelog = findViewById<TextView>(R.id.changelog)
        val versionName = findViewById<TextView>(R.id.versionName)
        updateButton = findViewById(R.id.updateButton)
        installButton = findViewById(R.id.installButton)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
        logo = findViewById(R.id.logo)

        versionName.text = "New Version: ${info.latestVersionName}"
        changelog.text = info.whatsNew
        val  appIcon:Bitmap? = intent.getParcelableCompat("app_icon")
        appIcon?.let { logo.setImageBitmap(it) }

        updateButton.setOnClickListener {
            startDownload(info.apkUrl)
        }
        installButton.setOnClickListener {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
            installApk(file)
        }

    }

    private fun startDownload(url: String) {
        updateButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = false
        statusText.text = "Downloading update…"

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val length = connection.contentLength
                var downloaded = 0L

                connection.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var count: Int
                        while (input.read(buffer).also { count = it } != -1) {
                            output.write(buffer, 0, count)
                            downloaded += count

                            // update progress
                            val progress = if (length > 0) ((downloaded * 100) / length).toInt() else -1
                            runOnUiThread {
                                if (progress >= 0) {
                                    progressBar.progress = progress
                                    statusText.text = "Downloading update… $progress%"
                                } else {
                                    statusText.text = "Downloading update…"
                                }
                            }
                        }
                    }
                }

                runOnUiThread {
                    installButton.visibility = View.VISIBLE
                    statusText.text = "Download complete"
                    installApk(file)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    statusText.text = "Download failed: ${e.message}"
                    updateButton.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }.start()
    }

    private var pendingApkFile: File? = null

    // Activity Result launcher
    private val unknownSourcesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (packageManager.canRequestPackageInstalls()) {
                    pendingApkFile?.let { doInstall(it) }
                } else {
                    Toast.makeText(this, "Permission required to install updates", Toast.LENGTH_LONG).show()
                }
            }
        }


    private fun installApk(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !packageManager.canRequestPackageInstalls()
        ) {
            // Save file reference to install later
            pendingApkFile = file

            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = "package:$packageName".toUri()
            }
            unknownSourcesLauncher.launch(intent) // ✅ modern way
            return
        }

        // Already allowed → proceed
        doInstall(file)
    }

    private fun doInstall(file: File) {
        val apkUri: Uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    companion object {
        fun launch(context: Context, info: UpdateInfo, appIcon: Bitmap) {
            val intent = Intent(context, UpdateActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("update_info", info)
                putExtra("app_icon",appIcon)
            }
            context.startActivity(intent)
        }
    }
}
