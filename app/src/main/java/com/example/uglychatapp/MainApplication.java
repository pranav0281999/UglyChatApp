package com.example.uglychatapp;

import android.app.Application;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    public XMPPTCPConnection connection = null;
    public static String serverXmpp = "192.168.0.167";
    public static String serverNode = "192.168.0.167";
    public static int serverXmppPort = 5222;
    public static int serverNodePort = 5000;
    public static String currentUserName;
    public static String serverXmppHostname = "openfire-server";
    public static boolean connected = false;
    public static boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean chat_created = false;

    public static int UPLOAD_IMAGE = 101;
    public static int UPLOAD_VIDEO = 102;
    public static int UPLOAD_AUDIO = 103;
    public static int UPLOAD_FILE = 104;
}
