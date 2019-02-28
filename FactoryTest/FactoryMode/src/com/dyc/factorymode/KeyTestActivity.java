package com.dyc.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.Window;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.provider.Settings;
import android.net.Uri;
import android.content.ContentResolver;

public class KeyTestActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "KeyTestActivity_Sagereal";
    private static String str = "|";
    private TextView show_key;
    private Button success;
    private Button mfail;
    private Button btn_up, btn_down, btn_power, btn_back, btn_home, btn_menu, key_camera;
    private Button key_share;
    private boolean key_share_test = false;
    private boolean pressUpKey = false, pressDownKey = false, pressBackKey = false, pressHomeKey = false, pressMenuKey = false, pressPowerKey = false, pressCamera = false;
    private Vibrator vibrator;
    private int testmode;
    private View mView;
    private int value;
    private boolean mIsVibrator = false;//添加振动测试功能
    
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.key_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        mView = getWindow().getDecorView();
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initview();
        vibrator = (Vibrator) getSystemService("vibrator");
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.android.phonewindowmanager.homekeypress");
        registerReceiver(getKeyReceiver, localIntentFilter);
        registerHomeKeyReceiver(this);
    }

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
                //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 begin
                case R.id.key_success_btn:
                    StartUtil.StartNextActivity("Key",1,testmode,KeyTestActivity.this,FingerprintTestActivity.class);
                    break;
                case R.id.key_fail_btn:
                    StartUtil.StartNextActivity("Key",2,testmode,KeyTestActivity.this,FingerprintTestActivity.class);
                    break;
                default:
                    break;
                //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 end
            }
        }
    }

    private void initview() {
        show_key = (TextView) findViewById(R.id.show_key);
        success = (Button) findViewById(R.id.key_success_btn);
        mfail = (Button) findViewById(R.id.key_fail_btn);
        mfail.setOnClickListener(this);
        success.setOnClickListener(this);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        success.setEnabled(false);
        btn_up = (Button) findViewById(R.id.key_up);
        btn_down = (Button) findViewById(R.id.key_down);
        btn_power = (Button) findViewById(R.id.key_power);
        btn_back = (Button) findViewById(R.id.key_back);
        btn_home = (Button) findViewById(R.id.key_home);
        btn_menu = (Button) findViewById(R.id.key_menu);
        key_share = (Button) findViewById(R.id.key_share);
        key_camera = (Button) findViewById(R.id.key_camera);
    }
    
    BroadcastReceiver getKeyReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            String action = paramIntent.getAction();
            if (action.equals("com.android.phonewindowmanager.homekeypress")) {
                str += getString(R.string.key_home);
                show_key.setText(str);
            }
        }
    };

    private void MyVibrate() {
        long[] arrayOfLong = new long[4];
        arrayOfLong[0] = 10L;
        arrayOfLong[1] = 400L;
        arrayOfLong[2] = 10L;
        arrayOfLong[3] = 400L;
        if (android.os.SystemProperties.get("ro.product.customer").equals("f1q_w302")) {
            arrayOfLong[0] = 1000L;
            arrayOfLong[1] = 1000L;
            arrayOfLong[2] = 1000L;
            arrayOfLong[3] = 1000L;
        }
        if (!mIsVibrator) {
            mIsVibrator = true;
            vibrator.vibrate(arrayOfLong, 0);
        }
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (value == 255) {
                        setActivityBrightness(10, KeyTestActivity.this);
                        android.util.Log.d("dyc_brightness","loop1.......");
                    } else {
                        android.util.Log.d("dyc_brightness","loop2.......");
                        setActivityBrightness(255, KeyTestActivity.this);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            mHanlder.sendEmptyMessage(1);
            mHanlder.postDelayed(this, 1500);//延迟1.5秒,再次执行task本身,实现了亮度变换的效果
        }
    };
    
    public void setActivityBrightness(int brightness, Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            value = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
        }
        android.util.Log.d(TAG, "value_before=" + value);
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        activity.getContentResolver().notifyChange(uri, null);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        mHanlder.postDelayed(task, 1000);
        setActivityBrightness(10, KeyTestActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MyVibrate();
    }

    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mHanlder.removeCallbacks(task);
        setActivityBrightness(255, KeyTestActivity.this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Override
    protected void onStop() {
        vibrator.cancel();
        super.onStop();
    }
    
    class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    btn_home.setVisibility(View.INVISIBLE);
                    pressHomeKey = true;
                    testFinish();
                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    btn_menu.setVisibility(View.INVISIBLE);
                    pressMenuKey = true;
                    testFinish();
                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    btn_home.setVisibility(View.INVISIBLE);
                    testFinish();
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    btn_home.setVisibility(View.INVISIBLE);
                    testFinish();
                }
            } else if ("com.android.zxl".equals(action)) {
                btn_menu.setVisibility(View.INVISIBLE);
                pressMenuKey = true;
                testFinish();
            } else if ("com.sagereal.keycode.power".equals(action)) {
                btn_power.setVisibility(View.INVISIBLE);
                pressPowerKey = true;
                testFinish();
            }
        }
    }

    private HomeWatcherReceiver mHomeWatcherReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter mHomeWatcherFilter = new IntentFilter();
        mHomeWatcherFilter.addAction("com.android.zxl");
        mHomeWatcherFilter.addAction("com.sagereal.keycode.power");
        context.registerReceiver(mHomeWatcherReceiver, mHomeWatcherFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        if (null != mHomeWatcherReceiver) {
            context.unregisterReceiver(mHomeWatcherReceiver);
        }
    }
    
    
    private void testFinish() {
        if (pressUpKey && pressDownKey && pressBackKey && pressHomeKey && pressMenuKey && pressPowerKey) {
            setActivityBrightness(255, KeyTestActivity.this);
            success.setEnabled(true);
            mHanlder.removeCallbacks(task);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        success.setFocusable(false);
        android.util.Log.d(TAG,"keyCode==="+keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                btn_menu.setVisibility(View.INVISIBLE);
                pressMenuKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_BACK:
                btn_back.setVisibility(View.INVISIBLE);
                pressBackKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_HOME:
                btn_home.setVisibility(View.INVISIBLE);
                pressHomeKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                btn_up.setVisibility(View.INVISIBLE);
                pressUpKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                btn_down.setVisibility(View.INVISIBLE);
                pressDownKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_POWER:
                show_key.setText(str);
                btn_power.setVisibility(View.INVISIBLE);
                pressPowerKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_F7:
                str += getString(R.string.key_sos);
                show_key.setText(str);
                return true;
            case KeyEvent.KEYCODE_F8:
                str += getString(R.string.key_ptt);
                show_key.setText(str);
                return true;
            case KeyEvent.KEYCODE_F12:
                btn_menu.setVisibility(View.INVISIBLE);
                pressMenuKey = true;
                testFinish();
                return true;
            case KeyEvent.KEYCODE_FOCUS:
                key_share.setVisibility(View.INVISIBLE);
                testFinish();
                return true;
            case KeyEvent.KEYCODE_CAMERA:
                key_camera.setVisibility(View.INVISIBLE);
                pressCamera = true;
                testFinish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        str = "|";
        unregisterReceiver(getKeyReceiver);
        unregisterHomeKeyReceiver(this);
    }
}
