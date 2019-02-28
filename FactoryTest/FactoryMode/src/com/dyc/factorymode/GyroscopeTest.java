package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;

public class GyroscopeTest extends Activity implements SensorEventListener, OnClickListener {

    private static final String TAG = "GyroscopeTest_Sagereal";
    // 将纳秒转换为秒
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final int GYROSCOPE_TEST_START = 0;
    private static final int GYROSCOPE_TEST_SUCCESS = 1;
    private static final int GYROSCOPE_TEST_FAIL = 2;
    private static final int GYROSCOPE_TEST_SUCCESS_X = 3;
    private static final int GYROSCOPE_TEST_SUCCESS_Y = 4;
    private static final int GYROSCOPE_TEST_SUCCESS_Z = 5;
    private static final int GYROSCOPE_TEST_START_TIME = 3000;
    private static final int GYROSCOPE_TEST_FAIL_TIME = 20000;

    private float timestamp = 0;
    private float angle[] = new float[3];
    private float degree[] = new float[3];

    private boolean isStartTest = false;
    private boolean isSuccess = false;
    private boolean isSuccessX = false;
    private boolean isSuccessY = false;
    private boolean isSuccessZ = false;
    private boolean positive[] = new boolean[3];
    private boolean negative[] = new boolean[3];

    private TextView mDegreeX;
    private TextView mDegreeY;
    private TextView mDegreeZ;
    private TextView mResultX;
    private TextView mResultY;
    private TextView mResultZ;
    private TextView mResult;
    private Button mNextTest;

    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.gyroscope_test);
        Root.getInstance().addActivity(this);
        initView();
        initGyroscope();
    }

    public static native int getGoogleKeyState();

    @Override
    protected void onResume() {
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        updateView();
        if (mSensorManager != null && mGyroscopeSensor != null) {
            mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        mHandler.postDelayed(mUpdateViewRunnable, 20);

        Message msg1 = Message.obtain(mHandler, GYROSCOPE_TEST_START);
        mHandler.sendMessageDelayed(msg1, GYROSCOPE_TEST_START_TIME);

        Message msg2 = Message.obtain(mHandler, GYROSCOPE_TEST_FAIL);
        mHandler.sendMessageDelayed(msg2, GYROSCOPE_TEST_FAIL_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private void initView() {
        mDegreeX = (TextView) findViewById(R.id.tv_degree_x);
        mDegreeY = (TextView) findViewById(R.id.tv_degree_y);
        mDegreeZ = (TextView) findViewById(R.id.tv_degree_z);
        mResult = (TextView) findViewById(R.id.tv_result);
        mResultX = (TextView) findViewById(R.id.tv_result_x);
        mResultY = (TextView) findViewById(R.id.tv_result_y);
        mResultZ = (TextView) findViewById(R.id.tv_result_z);
        mNextTest = (Button) findViewById(R.id.next_test);
        mNextTest.setOnClickListener(this);
    }

    private void initGyroscope() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void updateView() {
        timestamp = 0;
        if (isSuccess) {
            mResult.setText(null);
            isSuccess = false;
        }
        if (isSuccessX) {
            mResultX.setText(null);
            isSuccessX = false;
        }
        if (isSuccessY) {
            mResultY.setText(null);
            isSuccessY = false;
        }
        if (isSuccessZ) {
            mResultZ.setText(null);
            isSuccessZ = false;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GYROSCOPE_TEST_START:
                    isStartTest = true;
                    break;
                case GYROSCOPE_TEST_SUCCESS:
                    mResult.setTextColor(Color.BLUE);
                    mResult.setText(R.string.gyroscope_test_success);
                    break;
                case GYROSCOPE_TEST_FAIL:
                    if (!isSuccess) {
                        mResult.setTextColor(Color.RED);
                        mResult.setText(R.string.gyroscope_test_fault);
                    }
                    if (!isSuccessX) {
                        mResultX.setTextColor(Color.RED);
                        mResultX.setText(R.string.gyroscope_test_fail);
                    }
                    if (!isSuccessY) {
                        mResultY.setTextColor(Color.RED);
                        mResultY.setText(R.string.gyroscope_test_fail);
                    }
                    if (!isSuccessZ) {
                        mResultZ.setTextColor(Color.RED);
                        mResultZ.setText(R.string.gyroscope_test_fail);
                    }
                    break;
                case GYROSCOPE_TEST_SUCCESS_X:
                    mResultX.setTextColor(Color.BLUE);
                    mResultX.setText(R.string.gyroscope_test_pass);
                    break;
                case GYROSCOPE_TEST_SUCCESS_Y:
                    mResultY.setTextColor(Color.BLUE);
                    mResultY.setText(R.string.gyroscope_test_pass);
                    break;
                case GYROSCOPE_TEST_SUCCESS_Z:
                    mResultZ.setTextColor(Color.BLUE);
                    mResultZ.setText(R.string.gyroscope_test_pass);
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable mUpdateViewRunnable = new Runnable() {

        @Override
        public void run() {
            mDegreeX.setText(getString(R.string.gyroscope_x_axis) + degree[0]);
            mDegreeY.setText(getString(R.string.gyroscope_y_axis) + degree[1]);
            mDegreeZ.setText(getString(R.string.gyroscope_z_axis) + degree[2]);
            mHandler.postDelayed(mUpdateViewRunnable, 20);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (timestamp != 0) {
            // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
            final float dT = (event.timestamp - timestamp) * NS2S;
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，逆时针旋转o，得到正值；否则，为负值
            // 得到旋转弧度，并将弧度转化为角度
            for (int i = 0; i < angle.length; i++) {
                angle[i] = event.values[i] * dT;
                degree[i] = (float) Math.toDegrees(angle[i]);
            }

            if (isStartTest && !isSuccess) {
                for (int i = 0; i < degree.length; i++) {
                    if (0 == degree[i]) {

                    } else if (degree[i] > 0) {
                        positive[i] = true;
                    } else {
                        negative[i] = true;
                    }
                }
                if (!isSuccessX && positive[0] && negative[0]) {
                    Message msg = Message.obtain(mHandler, GYROSCOPE_TEST_SUCCESS_X);
                    mHandler.sendMessage(msg);
                    isSuccessX = true;
                }

                if (!isSuccessY && positive[1] && negative[1]) {
                    Message msg = Message.obtain(mHandler, GYROSCOPE_TEST_SUCCESS_Y);
                    mHandler.sendMessage(msg);
                    isSuccessY = true;
                }

                if (!isSuccessZ && positive[2] && negative[2]) {
                    Message msg = Message.obtain(mHandler, GYROSCOPE_TEST_SUCCESS_Z);
                    mHandler.sendMessage(msg);
                    isSuccessZ = true;
                }
                if (isSuccessX && isSuccessY && isSuccessZ) {
                    Message msg = Message.obtain(mHandler, GYROSCOPE_TEST_SUCCESS);
                    mHandler.sendMessage(msg);
                    isSuccess = true;
                }
            }
        }

        timestamp = event.timestamp;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(GyroscopeTest.this, AutoTest.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Intent intent = new Intent();
            intent.setClass(GyroscopeTest.this, AutoTest.class);
            startActivity(intent);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            onResume();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
