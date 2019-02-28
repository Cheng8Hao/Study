package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class RecTest3 extends Activity implements
		android.view.View.OnClickListener {

	private ToneGenerator mToneGenerator;
	private AudioManager mAudioManager;
	private int defaultVolume = 0;
	private Button nextButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		setContentView(R.layout.rec_test);
		Root.getInstance().addActivity(this);
		mToneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL,ToneGenerator.MAX_VOLUME);
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		defaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, AudioManager.FLAG_VIBRATE);
		System.out.println("defaultVolume = "+defaultVolume +"  volume = "+volume);
		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 
				defaultVolume, 0);
		//Intent intent = new Intent();
		//intent.setClass(RecTest3.this, touch.class);
		//startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 
					defaultVolume, 0);
		//	Intent intent = new Intent();
		//	intent.setClass(RecTest3.this, touch.class);
		//	startActivity(intent);
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			mToneGenerator.stopTone();
			mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mToneGenerator.stopTone();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
