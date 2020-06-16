package com.example.uglychatapp.ui;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;

import com.example.uglychatapp.R;

public class DisplayAudioActivity extends Activity implements MediaController.MediaPlayerControl {
    private static final String TAG = "DisplayAudioActivity";

    MediaController media_Controller;
    ImageView musicPlayerImage;
    MediaPlayer mediaPlayer;
    String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_audio);

        Bundle extras;
        extras = getIntent().getExtras();

        if (extras != null) {
            filepath = extras.getString("audioPath");
        }

        mediaPlayer = new MediaPlayer();
        media_Controller = new MediaController(this);
        media_Controller.setMediaPlayer(DisplayAudioActivity.this);
        musicPlayerImage = findViewById(R.id.activity_display_audio_iv);
        media_Controller.setAnchorView(musicPlayerImage);

        // https://medium.com/@sriramaripirala/android-10-open-failed-eacces-permission-denied-da8b630a89df
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return ((mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration());


    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        media_Controller.show();
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
