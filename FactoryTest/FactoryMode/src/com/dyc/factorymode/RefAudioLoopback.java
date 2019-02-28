package com.dyc.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.os.Handler;
import android.os.Message;
import android.media.MediaRecorder;
import java.util.Timer;
import android.content.SharedPreferences;
import java.util.TimerTask;
import android.widget.ImageView;

public class RefAudioLoopback extends Activity implements OnClickListener {

    private static final String TAG = "RefAudioLoopback_DYC";
	private AudioManager mAudioManager;
	final static String openMic = "SET_LOOPBACK_TYPE=3,2";
	final static String closeMic = "SET_LOOPBACK_TYPE=0";
	private final static int OPEN_MIC = 1;
	private final static int NEXT_BUTTON_ENABLE = 2;
	private Button nextButton;
    private Button mfail;
    private ImageView headsetimage;
    private float currentangle;
	private Resources res;
	private boolean flag;
	private static HeadsetBroadcast headsetBroadcast;
	private IntentFilter intentFilter;
	public boolean isReg = false;
    private int maxcount = 0;
    private TextView maxnotice;
    private int testmode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		setContentView(R.layout.refaudio_test);
		Root.getInstance().addActivity(this);
		SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
		res = getResources();
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		flag = false;
		nextButton = (Button) findViewById(R.id.refaudio_success_btn);
		headsetimage = (ImageView) findViewById(R.id.head_iv);
		mfail = (Button) findViewById(R.id.refaudio_fail_btn);
		nextButton.setOnClickListener(this);
		mfail.setOnClickListener(this);
		headsetBroadcast = new HeadsetBroadcast();
		intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		isReg = false;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!ButtonUtils.isFastDoubleClick(v.getId())) {
		 switch (v.getId()) {
            case R.id.refaudio_success_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                mAudioManager.setParameters(closeMic);
                StartUtil.StartNextActivity("Refaudio",1,testmode,RefAudioLoopback.this,HeadsetLoopback.class);
                break;
            case R.id.refaudio_fail_btn:
                if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
                }
                mAudioManager.setParameters(closeMic);
                StartUtil.StartNextActivity("Refaudio",1,testmode,RefAudioLoopback.this,HeadsetLoopback.class);
                break;
            }
        }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		nextButton.setEnabled(false);
		Timer timer = new Timer();
        	timer.schedule(new TimerTask() {
		      @Override
			public void run() {
				Message msg = Message.obtain(mHandler, NEXT_BUTTON_ENABLE);
				mHandler.sendMessage(msg);
			 }
		}, 1500);
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    registerReceiver(headsetBroadcast, intentFilter);
		new Thread(runnable).start();
		super.onResume();
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("flag = " + flag);
			synchronized(this){
			if (mAudioManager.isWiredHeadsetOn() && !flag) {
				flag = true;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = Message.obtain(mHandler, OPEN_MIC);
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
				case OPEN_MIC:
					mAudioManager.setParameters(openMic);
				default:
					return;
			}
		}
	};

	class HeadsetBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
            android.util.Log.d(TAG, "HeadsetBroadcast action = " + action);
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    headsetimage.setBackground(res.getDrawable(R.drawable.headsetimage1));
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                    headsetimage.setBackground(res.getDrawable(R.drawable.headsetimage2));
                }
                new Thread(runnable).start();
            }
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
		return true;
	    }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (headsetBroadcast != null) {
                    unregisterReceiver(headsetBroadcast);
                    headsetBroadcast = null;
        }
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		flag = false;
		mAudioManager.setParameters(closeMic);
	}
}
