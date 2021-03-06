package com.example.uglychatapp.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.MainApplication;
import com.example.uglychatapp.R;
import com.example.uglychatapp.database.LoginSQLiteDBHelper;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    MainApplication globalVariable;
    EditText etUsername, etPassword;
    TextView tvRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        globalVariable = (MainApplication) getApplicationContext();
        globalVariable.connection.addConnectionListener(connectionListener);

        etUsername = findViewById(R.id.activity_login_et_username);
        etPassword = findViewById(R.id.activity_login_et_password);
        btnLogin = findViewById(R.id.activity_login_btn_login);
        tvRegister = findViewById(R.id.activity_login_tv_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String username, password;
                    username = etUsername.getText().toString();
                    password = etPassword.getText().toString();

                    signin(username, password);
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        readFromDB();
    }

    private boolean validate() {
        String username, password;
        boolean result = true;

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (password.trim().isEmpty()) {
            etPassword.setError("Invalid");
            result = false;
        }

        if (username.trim().isEmpty()) {
            etUsername.setError("Invalid");
            result = false;
        }

        return result;
    }

    public void openUserActivity() {
        Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
        startActivity(intent);
        finish();
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void showLoginError() {
        Toast.makeText(LoginActivity.this, "Couldn't authenticate", Toast.LENGTH_LONG).show();
    }

    private void signin(String username, String password) {
        try {
            globalVariable.connection.login(username, password);

            MainApplication.currentUserName = username;
            saveToDB(username, password);
        } catch (Exception e) {
            e.printStackTrace();

            showLoginError();
        }
    }

    private void saveToDB(String username, String password) {
        SQLiteDatabase database = new LoginSQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LoginSQLiteDBHelper.PERSON_COLUMN_NAME, username);
        values.put(LoginSQLiteDBHelper.PERSON_COLUMN_PASSWORD, password);
        long newRowId = database.insert(LoginSQLiteDBHelper.PERSON_TABLE_NAME, null, values);
    }

    private void readFromDB() {
        String username = null, password = null;

        SQLiteDatabase database = new LoginSQLiteDBHelper(this).getReadableDatabase();

        String[] projection = {
            LoginSQLiteDBHelper.PERSON_COLUMN_ID,
            LoginSQLiteDBHelper.PERSON_COLUMN_NAME,
            LoginSQLiteDBHelper.PERSON_COLUMN_PASSWORD
        };

        Cursor cursor = database.query(
            LoginSQLiteDBHelper.PERSON_TABLE_NAME,   // The table to query
            projection,                               // The columns to return
            null,                                // The columns for the WHERE clause
            null,                            // The values for the WHERE clause
            null,                                     // don't group the rows
            null,                                     // don't filter by row groups
            null                                      // don't sort
        );

        if (cursor.moveToLast()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(LoginSQLiteDBHelper.PERSON_COLUMN_NAME));
            password = cursor.getString(cursor.getColumnIndexOrThrow(LoginSQLiteDBHelper.PERSON_COLUMN_PASSWORD));
        }

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            signin(username, password);
        }

        cursor.close();
    }


    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.v(TAG, "connected");

            MainApplication.connected = true;
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            openUserActivity();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.connection.removeConnectionListener(connectionListener);
    }
}
