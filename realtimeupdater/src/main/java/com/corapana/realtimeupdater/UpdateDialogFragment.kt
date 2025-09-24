package com.corapana.realtimeupdater

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.io.File


class UpdateDialogFragment : DialogFragment() {

    private lateinit var info: UpdateInfo
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info = requireArguments().getParcelableCompat("info", UpdateInfo::class.java)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        progressBar = root.findViewById(R.id.updateProgress)
        statusText = root.findViewById(R.id.updateStatus)
        updateButton = root.findViewById(R.id.updateButton)

        progressBar.visibility = View.GONE
        statusText.text = "What's new:\n\n${info.whatsNew}"
        updateButton.text = "Update Now"

        updateButton.setOnClickListener {
            startDownload()
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Update v${info.latestVersionName}")
            .setView(root)

        if (!info.forceUpdate) {
            builder.setNegativeButton("Later", null)
        } else {
            builder.setCancelable(false) // force update mode
        }

        return builder.create()
    }

    private fun startDownload() {
        updateButton.isEnabled = false
        updateButton.text = "Downloading..."
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = false
        progressBar.progress = 0
        statusText.text = "Downloading update..."

        UpdateDownloader.start(requireContext(), info, object : UpdateDownloader.Listener {
            override fun onProgress(progress: Int) {
                progressBar.progress = progress
                statusText.text = "Downloading... $progress%"
            }

            override fun onDownloaded(file: File) {
                showInstallingState(file)
            }

            override fun onError(e: Exception) {
                statusText.text = "Download failed: ${e.message}"
                updateButton.text = "Retry"
                updateButton.isEnabled = true
            }
        })
    }

    private fun showInstallingState(file: File) {
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        statusText.text = "Installing update..."
        updateButton.text = "Installing..."
        updateButton.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            UpdateInstaller.install(requireContext(), file)
            dismiss()
        }, 1500)
    }
    companion object {
        @JvmStatic
        fun newInstance(info: UpdateInfo): UpdateDialogFragment {
            return UpdateDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("info", info)
                }
            }
        }
    }
}



