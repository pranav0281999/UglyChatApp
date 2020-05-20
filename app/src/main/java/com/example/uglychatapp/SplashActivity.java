package com.example.uglychatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    MainApplication globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        globalVariable = (MainApplication) getApplicationContext();

        initialiseConnection();
    }

    public void initialiseConnection() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(MainApplication.server);
        config.setHost(MainApplication.server);
        config.setPort(MainApplication.serverPort);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        globalVariable.connection = new XMPPTCPConnection(config.build());

        globalVariable.connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                startLoginActivity();
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.v(TAG, "authenticated");
            }

            @Override
            public void connectionClosed() {
                Log.v(TAG, "connectionClosed");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.v(TAG, "connectionClosedOnError");
            }

            @Override
            public void reconnectionSuccessful() {
                Log.v(TAG, "reconnectionSuccessful");
            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.v(TAG, "reconnectingIn");
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.v(TAG, "reconnectionFailed");
            }
        });

        connect();
    }

    public void connect() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (globalVariable.connection.isConnected())
                    return false;

                MainApplication.isconnecting = true;

                try {
                    globalVariable.connection.connect();

                    DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(globalVariable.connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);

                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {
                        @Override
                        public void onReceiptReceived(final String fromid, final String toid, final String msgid, final Stanza packet) {
                            Log.v(TAG, "onReceiptReceived");
                        }
                    });

                    MainApplication.connected = true;
                } catch (Exception e) {
                    Log.v(TAG, e.toString());

                    showConnectionError();
                }

                return MainApplication.isconnecting = false;
            }
        };

        connectionThread.execute();
    }

    public void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void showConnectionError() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Couldn't connect to server", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
}
