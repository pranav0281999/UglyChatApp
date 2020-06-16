package com.example.uglychatapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uglychatapp.R;

public class DisplayVideoActivity extends AppCompatActivity {
    VideoView videoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_video);

        mediaController = new MediaController(this);

        videoView = findViewById(R.id.display_video_vv);

        Intent intent = getIntent();
        if (intent.hasExtra("videoPath")) {
            String videoPath = intent.getStringExtra("videoPath");

            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.start();
            mediaController.setAnchorView(videoView);
        }
    }
}
