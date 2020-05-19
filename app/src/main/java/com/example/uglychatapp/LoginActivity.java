package com.example.uglychatapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText et_username, et_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void signin() {
        Log.v(TAG, "signin");
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
}
