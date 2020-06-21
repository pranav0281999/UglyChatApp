package com.example.uglychatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import net.gotev.uploadservice.UploadServiceConfig
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class MainApplication : Application() {
    @JvmField
    var connection: XMPPTCPConnection? = null

    companion object {
        private const val TAG = "MainApplication"

        @JvmField
        var serverXmpp = "192.168.0.167"

        @JvmField
        var serverNode = "192.168.0.167"

        @JvmField
        var serverXmppPort = 5222

        @JvmField
        var serverNodePort = 5000

        @JvmField
        var currentUserName: String? = null

        @JvmField
        var serverXmppHostname = "openfire-server"

        @JvmField
        var connected = false

        @JvmField
        var loggedin = false

        @JvmField
        var isconnecting = false

        @JvmField
        var chat_created = false

        @JvmField
        var UPLOAD_IMAGE = 101

        @JvmField
        var UPLOAD_VIDEO = 102

        @JvmField
        var UPLOAD_AUDIO = 103

        @JvmField
        var UPLOAD_FILE = 104

        const val notificationChannelID = "TestChannel"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                    notificationChannelID,
                    "TestApp Channel",
                    NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        UploadServiceConfig.initialize(
                context = this,
                defaultNotificationChannel = notificationChannelID,
                debug = BuildConfig.DEBUG
        )
    }
}