package com.dyc.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FingerprintTestActivity extends Activity {
    private final static String TAG = "FingerprintTestActivity_sagereal";
    private final static int MSG_SUCCESS_STATE = 1;
    private final static int MSG_FAILED_STATE = 2;
    private Button mFailed;
    private Button mSuccess;
    private Button mStart;
    private TextView mResult;
    private String fingerprint = "";
    private WakeLock mWakeLock;
    private int testmode;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.test_fingerprint);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        try {
            fingerprint = readFromFile();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver, filter);
        initview();
    }

    OnClickListener m = new OnClickListener() {
        public void onClick(View paramView) {
            switch (paramView.getId()) {
                case R.id.fingerprint_success_btn:
                    mFingerpringThread.stopLogcat();
                    StartUtil.StartNextActivity("Fingerprint", 1, testmode, FingerprintTestActivity.this, TestReport.class);
                    break;
                case R.id.fingerprint_fail_btn:
                    mFingerpringThread.stopLogcat();
                    StartUtil.StartNextActivity("Fingerprint", 2, testmode, FingerprintTestActivity.this, TestReport.class);
                    break;
                case R.id.start_test:
                    mSuccess.setEnabled(true);
                    Intent intent3 = new Intent(Intent.ACTION_MAIN);
                    ComponentName componetName3 = new ComponentName("com.swfp.factory", "com.swfp.activity.DetectActivity");
                    intent3.setComponent(componetName3);
                    startActivity(intent3);
                    break;
                default:
                    break;
            }
        }
    };

    private void initview() {
        mResult = (TextView) findViewById(R.id.result);
        mSuccess = (Button) findViewById(R.id.fingerprint_success_btn);
        mFailed = (Button) findViewById(R.id.fingerprint_fail_btn);
        mStart = (Button) findViewById(R.id.start_test);
        mSuccess.setEnabled(false);
        mSuccess.setOnClickListener(m);
        mFailed.setOnClickListener(m);
        mStart.setOnClickListener(m);
        mResult.setText("");
        //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 begin
        //startFingerprint();
        //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 begin
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS_STATE:
                    mResult.setText(getString(R.string.success));
                    mResult.setTextColor(Color.GREEN);
                    mSuccess.setEnabled(true);
                    break;
                case MSG_FAILED_STATE:
                    mResult.setText(getString(R.string.mfailed));
                    mResult.setTextColor(Color.RED);
                    mSuccess.setEnabled(true);
                    break;
                default:
                    return;
            }
        }
    };

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBatInfoReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private FingerpringThread mFingerpringThread = new FingerpringThread();

    private final class FingerpringThread extends Thread {
        private Process logcatProcess = null;
        private BufferedReader bufferedReader = null;

        @Override
        public void run() {
            try {
                logcatProcess = Runtime.getRuntime().exec("logcat -c");
                logcatProcess.waitFor();
                logcatProcess = Runtime.getRuntime().exec("logcat -v tag -s [GF_HAL][gf_hal_milan_f_series]");
                bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
                String line;
                String strFilter = "GF_IRQ_FINGER_UP_MASK";
                Log.d(TAG, "while...");
                while (true) {
                    line = bufferedReader.readLine();
                    if (line != null && line.contains(strFilter)) {
                        Log.d(TAG, "get fingerprint key up");
                        Message msg = Message.obtain(mHandler, MSG_SUCCESS_STATE);
                        mHandler.sendMessage(msg);
                        break;
                    }
                }
                stopLogcat();
            } catch (Exception e) {
                Log.d(TAG, "<run>: " + e);
            }
        }

        public void stopLogcat() {
            Log.d(TAG, "<stopLogcat>");
            try {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    bufferedReader = null;
                }
            } catch (Exception e) {
                Log.d(TAG, "<stopLogcat>: " + e);
            }
        }
    }

    private void startFingerprint() {
        Log.d(TAG, "<startFingerprint> " + mFingerpringThread.isAlive());
        if (!mFingerpringThread.isAlive())
            mFingerpringThread.start();
    }

    public static String readFromFile() throws IOException {
        File file = new File("proc/hw_info/hw_info");
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        String touch_ic_number = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            // 读取的每一行内容后面加上一个空格用于拆分成语句
            if (temp.contains("Finger_Sensor")) {
                touch_ic_number = temp.replaceAll("Finger_Sensor:", "");
            }
            temp = br.readLine();
        }
        sb.append(touch_ic_number);
        return sb.toString();
    }
}

