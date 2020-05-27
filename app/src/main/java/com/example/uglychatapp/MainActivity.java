package com.example.uglychatapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.database.LoginSQLiteDBHelper;
import com.example.uglychatapp.models.ChatMessage;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button button;
    MainApplication globalVariable;
    EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalVariable = (MainApplication) getApplicationContext();

        button = findViewById(R.id.activity_main_btn_sendmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        editTextMessage = findViewById(R.id.activity_main_et_msg);

        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.connection.removeConnectionListener(connectionListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_activity_logout) {
            globalVariable.connection.disconnect();

            SQLiteDatabase database = new LoginSQLiteDBHelper(this).getReadableDatabase();
            database.execSQL("delete from " + LoginSQLiteDBHelper.PERSON_TABLE_NAME);
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void sendMessage() {
        String messageString = editTextMessage.getText().toString();

        if (!messageString.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setReceiver(MainApplication.receiver);
            chatMessage.setBody(messageString);
            chatMessage.setMine(false);
            sendMessage(chatMessage);
        }
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

        globalVariable.connection.addConnectionListener(connectionListener);
    }

    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.v(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.v(TAG, "authenticated");
        }

        @Override
        public void connectionClosed() {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
    };

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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), chatMessage.getBody(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
