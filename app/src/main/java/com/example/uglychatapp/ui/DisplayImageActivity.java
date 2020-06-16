package com.example.uglychatapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.R;

public class DisplayImageActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        imageView = findViewById(R.id.display_image_iv);

        Intent intent = getIntent();

        if (intent.hasExtra("imagePath")) {
            String imagePath = intent.getStringExtra("imagePath");

            imageView.setImageURI(Uri.parse(imagePath));
        }
    }
}
