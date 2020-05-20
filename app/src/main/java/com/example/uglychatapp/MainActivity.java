package com.example.uglychatapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.models.ChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button button;
    MainApplication globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalVariable = (MainApplication) getApplicationContext();

        button = findViewById(R.id.activity_main_btn_sendmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setReceiver(MainApplication.receiver);
                chatMessage.setBody("hello from " + MainApplication.sender);
                chatMessage.setMine(false);
                sendMessage(chatMessage);
            }
        });

        setup();
    }

    public void setup() {
        if (globalVariable.connection.isAuthenticated()) {
            ChatManager.getInstanceFor(globalVariable.connection).addChatListener(
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
        }
    }

//    public class XMPPConnectionListener implements ConnectionListener {
//
//        @Override
//        public void connected(final XMPPConnection connection) {
//            MainApplication.connected = true;
//        }
//
//        @Override
//        public void connectionClosed() {
//            if (MainApplication.isToasted) {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "ConnectionCLosed!",
//                            Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            MainApplication.connected = false;
//            MainApplication.chat_created = false;
//            MainApplication.loggedin = false;
//        }
//
//        @Override
//        public void connectionClosedOnError(Exception arg0) {
//            if (MainApplication.isToasted) {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "ConnectionClosedOn Error!!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            MainApplication.connected = false;
//            MainApplication.chat_created = false;
//            MainApplication.loggedin = false;
//        }
//
//        @Override
//        public void reconnectingIn(int arg0) {
//            MainApplication.loggedin = false;
//        }
//
//        @Override
//        public void reconnectionFailed(Exception arg0) {
//            if (MainApplication.isToasted) {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "ReconnectionFailed!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            MainApplication.connected = false;
//            MainApplication.chat_created = false;
//            MainApplication.loggedin = false;
//        }
//
//        @Override
//        public void reconnectionSuccessful() {
//            if (MainApplication.isToasted) {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "REConnected!",
//                            Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            MainApplication.connected = true;
//            MainApplication.chat_created = false;
//            MainApplication.loggedin = false;
//        }
//
//        @Override
//        public void authenticated(XMPPConnection arg0, boolean arg1) {
//            MainApplication.loggedin = true;
//            ChatManager.getInstanceFor(globalVariable.connection).addChatListener(
//                new ChatManagerListener() {
//                    @Override
//                    public void chatCreated(Chat chat, boolean createdLocally) {
//                        if (createdLocally) {
//                            Log.v(TAG, "createdLocally");
//                        } else {
//                            // Chat created by others
//                            chat.addMessageListener(new MMessageListener(getApplicationContext()));
//                        }
//                    }
//                });
//
//            MainApplication.chat_created = false;
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        Log.v(TAG, e.toString());
//                    }
//
//                }
//            }).start();
//
//            if (MainApplication.isToasted)
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        }
//    }

    public void sendMessage(ChatMessage chatMessage) {
        Chat mychat = null;

        if (!MainApplication.chat_created) {
            mychat = ChatManager.getInstanceFor(globalVariable.connection).createChat(
                chatMessage.getReceiver() + "@" + MainApplication.openfireHostname,
                new MMessageListener(getApplicationContext()));
            MainApplication.chat_created = true;
        }

        final Message message = new Message();
        message.setBody(chatMessage.getBody());
        message.setType(Message.Type.normal);

        try {
            mychat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            Log.v(TAG, e.toString());
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }

    private class MMessageListener implements ChatMessageListener {
        Context context;

        public MMessageListener(Context context) {
            this.context = context;
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat, final Message message) {
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
