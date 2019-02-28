package com.dyc.factorymode;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.AudioManager;
import android.content.res.Resources;
import android.media.MediaPlayer.OnPreparedListener;
import java.util.Random;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.ImageView;
import android.view.KeyEvent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;
import android.view.WindowManager;

public class LoudSpeaker extends Activity implements View.OnClickListener {

    private static final String TAG = "LoudSpeaker_Sagereal";
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor afd;
    private Resources res;
    private int mDefaultVolume = -1;
    private int musicrandom;
    private Button msuccess;
    private Button retestbt;
    private Button mfail;
    private int isman = 0;
    private ImageView female_iv;
    private ImageView man_iv;
    private int testmode;
    private static HeadsetBroadcast headsetBroadcast;
    private IntentFilter intentFilter;
    private boolean testpass = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.loudspeaker_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        init();
        Random random = new Random();//随机播放音乐
        musicrandom = random.nextInt(2);
        android.util.Log.d(TAG, "musicrandom" + musicrandom);
        res = getResources();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.setParameters("SET_LOOPBACK_TYPE=0");
        headsetBroadcast = new HeadsetBroadcast();
        intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        if (musicrandom == 0) {
            isman = 1;
            afd = res.openRawResourceFd(R.raw.manvoice);
        }
        if (musicrandom == 1) {
            isman = 2;
            afd = res.openRawResourceFd(R.raw.womanvoice);
        }
    }

    @Override
    protected void onResume() {
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        registerReceiver(headsetBroadcast, intentFilter);
        if (!mAudioManager.isWiredHeadsetOn()) {
            Openspeaker();
            StopAlarmRing();
            PlayAlarmRing();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    class HeadsetBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    Toast.makeText(LoudSpeaker.this, R.string.remind_remove_headset, Toast.LENGTH_SHORT).show();
                    StopAlarmRing();
                } else {
                    android.util.Log.d(TAG, "HeadsetBroadcast action ==" + action);
                    Openspeaker();
                    retestbt.performClick();
                }
            }
        }
    }

    private void Openspeaker() {
        try {
            mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            android.util.Log.d(TAG, "mDefaultVolume openspeaker == " + mDefaultVolume);
            int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            if (!mAudioManager.isSpeakerphoneOn()) {
                //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(true);// 使用扬声器外放，即使已经插入耳机
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private void init() {
        msuccess = (Button) findViewById(R.id.loudspeaker_success_btn);
        msuccess.setEnabled(false);
        mfail = (Button) findViewById(R.id.loudspeaker_fail_btn);
        retestbt = (Button) findViewById(R.id.re_test);
        female_iv = (ImageView) findViewById(R.id.female_iv);
        man_iv = (ImageView) findViewById(R.id.man_iv);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        msuccess.setOnClickListener(this);
        mfail.setOnClickListener(this);
        female_iv.setOnClickListener(this);
        man_iv.setOnClickListener(this);
        retestbt.setOnClickListener(this);
    }

    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.man_iv:
                man_iv.setVisibility(View.INVISIBLE);
                female_iv.setVisibility(View.INVISIBLE);
                if (isman == 1) {
                    msuccess.setEnabled(true);
                    if(testpass){
                        testpass=false;
                        msuccess.performClick();
                   }
                }
                break;
            case R.id.female_iv:
                man_iv.setVisibility(View.INVISIBLE);
                female_iv.setVisibility(View.INVISIBLE);
                if (isman == 2) {
                    msuccess.setEnabled(true);
                    if(testpass){
                        testpass=false;
                        msuccess.performClick();
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
                PlayAlarmRing();
                break;
            case R.id.loudspeaker_success_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                StartUtil.StartNextActivity("Loudspeaker",1,testmode,LoudSpeaker.this,TestLCD.class);
                break;
            case R.id.loudspeaker_fail_btn:
                 if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                StartUtil.StartNextActivity("Loudspeaker",2,testmode,LoudSpeaker.this,TestLCD.class);
                break;
           }
        }
    }

    private void PlayAlarmRing() {
        try {
            if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // TODO Auto-generated method stub
                        android.util.Log.d(TAG, "ruanwenqiang: what=" + what + " extra=" + extra);
                        return false;
                    }
                });
                mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    private void StopAlarmRing() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            try {
                afd.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.setSpeakerphoneOn(false);

        if (mDefaultVolume != -1) {
            android.util.Log.d(TAG, " StopAlarmRing mDefaultVolume = " + mDefaultVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mDefaultVolume, 0);
        }

    }

    @Override
    protected void onPause() {
        StopAlarmRing();
        if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        StopAlarmRing();
        super.onDestroy();
    }
}
