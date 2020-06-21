package com.example.uglychatapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uglychatapp.MainApplication;
import com.example.uglychatapp.R;
import com.example.uglychatapp.adapters.AdapterChatMsgs;
import com.example.uglychatapp.models.ChatMessage;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    Button button;
    MainApplication globalVariable;
    EditText editTextMessage;
    Chat mychat = null;
    RecyclerView recyclerViewMsgs;
    List<ChatMessage> chatMessageList;
    AdapterChatMsgs adapterChatMsgs;
    String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        if (intent.hasExtra("receiver")) {
            receiver = intent.getStringExtra("receiver");
        }

        globalVariable = (MainApplication) getApplicationContext();
        chatMessageList = new ArrayList<>();

        button = findViewById(R.id.activity_main_btn_sendmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        editTextMessage = findViewById(R.id.activity_main_et_msg);
        recyclerViewMsgs = findViewById(R.id.activity_main_rv_msgs);

        adapterChatMsgs = new AdapterChatMsgs(chatMessageList);
        recyclerViewMsgs.setAdapter(adapterChatMsgs);
        recyclerViewMsgs.setLayoutManager(new LinearLayoutManager(this));

        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chat_activity_send_image) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, MainApplication.PICK_IMAGE);
            return true;
        } else if (item.getItemId() == R.id.menu_chat_activity_send_audio) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, MainApplication.PICK_AUDIO);
            return true;
        } else if (item.getItemId() == R.id.menu_chat_activity_send_video) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, MainApplication.PICK_VIDEO);
            return true;
        } else if (item.getItemId() == R.id.menu_chat_activity_send_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, MainApplication.PICK_FILE);
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == MainApplication.PICK_IMAGE && data != null) {
            Uri selectedImage = data.getData();

            if (selectedImage != null) {
                Intent intent = new Intent(ChatActivity.this, DisplayImageActivity.class);
                intent.putExtra("imagePath", selectedImage.toString());
                startActivityForResult(intent, MainApplication.UPLOAD_IMAGE);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == MainApplication.PICK_VIDEO && data != null) {
            Uri contentURI = data.getData();

            Intent intent = new Intent(ChatActivity.this, DisplayVideoActivity.class);
            intent.putExtra("videoPath", contentURI.toString());
            startActivity(intent);
        } else if (resultCode == Activity.RESULT_OK && requestCode == MainApplication.PICK_AUDIO && data != null) {
            Uri contentURI = data.getData();

            Intent intent = new Intent(ChatActivity.this, DisplayAudioActivity.class);
            intent.putExtra("audioPath", getPath(contentURI));
            startActivity(intent);
        } else if (resultCode == Activity.RESULT_OK && requestCode == MainApplication.PICK_FILE && data != null) {
            Uri contentURI = data.getData();
            String filePath = getFilePath(contentURI);

            Log.v(TAG, filePath);
        } else if (resultCode == Activity.RESULT_OK && requestCode == MainApplication.UPLOAD_IMAGE && data != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setReceiverUsername(receiver);
            chatMessage.setBody(data.getStringExtra("imageUuid"));
            sendImageMessage(chatMessage);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String getFilePath(Uri uri) {

        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.connection.removeConnectionListener(connectionListener);
    }

    public void sendMessage() {
        String messageString = editTextMessage.getText().toString();

        if (!messageString.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setReceiverUsername(receiver);
            chatMessage.setBody(messageString);
            sendMessage(chatMessage);
        }

        editTextMessage.setText("");
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

            MainApplication.connected = true;
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.v(TAG, "authenticated");

            MainApplication.connected = true;
            MainApplication.chat_created = false;
            MainApplication.loggedin = true;
        }

        @Override
        public void connectionClosed() {
            Log.v(TAG, "connectionClosed");

            MainApplication.connected = false;
            MainApplication.chat_created = false;
            MainApplication.loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.v(TAG, "connectionClosedOnError");

            MainApplication.connected = false;
            MainApplication.chat_created = false;
            MainApplication.loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            Log.v(TAG, "reconnectionSuccessful");

            MainApplication.connected = true;
            MainApplication.chat_created = false;
            MainApplication.loggedin = false;
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.v(TAG, "reconnectingIn");

            MainApplication.loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.v(TAG, "reconnectionFailed");

            MainApplication.connected = false;
            MainApplication.chat_created = false;
            MainApplication.loggedin = false;
        }
    };

    public void sendMessage(ChatMessage chatMessage) {
        boolean msgSent = true;
        if (!MainApplication.chat_created || mychat == null) {
            mychat = ChatManager.getInstanceFor(globalVariable.connection).createChat(
                chatMessage.getReceiverUsername() + "@" + MainApplication.serverXmppHostname,
                new MMessageListener(getApplicationContext()));
            MainApplication.chat_created = true;
        }

        final Message message = new Message();
        message.setBody(chatMessage.getBody());
        message.addBody("receiver", chatMessage.getReceiverUsername());
        message.setType(Message.Type.chat);

        try {
            mychat.sendMessage(message);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
            msgSent = false;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Message not sent", Toast.LENGTH_LONG).show();
                }
            });
        } finally {
            if (msgSent) {
                chatMessageList.add(chatMessage);
                adapterChatMsgs.notifyItemInserted(chatMessageList.size() - 1);
            }
        }
    }

    public void sendImageMessage(ChatMessage chatMessage) {
        boolean msgSent = true;

        if (!MainApplication.chat_created || mychat == null) {
            mychat = ChatManager.getInstanceFor(globalVariable.connection)
                .createChat(chatMessage.getReceiverUsername() + "@" + MainApplication.serverXmppHostname, new MMessageListener(getApplicationContext()));
            MainApplication.chat_created = true;
        }

        String stanzaUuid = UUID.randomUUID().toString();

        final Message message = new Message();
        message.setBody(chatMessage.getBody());
        message.setType(Message.Type.chat);
        message.addBody("receiver", chatMessage.getReceiverUsername());
        message.setStanzaId(stanzaUuid + "@image");
        message.setFrom(MainApplication.currentUserName + "@" + MainApplication.serverXmppHostname);
        message.setTo(chatMessage.getReceiverUsername() + "@" + MainApplication.serverXmppHostname);

        try {
            mychat.sendMessage(message);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
            msgSent = false;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Message not sent", Toast.LENGTH_LONG).show();
                }
            });
        } finally {
            if (msgSent) {
                chatMessageList.add(chatMessage);
                adapterChatMsgs.notifyItemInserted(chatMessageList.size() - 1);
            }
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
                chatMessage.setReceiverUsername(message.getBody("receiver"));

                processMessage(chatMessage);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), chatMessage.getBody(), Toast.LENGTH_LONG).show();

                    chatMessageList.add(chatMessage);
                    adapterChatMsgs.notifyItemInserted(chatMessageList.size() - 1);
                }
            });
        }
    }
}
