package com.example.uglychatapp.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uglychatapp.MainApplication;
import com.example.uglychatapp.R;
import com.example.uglychatapp.adapters.AdapterUsers;
import com.example.uglychatapp.database.LoginSQLiteDBHelper;
import com.example.uglychatapp.network.UserRestCalls;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {
    private static final String TAG = "UsersActivity";

    private ProgressBar progressBar;
    private RecyclerView recyclerViewUsers;
    private MainApplication globalVariable;
    private ArrayList<String> listUsers;
    private AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        globalVariable = (MainApplication) getApplicationContext();
        listUsers = new ArrayList<>();

        progressBar = findViewById(R.id.activity_users_pb);
        recyclerViewUsers = findViewById(R.id.activity_users_rv_users);

        adapterUsers = new AdapterUsers(globalVariable, listUsers);
        recyclerViewUsers.setAdapter(adapterUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        setup();
        getUsers();
    }

    public void setup() {
        globalVariable.connection.addConnectionListener(connectionListener);
    }

    public void getUsers() {
        UserRestCalls.GetAllUsers getAllUsers = new UserRestCalls.GetAllUsers(new UserRestCalls.GetAllUsersCallback() {
            @Override
            public void onTaskComplete(boolean success, JSONArray result) {
                progressBar.setVisibility(View.GONE);

                if (success) {
                    listUsers.clear();

                    for (int i = 0; i < result.length(); i++) {
                        try {
                            listUsers.add(result.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    listUsers.remove(MainApplication.currentUserName);

                    adapterUsers.notifyDataSetChanged();
                } else {
                    Toast.makeText(UsersActivity.this, R.string.couldnt_fetch_users, Toast.LENGTH_LONG).show();
                }
            }
        });
        getAllUsers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.connection.removeConnectionListener(connectionListener);
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
            Intent intent = new Intent(UsersActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

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
}
