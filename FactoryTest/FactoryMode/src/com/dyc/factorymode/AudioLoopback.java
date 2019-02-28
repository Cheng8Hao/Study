package com.dyc.factorymode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;

public class AudioLoopback extends Activity implements OnClickListener, Recorder.OnStateChangedListener {

    private static final String TAG = "AudioLoopback_Sagereal";
    private AudioManager mAudioManager;
    private int mDefaultVolume = -1;
    final static String openMic = "SET_LOOPBACK_TYPE=1,2";
    final static String closeMic = "SET_LOOPBACK_TYPE=0";
    private Button nextButton;
    private Button mfail;
    private Button mPlayButton;
    private Button test_againButton;
    private Resources res;
    private float currentangle;
    private int maxcount = 0;
    private int testmode;
    VUMeter mVUMeter;
    Recorder mRecorder;
    private TextView maxnotice;
    private static HeadsetBroadcast headsetBroadcast;
    private IntentFilter intentFilter;
    public boolean isReg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.audio_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        res = getResources();
        isReg = false;
        initview();
        headsetBroadcast = new HeadsetBroadcast();
        intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mVUMeter = (VUMeter) findViewById(R.id.uvMeter);//麦克风幅度
        mRecorder = new Recorder();
        mRecorder.setOnStateChangedListener(this);
        mVUMeter.setRecorder(mRecorder);
        mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr", AudioLoopback.this);
        mVUMeter.setCallBack(new VUMeter.CallBack() {
            @Override
            public void getData(float value) {
                currentangle = value;
                android.util.Log.d(TAG, "value===" + value);
                if (currentangle > 1.7) {
                    maxcount++;
                }
                if (maxcount == 2) {
                    maxnotice.setText(R.string.max_pass);
                    maxnotice.setTextColor(Color.GREEN);
                    mPlayButton.setEnabled(true);
                    test_againButton.setEnabled(true);
                }
            }
        });
    }
    
    private void initview(){
        nextButton = (Button) findViewById(R.id.audio_success_btn);
        mfail = (Button) findViewById(R.id.audio_fail_btn);
        maxnotice = (TextView) findViewById(R.id.max_notice);
        mPlayButton = (Button) findViewById(R.id.playButton);
        test_againButton = (Button) findViewById(R.id.test_againButton);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        test_againButton.setOnClickListener(this);
    }

    public void testAgain(){
        if(mRecorder!=null){
            mRecorder.delete();
	        AudioLoopback.this.recreate();
	        overridePendingTransition(0, 0);
        }
    }
    class HeadsetBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    Toast.makeText(AudioLoopback.this, R.string.remind_remove_headset, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateUi() {
        mVUMeter.invalidate();
    }

    @Override
    public void onStateChanged(int state) {
        updateUi();
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.audio_success_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                mRecorder.delete();
                if (FeatureOption.MTK_DUAL_MIC_SUPPORT) {
                      StartUtil.StartNextActivity("Audioloop",1,testmode,AudioLoopback.this,RefAudioLoopback.class);//这个需要注意＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃
                   } else {
                      StartUtil.StartNextActivity("Audioloop",1,testmode,AudioLoopback.this,HeadsetLoopback.class);
                   }
                break;
            case R.id.playButton:
                mRecorder.startPlayback();
                nextButton.setEnabled(true);
                break;
            case R.id.test_againButton:
                testAgain();
                break;
            case R.id.audio_fail_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                mRecorder.delete();
                if (FeatureOption.MTK_DUAL_MIC_SUPPORT) {
                      StartUtil.StartNextActivity("Audioloop",2,testmode,AudioLoopback.this,RefAudioLoopback.class);//这个需要注意＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃
                   } else {
                      StartUtil.StartNextActivity("Audioloop",2,testmode,AudioLoopback.this,HeadsetLoopback.class);
                   }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        registerReceiver(headsetBroadcast, intentFilter);
        try {
            mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (headsetBroadcast != null) {
            unregisterReceiver(headsetBroadcast);
            headsetBroadcast = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
