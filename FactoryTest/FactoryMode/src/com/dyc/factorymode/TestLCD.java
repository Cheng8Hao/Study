package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class TestLCD extends Activity implements View.OnClickListener {

    private static final String TAG = "TestLCD_Sagereal";
    private LinearLayout imageView1;
    private ImageView imageView2;
    static int times = 0;
    private TextView ledremind;
    private Button nextButton;
    private Button mfail;
    private int testmode;
    private Intent ledIntent = null;
    private Intent btledIntent = null;
    private RelativeLayout imageViews;
    private FrameLayout ledviews;
    private View mView;
    //add to test led begin 
    private boolean isTesting = true;
	private boolean isTestLed = true;
	private Intent mIntent;
	//add to test led end
	private int colorrandom;
	private int color_id;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.test_lcd);
        android.util.Log.d(TAG, "onCreate success");
        Random random = new Random();//随机播放音乐
        colorrandom = random.nextInt(6);
        android.util.Log.d(TAG, "colorrandom" + colorrandom);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mView = getWindow().getDecorView();
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Root.getInstance().addActivity(this);
        Root.getInstance().haveLED = FeatureOption.SAGEREAL_FACTORYTEST_LED;
        //add to test led begin
        mIntent = new Intent("sagereal.intent.action.led");
        //add to test led end
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        times = 1;
        initview();
        imageViews.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imageView1.setVisibility(View.GONE);
                imageView2.setVisibility(View.VISIBLE);
                if (times == 1) {
                    //Redmine 164876 modify three colors for LCD test by lihongmin 20190116 begin
                    imageView2.setBackgroundResource(R.drawable.ic_gradient);
                } else if (times == 2) {
                    imageView2.setBackgroundColor(Color.WHITE);
                } else if (times == 3) {
                    imageView2.setBackgroundColor(Color.WHITE);
                } else if (times == 4) {
                    imageView2.setBackgroundColor(Color.BLACK);
                    //Redmine 164876 modify three colors for LCD test by lihongmin 20190116 end
                } else if (times == 5) {
                    imageView2.setVisibility(View.GONE);
                    imageViews.setVisibility(View.GONE);
                    ledviews.setVisibility(View.VISIBLE);
                }
                times++;
            }
        });

        Toast.makeText(this, getResources().getString(R.string.lcd), Toast.LENGTH_SHORT).show();
    }

    private void initview() {
        switch (colorrandom) {
                case 0:
                    color_id = R.id.imageView_1;
                    break;
                case 1:
                    color_id = R.id.imageView_2;
                    break;
                case 2:
                    color_id = R.id.imageView_3;
                    break;
                case 3:
                    color_id = R.id.imageView_4;
                    break;
                case 4:
                    color_id = R.id.imageView_5;
                    break;
                case 5:
                    color_id = R.id.imageView_6;
                    break;
                default:
                    return;
            }
        imageView1 = (LinearLayout) findViewById(color_id);
        imageView1.setVisibility(View.VISIBLE);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageViews = (RelativeLayout) findViewById(R.id.imageViews);
        ledviews = (FrameLayout) findViewById(R.id.led_views);
        ledremind = (TextView) findViewById(R.id.remind_led_test);
        nextButton = (Button) findViewById(R.id.led_success_btn);
        if (!Root.getInstance().haveLED) {
            ledremind.setVisibility(View.GONE);
        }
        mfail = (Button) findViewById(R.id.led_fail_btn);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
    }

    private void setLedStatus(boolean ledStatusOn, boolean btledStatusOn) {
        if (!Root.getInstance().haveLED) {
            return;
        }
        ledIntent = new Intent("sagereal.intent.action.led");
        btledIntent = new Intent("sagereal.intent.action.btled");
        ledIntent.putExtra("ledStatusOn", ledStatusOn);
        btledIntent.putExtra("btStatusOn", btledStatusOn);
        sendBroadcast(ledIntent);
        sendBroadcast(btledIntent);
    }

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.led_success_btn:
                StartUtil.StartNextActivity("Lcd",1,testmode,TestLCD.this,CameraTest.class);
                break;
            case R.id.led_fail_btn:
                StartUtil.StartNextActivity("Lcd",2,testmode,TestLCD.this,CameraTest.class);
                break;
            default:
                break;

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setLedStatus(false, false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //add to test led begin
        isTestLed = false;
		isTesting = false; 
		Bundle mBundle = new Bundle();
		mBundle.putBoolean("isTesting", isTesting);
		mBundle.putBoolean("isTestLed", isTestLed);
		mIntent.putExtras(mBundle);
		sendBroadcast(mIntent);
		//add to test led end
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setLedStatus(true, true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //add to test led begin
        isTestLed = true;
		isTesting = true; 
        Bundle mBundle = new Bundle();
		mBundle.putBoolean("isTesting", isTesting);
		mBundle.putBoolean("isTestLed", isTestLed);
		mIntent.putExtras(mBundle);
		sendBroadcast(mIntent);
		//add to test led end
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
        setLedStatus(false, false);
        //add to test led begin
        isTestLed = false;
		isTesting = false;  
		Bundle mBundle = new Bundle();
		mBundle.putBoolean("isTesting", isTesting);
		mBundle.putBoolean("isTestLed", isTestLed);
		mIntent.putExtras(mBundle);
		sendBroadcast(mIntent);
		//add to test led end
        super.onDestroy();
    }
}
