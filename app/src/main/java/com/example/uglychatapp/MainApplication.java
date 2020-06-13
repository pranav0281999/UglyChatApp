package com.example.uglychatapp;

import android.app.Application;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    public XMPPTCPConnection connection = null;
    public static String server = "192.168.0.167";
    public static int serverPort = 5222;
    public static String sender = "testuser1", receiver = "testuser2";
    public static String openfireHostname = "openfire-server";
    public static boolean connected = false;
    public static boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean chat_created = false;
}
