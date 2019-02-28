package com.dyc.factorymode;

import java.io.FileDescriptor;
import java.io.IOException;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
import java.util.Random;
import android.widget.ImageView;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;

public class RecTest extends Activity implements OnClickListener {

    private static final String TAG = "RecTest_Sagereal";
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Button nextButton;
    private Button mfail;
    private Button retestbt;
    private int defaultVolume = 0;
    private int musicrandom;
    private int testmode;
    private boolean isDefalutSpeakerOn = false , testpass = true;
    private int volume = 0;
    private ImageView female_iv;
    private ImageView man_iv;
    private int isman = 0;
    private String mFilePath = "";
    private AssetFileDescriptor afd;
    //private static HeadsetBroadcast headsetBroadcast;
    private IntentFilter intentFilter;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.rec_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        res = getResources();
        Random random = new Random();//随机播放音乐
        musicrandom = random.nextInt(2);
        if (musicrandom == 0) {
            isman = 1;
            afd = res.openRawResourceFd(R.raw.manvoice);
        }
        if (musicrandom == 1) {
            isman = 2;
            afd = res.openRawResourceFd(R.raw.womanvoice);
        }
        android.util.Log.d(TAG, "musicrandom = " + musicrandom);
        //headsetBroadcast = new HeadsetBroadcast();
        intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        isDefalutSpeakerOn = mAudioManager.isSpeakerphoneOn();
        defaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        nextButton = (Button) findViewById(R.id.rec_success_btn);
        retestbt = (Button) findViewById(R.id.re_test);
        nextButton.setEnabled(false);
        mfail = (Button) findViewById(R.id.rec_fail_btn);
        female_iv = (ImageView) findViewById(R.id.female_iv);
        man_iv = (ImageView) findViewById(R.id.man_iv);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        man_iv.setOnClickListener(this);
        female_iv.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
        retestbt.setOnClickListener(this);
    }

    /*class HeadsetBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    Toast.makeText(RecTest.this, R.string.remind_remove_headset, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.man_iv:
                man_iv.setVisibility(View.INVISIBLE);
                female_iv.setVisibility(View.INVISIBLE);
                if (isman == 1) {
                    nextButton.setEnabled(true);
                    if(testpass){
                        testpass=false;
                        nextButton.performClick();
                   }
                }
                break;
            case R.id.female_iv:
                man_iv.setVisibility(View.INVISIBLE);
                female_iv.setVisibility(View.INVISIBLE);
                if (isman == 2) {
                    nextButton.setEnabled(true);
                    if(testpass){
                        testpass=false;
                        nextButton.performClick();
                   }
                }
                break;
            case R.id.re_test:
                man_iv.setVisibility(View.VISIBLE);
                female_iv.setVisibility(View.VISIBLE);
                StopAlarmRing();
                Random random = new Random();//随机播放音乐
                int musicrandom1 = random.nextInt(2);
                if (musicrandom1 == 0) {
                    isman = 1;
                    afd = res.openRawResourceFd(R.raw.manvoice);
                }
                if (musicrandom1 == 1) {
                    isman = 2;
                    afd = res.openRawResourceFd(R.raw.womanvoice);
                }
                StartAlarmRing();
                break;
            case R.id.rec_success_btn:
                /*if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }*/
                StartUtil.StartNextActivity("Rec",1,testmode,RecTest.this,AutoTest.class);
                break;
            case R.id.rec_fail_btn:
                /*if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }*/
                StartUtil.StartNextActivity("Rec",2,testmode,RecTest.this,AutoTest.class);
                 break;
            default:
                break;
           }
        }
    }

    private void StartAlarmRing() {

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            android.util.Log.d(TAG, "RecTest StartAlarmRing0 ");
            if (mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) != 0) {
                android.util.Log.d(TAG, "RecTest StartAlarmRing1 ");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();

            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.util.Log.d(TAG, "RecTest StartAlarmRing2 ");
        mMediaPlayer.start();
    }

    private void StopAlarmRing() {
        android.util.Log.d(TAG, "RecTest StopAlarmRing0");
        if (mMediaPlayer != null) {
            android.util.Log.d(TAG, "RecTest StopAlarmRing1");
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        android.util.Log.d(TAG, "RecTest onstop StopAlarmRing1");
        StopAlarmRing();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                defaultVolume, 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        StopAlarmRing();
        /*if (headsetBroadcast != null) {
            unregisterReceiver(headsetBroadcast);
            headsetBroadcast = null;
        }*/
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAudioManager.setSpeakerphoneOn(isDefalutSpeakerOn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(headsetBroadcast, intentFilter);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, AudioManager.FLAG_VIBRATE);
        mAudioManager.setSpeakerphoneOn(false);// 不使用扬声器外放
        android.util.Log.d(TAG, "RecTest volume = " + volume);
        StopAlarmRing();
        StartAlarmRing();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
