package com.isu.apitracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream



object FileMaker {

    fun createTxtFile(context: Context, fileName: String, content: String){
        // Determine where to save the file
        val filePath = File(context.getExternalFilesDir(null), "$fileName.txt")

        // Write the content to the file
        FileOutputStream(filePath).use { outputStream ->
            outputStream.write(content.toByteArray())
        }
        shareFile(context,filePath)
    }


   private fun shareFile(context: Context, file: File) {
        // Get the content URI for the file using FileProvider
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // Create an Intent to share the file
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Adjust the MIME type if necessary
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start the share intent
        context.startActivity(Intent.createChooser(shareIntent, "Share file via"))
    }

}