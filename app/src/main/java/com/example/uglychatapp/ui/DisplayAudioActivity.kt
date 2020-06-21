package com.example.uglychatapp.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import androidx.appcompat.app.AppCompatActivity
import com.example.uglychatapp.MainApplication
import com.example.uglychatapp.R
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.util.*

class DisplayAudioActivity : AppCompatActivity(), MediaPlayerControl {

    var media_Controller: MediaController? = null
    var musicPlayerImage: ImageView? = null
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_audio)

        if (intent.hasExtra("audioPath")) {
            val audioPath = intent.getStringExtra("audioPath")

            mediaPlayer = MediaPlayer()
            media_Controller = MediaController(this)
            media_Controller!!.setMediaPlayer(this)
            musicPlayerImage = findViewById(R.id.activity_display_audio_iv)
            media_Controller!!.setAnchorView(musicPlayerImage)

            // https://medium.com/@sriramaripirala/android-10-open-failed-eacces-permission-denied-da8b630a89df
            try {
                mediaPlayer!!.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                mediaPlayer!!.setDataSource(audioPath)
                mediaPlayer!!.prepare()
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
            }

            val uuid = UUID.randomUUID().toString()

            try {
                val multipartUploadRequest = MultipartUploadRequest(this, serverUrl = "http://" + MainApplication.serverNode + ":" + MainApplication.serverNodePort + "/chat/audio")
                        .setMethod("POST")
                        .addHeader("filename", uuid)
                        .addFileToUpload(
                                filePath = audioPath,
                                parameterName = "audio"
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return false
    }

    override fun canSeekForward(): Boolean {
        return false
    }

    override fun getBufferPercentage(): Int {
        return mediaPlayer!!.currentPosition * 100 / mediaPlayer!!.duration
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    override fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    override fun pause() {
        if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
    }

    override fun seekTo(pos: Int) {
        mediaPlayer!!.seekTo(pos)
    }

    override fun start() {
        mediaPlayer!!.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        media_Controller!!.show()
        return false
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    companion object {
        private const val TAG = "DisplayAudioActivity"
    }
}