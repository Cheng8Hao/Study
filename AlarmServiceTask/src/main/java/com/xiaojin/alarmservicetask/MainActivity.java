package com.xiaojin.alarmservicetask;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xiaojin.alarmservicetask.service.LongRunningService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myMainActivity";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent intent = new Intent(this, LongRunningService.class);
        startService(intent);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, LongRunningService.class));
        } else {
            startService(new Intent(MainActivity.this, LongRunningService.class));
        }
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "onCreate: currentVolume "+currentVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);


    }
}
