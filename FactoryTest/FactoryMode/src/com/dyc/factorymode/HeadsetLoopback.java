package com.dyc.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Color;
import android.content.SharedPreferences;
import com.dyc.factorymode.fmradio.FmMainActivity;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaRecorder;
import android.widget.TextView;
import android.os.SystemClock;
import android.os.PowerManager;

public class HeadsetLoopback extends Activity implements OnClickListener, Recorder.OnStateChangedListener {

    private static final String TAG = "HeadsetLoopback_Sagereal";
    private AudioManager mAudioManager;
    private IntentFilter intentFilter;
    final static String openHeadset = "SET_LOOPBACK_TYPE=2,2";
    final static String closeHeadset = "SET_LOOPBACK_TYPE=0,0";
    private final static int OPEN_HEADSET = 1;
    private final static int NEXT_BUTTON_ENABLE = 2;
    private ImageView headsetimage;
    private Button nextButton;
    private Button mfail;
    private static HeadsetBroadcast headsetBroadcast;
    private Resources res;
    public boolean flag = false ,testpass = true;
    private int testmode;
    int mDefaultRingerMode;
    VUMeter mVUMeter;
    Recorder mRecorder;
    private int maxcount = 0;
    private float currentangle;
    private TextView maxnotice;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.headset_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        res = getResources();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mDefaultRingerMode = mAudioManager.getRingerMode();
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        mVUMeter = (VUMeter) findViewById(R.id.uvMeter);//麦克风幅度
        maxnotice = (TextView) findViewById(R.id.max_notice);
        mRecorder = new Recorder();
        mRecorder.setOnStateChangedListener(this);
        mVUMeter.setRecorder(mRecorder);

        mVUMeter.setCallBack(new VUMeter.CallBack() {
            @Override
            public void getData(float value) {
                currentangle = value;
                android.util.Log.d(TAG, "value===" + value);
                if (currentangle > 2.4) {
                    maxcount++;
                }
                if (maxcount == 2) {
                    maxnotice.setText(R.string.max_pass);
                    maxnotice.setTextColor(Color.GREEN);
                    nextButton.setEnabled(true);
                    if(testpass){
                        testpass=false;
                        nextButton.performClick();
                   }
                }
            }
        });
        headsetimage = (ImageView) findViewById(R.id.head_iv);
        nextButton = (Button) findViewById(R.id.headset_success_btn);
        mfail = (Button) findViewById(R.id.headset_fail_btn);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        mfail.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        flag = false;
        headsetBroadcast = new HeadsetBroadcast();
        intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    }

    class HeadsetBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            android.util.Log.d(TAG, "HeadsetBroadcast action = " + action);
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                new Thread(runnable).start();
                if (mAudioManager.isWiredHeadsetOn()) {
                    headsetimage.setBackground(res.getDrawable(R.drawable.headsetimage1));
                    if (maxcount < 2) {
                        maxnotice.setText(R.string.max_fail);
                        maxnotice.setTextColor(Color.RED);
                    } else {
                        nextButton.setEnabled(true);
                    }
                    mAudioManager.setParameters(closeHeadset);
                    mAudioManager.setParameters(openHeadset);
                    mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr", HeadsetLoopback.this);
                } else {
                    mAudioManager.setParameters(closeHeadset);
                    nextButton.setEnabled(false);
                    headsetimage.setBackground(res.getDrawable(R.drawable.headsetimage2));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        nextButton.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain(mHandler, NEXT_BUTTON_ENABLE);
                mHandler.sendMessage(msg);
            }
        }, 1500);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        registerReceiver(headsetBroadcast, intentFilter);
        new Thread(runnable).start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.headset_success_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                SystemClock.sleep(400);
                if (flag) {
                    flag = false;
                    mAudioManager.setParameters(closeHeadset);
                }
                mRecorder.delete();
                StartUtil.StartNextActivity("Headloop",1,testmode,HeadsetLoopback.this,FmMainActivity.class);
                break;
            case R.id.headset_fail_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                if (flag) {
                    flag = false;
                    mAudioManager.setParameters(closeHeadset);
                }
                SystemClock.sleep(400);
                mRecorder.delete();
                StartUtil.StartNextActivity("Headloop",2,testmode,HeadsetLoopback.this,FmMainActivity.class);
                break;
            }
       }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    flag = true;
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    android.util.Log.d(TAG, "openHeadset = " + openHeadset);
                    Message msg = Message.obtain(mHandler, OPEN_HEADSET);
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEXT_BUTTON_ENABLE:
                    //nextButton.setEnabled(true);
                    break;
                case OPEN_HEADSET:
                    mAudioManager.setParameters(openHeadset);
                    break;
                default:
                    return;
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (headsetBroadcast != null) {
            unregisterReceiver(headsetBroadcast);
            headsetBroadcast = null;
         }
        flag = false;
        // mAudioManager.setParameters(closeHeadset);
        mAudioManager.setRingerMode(mDefaultRingerMode);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mAudioManager.setRingerMode(mDefaultRingerMode);
    }
}
