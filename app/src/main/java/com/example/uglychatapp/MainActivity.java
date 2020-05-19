package com.example.uglychatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.models.ChatMessage;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    XMPPTCPConnection connection = null;
    Button button;
    String server = "192.168.0.101";
    String sender = "testuser1", receiver = "testuser2";
    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.activity_main_btn_sendmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setReceiver(receiver);
                chatMessage.setBody("hello from " + sender);
                chatMessage.setMine(false);

                sendMessage(chatMessage);
            }
        });

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                initialiseConnection();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initialiseConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(server);
        config.setHost(server);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());

        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
        connect("me");
    }

    public void connect(final String caller) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;

                isconnecting = true;

                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), caller + "=>connecting....", Toast.LENGTH_LONG).show();
                        }
                    });

                Log.v("Connect() Function", caller + "=>connecting....");

                try {
                    connection.connect();
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                        .getInstanceFor(connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid, final String toid, final String msgid, final Stanza packet) {
                        }
                    });
                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "(" + caller + ")" + "IOException: ", Toast.LENGTH_SHORT).show();
                                }
                            });

                    Log.v("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "(" + caller + ")" + "SMACKException: ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.v("(" + caller + ")", "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                            .post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "(" + caller + ")" + "XMPPException: ", Toast.LENGTH_SHORT).show();
                                }
                            });
                    Log.v("connect(" + caller + ")", "XMPPException: " + e.getMessage());
                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {
        try {
            connection.login(sender, sender);
            Log.v("LOGIN", "Yey! We're connected to the Xmpp server!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class XMPPConnectionListener implements ConnectionListener {

        @Override
        public void connected(final XMPPConnection connection) {
            Log.v("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "ConnectionCLosed!",
                            Toast.LENGTH_SHORT).show();

                    }
                });
            Log.v("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "ConnectionClosedOn Error!!",
                            Toast.LENGTH_SHORT).show();

                    }
                });
            Log.v("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.v("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "ReconnectionFailed!",
                            Toast.LENGTH_SHORT).show();

                    }
                });
            Log.v("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "REConnected!",
                            Toast.LENGTH_SHORT).show();

                    }
                });
            Log.v("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.v("xmpp", "Authenticated!");
            loggedin = true;
            ChatManager.getInstanceFor(connection).addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        if (createdLocally) {
                            Log.v(TAG, "createdLocally");
                        } else {
                            // Chat created by others
                            chat.addMessageListener(new MMessageListener(getApplicationContext()));
                        }

                    }
                });

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(getApplicationContext(), "Connected!",
                            Toast.LENGTH_SHORT).show();

                    }
                });
        }
    }

    public void sendMessage(ChatMessage chatMessage) {
        Chat mychat = null;

        if (!chat_created) {
            mychat = ChatManager.getInstanceFor(connection).createChat(
                chatMessage.getReceiver() + "@"
                    + "arugu-g41mt-s2",
                new MMessageListener(getApplicationContext()));
            chat_created = true;
        }

        final Message message = new Message();
        message.setBody(chatMessage.getBody());
        message.setType(Message.Type.normal);

        try {
            if (connection.isAuthenticated()) {
                mychat.sendMessage(message);
            } else {
                login();
            }
        } catch (SmackException.NotConnectedException e) {
            Log.v("xmpp.SendMessage()", "msg Not sent!-Not Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context contxt) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            Log.v("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                + message);

            System.out.println("Body-----" + message.getBody());

            if (message.getType() == Message.Type.chat
                && message.getBody() != null) {
                final ChatMessage chatMessage = new ChatMessage();
                chatMessage.setBody(message.getBody());

                processMessage(chatMessage);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {

//            chatMessage.isMine = false;
//            Chats.chatlist.add(chatMessage);
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                @Override
//                public void run() {
//                    Chats.chatAdapter.notifyDataSetChanged();
//                }
//            });

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), chatMessage.getBody(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
