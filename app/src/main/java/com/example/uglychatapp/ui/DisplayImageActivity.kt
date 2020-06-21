package com.example.uglychatapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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
    var btnSend: Button? = null
    var imagePath: String? = null
    val imageUuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        imageView = findViewById(R.id.display_image_iv)
        btnSend = findViewById(R.id.display_image_btn_send)

        if (intent.hasExtra("imagePath")) {
            imagePath = intent.getStringExtra("imagePath")

            imageView?.setImageURI(Uri.parse(imagePath))
        }

        btnSend?.setOnClickListener {
            if (imagePath != null) {
                sendImage()
            } else {
                Toast.makeText(this, "Couldn't send image", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendImage() {
        try {
            val multipartUploadRequest = MultipartUploadRequest(this, serverUrl = "http://" + MainApplication.serverNode + ":" + MainApplication.serverNodePort + "/chat/image")
                    .setMethod("POST")
                    .addHeader("filename", imageUuid)
                    .addFileToUpload(
                            filePath = imagePath!!,
                            parameterName = "image"
                    )

            multipartUploadRequest.subscribe(this, this, object : RequestObserverDelegate {
                override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                    Log.v(TAG, uploadInfo.uploadedBytes.toString())
                }

                override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                    val returnIntent = Intent()
                    returnIntent.putExtra("imageUuid", imageUuid)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }

                override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
                    Toast.makeText(context, "Couldn't send image", Toast.LENGTH_LONG).show()
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

    companion object {
        private const val TAG = "DisplayImageActivity"
    }
}