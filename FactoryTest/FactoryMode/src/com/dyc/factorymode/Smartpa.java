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
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.graphics.Color;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
public class Smartpa extends Activity implements OnClickListener {

	private AudioManager mAudioManager;
	// SetAfeLoopback
	private TextView smartpaView;
	private Button smartpaButton;
	private Button nextButton;
	private Resources res;
	private boolean isnextbutton = false;
	private Intent mIntent;
	private Intent emIntent;
	private AssetFileDescriptor afd ;
	private MediaPlayer mMediaPlayer;
	private final static int SMART_PA_SUCCESS=25;
	private int mDefaultVolume = -1;
	private int clickflag = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		setContentView(R.layout.smartpa);
		Root.getInstance().addActivity(this);
		res = getResources();
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		smartpaButton = (Button) findViewById(R.id.smartpaButton);
		smartpaButton.setOnClickListener(this);
		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
		smartpaView = (TextView)findViewById(R.id.smartpaView);
		smartpaView.setText(R.string.app_name_smart_pa);
		smartpaView.setTextColor(res.getColor(R.color.normal));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	switch (v.getId()) {
		case R.id.nextButton:
			isnextbutton=true;
			StopAlarmRing();
			emIntent = new Intent("sagereal.intent.action.smartpa");
			Bundle emBundle = new Bundle();
			emBundle.putBoolean("isTestsmartpa", false);
			emIntent.putExtras(emBundle);
			sendBroadcast(emIntent);
			Intent intent = new Intent();
			intent.setClass(Smartpa.this, TestLCD.class);
			startActivity(intent);
			break;
		case R.id.smartpaButton:
		if(clickflag == 1){
			isnextbutton=false;
			smartpaView.setText(R.string.app_name_smart_pa);
			smartpaView.setTextColor(res.getColor(R.color.normal));
			afd = res.openRawResourceFd(R.raw.womanvoice);
			try {
			mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			if(!mAudioManager.isSpeakerphoneOn()){
				mAudioManager.setMode(AudioManager.MODE_NORMAL);
				mAudioManager.setSpeakerphoneOn(true);// 使用扬声器外放，即使已经插入耳机
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			PlayAlarmRing();
			new Thread(runnable).start();
			Log.e("shiyujiao","send begin");
			mIntent = new Intent("sagereal.intent.action.smartpa");
			Bundle mBundle = new Bundle();
			mBundle.putBoolean("isTestsmartpa", true);
			mIntent.putExtras(mBundle);
			sendBroadcast(mIntent);
			Log.e("shiyujiao","send end");
			clickflag=2;
		}
			break;

		default:
			break;
			
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		super.onResume();
		clickflag=1;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stubz
				try {
					Thread.sleep(5000);
					Message msg = Message.obtain(mHandler, SMART_PA_SUCCESS);
					mHandler.sendMessage(msg);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	};

	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SMART_PA_SUCCESS:
				Log.e("shiyujiao","ok");
				if(!isnextbutton){
					smartpaView.setText(res.getString(R.string.smart_pa_success));
					smartpaView.setTextColor(Color.BLUE);
					}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void PlayAlarmRing() {
		try {
			if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				mMediaPlayer.setOnErrorListener(new OnErrorListener() {
					
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						// TODO Auto-generated method stub
						System.out.println("shiyujiao: what=" + what + " extra="+extra);
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
		if(mMediaPlayer != null){
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
		
		if(mDefaultVolume != -1){
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mDefaultVolume, 0);
		}
		
	}
	private void stopBroadcast(){
	mIntent = new Intent("sagereal.intent.action.smartpa");
			Bundle mBundle = new Bundle();	
			mBundle.putBoolean("isTestsmartpa", false);
			mIntent.putExtras(mBundle);
			sendBroadcast(mIntent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			StopAlarmRing();
			stopBroadcast();
			Intent intent = new Intent();
			intent.setClass(Smartpa.this, TestLCD.class);
			startActivity(intent);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			onResume();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		StopAlarmRing();
		stopBroadcast();
	}
}
