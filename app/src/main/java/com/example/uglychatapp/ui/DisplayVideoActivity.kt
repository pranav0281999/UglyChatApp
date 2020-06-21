package com.example.uglychatapp.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.uglychatapp.MainApplication
import com.example.uglychatapp.R
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.util.*

class DisplayVideoActivity : AppCompatActivity() {

    var videoView: VideoView? = null
    var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_video)

        mediaController = MediaController(this)
        videoView = findViewById(R.id.display_video_vv)

        if (intent.hasExtra("videoPath")) {
            val videoPath = intent.getStringExtra("videoPath")

            videoView?.setVideoURI(Uri.parse(videoPath))
            videoView?.start()

            mediaController!!.setAnchorView(videoView)

            val uuid = UUID.randomUUID().toString()

            try {
                val multipartUploadRequest = MultipartUploadRequest(this, serverUrl = "http://" + MainApplication.serverNode + ":" + MainApplication.serverNodePort + "/chat/video")
                        .setMethod("POST")
                        .addHeader("filename", uuid)
                        .addFileToUpload(
                                filePath = videoPath,
                                parameterName = "video"
                        )

                multipartUploadRequest.subscribe(this, this, object : RequestObserverDelegate {
                    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                        Log.v(TAG, uploadInfo.uploadedBytes.toString())
                    }

                    override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                        Log.v(TAG, uploadInfo.uploadedBytes.toString())
                    }

                    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
                        Log.v(TAG, exception.toString())
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
        private const val TAG = "DisplayVideoActivity"
    }
}