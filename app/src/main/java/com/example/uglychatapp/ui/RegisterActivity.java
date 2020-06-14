package com.example.uglychatapp.ui;

import android.content.ContentValues;
import android.content.Intent;
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
import org.jivesoftware.smackx.iqregister.AccountManager;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    MainApplication globalVariable;
    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        globalVariable = (MainApplication) getApplicationContext();
        globalVariable.connection.addConnectionListener(connectionListener);

        etUsername = findViewById(R.id.activity_register_et_username);
        etPassword = findViewById(R.id.activity_register_et_password);
        btnLogin = findViewById(R.id.activity_register_btn_register);
        tvLogin = findViewById(R.id.activity_register_tv_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String username, password;
                    username = etUsername.getText().toString();
                    password = etPassword.getText().toString();

                    register(username, password);
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
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
        Intent intent = new Intent(RegisterActivity.this, UsersActivity.class);
        startActivity(intent);
        finish();
    }

    public void openLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showLoginError() {
        Toast.makeText(RegisterActivity.this, "Couldn't register", Toast.LENGTH_LONG).show();
    }

    private void register(String username, String password) {
        try {
            AccountManager accountManager = AccountManager.getInstance(globalVariable.connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(username, password);

            MainApplication.currentUserName = username;
            saveToDB(username, password);

            globalVariable.connection.login(username, password);
        } catch (Exception e) {
            Log.e(TAG, e.toString());

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
