package com.example.uglychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    MainApplication globalVariable;
    EditText et_username, et_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        globalVariable = (MainApplication) getApplicationContext();

        et_username = findViewById(R.id.activity_login_et_username);
        et_password = findViewById(R.id.activity_login_et_password);
        btn_login = findViewById(R.id.activity_login_btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    signin();
                }
            }
        });
    }

    private boolean validate() {
        String username, password;
        boolean result = true;

        username = et_username.getText().toString();
        password = et_password.getText().toString();

        if (password.trim().isEmpty()) {
            et_password.setError("Invalid");
            result = false;
        }

        if (username.trim().isEmpty()) {
            et_username.setError("Invalid");
            result = false;
        }

        return result;
    }

    public void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void showLoginError() {
        Toast.makeText(LoginActivity.this, "Couldn't authenticate", Toast.LENGTH_LONG).show();
    }

    private void signin() {
        globalVariable.connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.v(TAG, "connected");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                openMainActivity();
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

        String username, password;
        username = et_username.getText().toString();
        password = et_password.getText().toString();

        try {
            globalVariable.connection.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();

            showLoginError();
        }
    }
}
