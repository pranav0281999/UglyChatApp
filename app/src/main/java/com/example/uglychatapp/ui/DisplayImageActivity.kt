package com.example.uglychatapp.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.uglychatapp.MainApplication
import com.example.uglychatapp.R
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.util.*

class DisplayImageActivity : AppCompatActivity() {
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        imageView = findViewById(R.id.display_image_iv)

        if (intent.hasExtra("imagePath")) {
            val imagePath = intent.getStringExtra("imagePath")

            imageView?.setImageURI(Uri.parse(imagePath))

            val uuid = UUID.randomUUID().toString()
//        }
            try {
                val multipartUploadRequest = MultipartUploadRequest(this, serverUrl = "http://" + MainApplication.serverNode + ":" + MainApplication.serverNodePort + "/chat/image")
                        .setMethod("POST")
                        .addHeader("filename", uuid)
                        .addFileToUpload(
                                filePath = imagePath,
                                parameterName = "image"
                        )

                multipartUploadRequest.subscribe(this@DisplayImageActivity, this@DisplayImageActivity, object : RequestObserverDelegate {
                    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                        Log.v(TAG, uploadInfo.uploadedBytes.toString())
                    }

                    override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                        Log.v(TAG, uploadInfo.uploadedBytes.toString())
                    }

                    override fun onError(context: Context, uploadInfo: UploadInfo, throwable: Throwable) {
                        Log.v(TAG, throwable.toString())
                    }

                    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
                        Log.v(TAG, uploadInfo.uploadedBytes.toString())
                    }

                    override fun onCompletedWhileNotObserving() {
                        Log.v(TAG, "onCompletedWhileNotObserving")
                    }
                })

                multipartUploadRequest.startUpload()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "DisplayImageActivity"
    }
}