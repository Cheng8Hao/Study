package com.dyc.factorymode;


import android.R.integer;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;
import android.location.Location;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;
import android.view.Gravity;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Button;
import android.content.SharedPreferences;

public class CompassTest extends Activity implements SensorEventListener, View.OnClickListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticSensor;
    private LinearLayout layout_direction;
    private CompassView compassView;
    private LinearLayout layout_angle;
    private ImageView animate;
    private FrameLayout calibrate;
    private AnimationDrawable frameAnimation;
    private LayoutParams lp;
    private ImageView east;
    private ImageView west;
    private ImageView south;
    private ImageView north;
    private AccelerateInterpolator accelerateInterpolator;
    private boolean mStopDrawing;// 是否停止指南针旋转的标志位

    private float mDirection;
    private float mTargetDirection;
    private float[] mAccelerometerValues = new float[3];
    private float[] mMagneticValues = new float[3];
    private float[] values = new float[3];
    private float[] rotateMatrix = new float[9];  //旋转矩阵

    private static final int EXIT_TIME = 2000;// 两次按返回键的间隔判断
    private final float MAX_ROATE_DEGREE = 1.0f;// 最多旋转一周，即360

    private TextView showPassOrFail;
    private float firstDegree = 0;
    private boolean isSuccess = false ,testpass = true;
    private boolean isStartTest = false;
    private boolean isfirstDegree = true;
    private static final int COMPASS_START = 0;
    private static final int COMPASS_SUCCESS = 1;
    private static final int COMPASS_FAULT = 2;
    private static final int COMPASS_START_TIME = 3000;
    private static final int COMPASS_FAULT_TIME = 20000;
    private Button nextButton;
    private Button mfail;
    private int testmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.compass_test);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Root.getInstance().addActivity(this);
        initServices();
        initview();
    }

    private void initview() {
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        layout_direction = (LinearLayout) findViewById(R.id.layout_direction);
        layout_angle = (LinearLayout) findViewById(R.id.layout_angle);
        compassView = (CompassView) findViewById(R.id.compassView);
        showPassOrFail = (TextView) findViewById(R.id.pass_or_fail);
        nextButton = (Button) findViewById(R.id.compass_success_btn);
        mfail = (Button) findViewById(R.id.compass_fail_btn);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
        nextButton.setEnabled(false);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        east = new ImageView(this);
        west = new ImageView(this);
        south = new ImageView(this);
        north = new ImageView(this);
        accelerateInterpolator = new AccelerateInterpolator();   //动画加速器
        //mStopDrawing = true;
        calibrate = (FrameLayout) findViewById(R.id.calibrate);
        animate = (ImageView) findViewById(R.id.animate);

        if (FeatureOption.SAGEREAL_COMPASS_SHOW_ANIMATION) {
            mAnimationHandler.sendMessageDelayed(new Message(), 3000);
        } else {
            mAnimationHandler.sendMessage(new Message());
        }

    }

    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.compass_success_btn:
                StartUtil.StartNextActivity("Compass",1,testmode,CompassTest.this,AudioLoopback.class);
                break;
            case R.id.compass_fail_btn:
                StartUtil.StartNextActivity("Compass",2,testmode,CompassTest.this,AudioLoopback.class);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mStopDrawing= false;
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mHandler.postDelayed(mUpdateViewRunnable, 20);
        Message msg1 = Message.obtain(mHandler, COMPASS_START);
        mHandler.sendMessageDelayed(msg1, COMPASS_START_TIME);
        Message msg2 = Message.obtain(mHandler, COMPASS_FAULT);
        mHandler.sendMessageDelayed(msg2, COMPASS_FAULT_TIME);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mStopDrawing = true;
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        //super.onWindowFocusChanged(hasFocus);
        frameAnimation = (AnimationDrawable) animate.getDrawable();
        frameAnimation.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Runnable mUpdateViewRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            if (mDirection != mTargetDirection) {
                float to = mTargetDirection;
                if (to - mDirection > 180) {
                    to -= 360;
                } else if (to - mDirection < -180) {

                    to += 360;
                }
                float distance = to - mDirection;
                if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                    distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                }
                mDirection = mDirection + ((to - mDirection) * accelerateInterpolator
                        .getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f));
            }
            if (compassView != null) {
                //compassView.updateDirection(-mTargetDirection);
                compassView.updateDirection(-mDirection);
                updateDirection();
                mHandler.postDelayed(mUpdateViewRunnable, 20);
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMPASS_START:
                    if (!isSuccess) {
                        isStartTest = true;
                    }
                    break;
                case COMPASS_FAULT:
                    if (!isSuccess) {
                        showPassOrFail.setTextColor(Color.RED);
                        showPassOrFail.setText(R.string.compass_fail);
                    }
                    break;
                case COMPASS_SUCCESS:
                  if(testpass){
                        nextButton.setEnabled(true);
                        testpass=false;
                        nextButton.performClick();
                    }
                    showPassOrFail.setTextColor(Color.GREEN);
                    showPassOrFail.setText(R.string.compass_success);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler mAnimationHandler = new Handler() {
        public void handleMessage(Message msg) {
            calibrate.setVisibility(View.GONE);
        }
    };

    private void initServices() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //加速度计
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //磁感应计
    }

    public void updateDirection() {
        layout_angle.removeAllViews();  //必须先清空ViewGroup
        layout_direction.removeAllViews();
        int temp = (int) mTargetDirection;
        boolean tenPointShow = false;
        if (temp >= 100) {
            layout_angle.addView(getNumImageView(temp / 100));
            temp %= 100;
            tenPointShow = true;
        }
        if (temp >= 10 || tenPointShow) {
            layout_angle.addView(getNumImageView(temp / 10));
            temp %= 10;
        }
        layout_angle.addView(getNumImageView(temp));
        layout_angle.addView(getNumImageView(-1));

        if (mTargetDirection > 22.5f && mTargetDirection < 157.5f) {
            east.setImageResource(R.drawable.e_cn);
            layout_direction.addView(east, lp);
        } else if (mTargetDirection > 202.5f && mTargetDirection < 337.5f) {
            west.setImageResource(R.drawable.w_cn);
            layout_direction.addView(west, lp);
        }
        if (mTargetDirection > 112.5f && mTargetDirection < 247.5f) {
            south.setImageResource(R.drawable.s_cn);
            layout_direction.addView(south, lp);
        } else if (mTargetDirection < 67.5f || mTargetDirection > 292.5f) {
            north.setImageResource(R.drawable.n_cn);
            layout_direction.addView(north, lp);
        }
    }

    public ImageView getNumImageView(int num) {
        ImageView angle = new ImageView(this);
        switch (num) {
            case 0:
                angle.setImageResource(R.drawable.number_0);
                break;
            case 1:
                angle.setImageResource(R.drawable.number_1);
                break;
            case 2:
                angle.setImageResource(R.drawable.number_2);
                break;
            case 3:
                angle.setImageResource(R.drawable.number_3);
                break;
            case 4:
                angle.setImageResource(R.drawable.number_4);
                break;
            case 5:
                angle.setImageResource(R.drawable.number_5);
                break;
            case 6:
                angle.setImageResource(R.drawable.number_6);
                break;
            case 7:
                angle.setImageResource(R.drawable.number_7);
                break;
            case 8:
                angle.setImageResource(R.drawable.number_8);
                break;
            case 9:
                angle.setImageResource(R.drawable.number_9);
                break;
            default:
                angle.setImageResource(R.drawable.degree);
                break;
        }
        angle.setLayoutParams(lp);
        return angle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {    //加速度计
            mAccelerometerValues = event.values;
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {    //磁感应计
            mMagneticValues = event.values;
        }
        SensorManager.getRotationMatrix(rotateMatrix, null,
                mAccelerometerValues, mMagneticValues); //得到旋转矩阵rotateMatrix
        /*注意：此处得到的values为弧度*/
        SensorManager.getOrientation(rotateMatrix, values);
        int degree = (int) Math.toDegrees(values[0]);
        /*
         * -180度到180度，从正北方向顺时针转到正南方向，此时角度变化为0~180度
         * 从正北方向逆时针转到正南方向，此时角度为-180~0度
         */
        if (degree < 0) {  //转化为正常值
            degree += 360;
        }

        mTargetDirection = degree;  //赋值给全局变量
        Log.d("liuhanling", "mTargetDirection = " + mTargetDirection);

        if (isStartTest) {
            if (isfirstDegree) {
                firstDegree = degree;
                isfirstDegree = false;
                Log.d("liuhanling", "firstDegree = " + firstDegree);
            }

            Log.d("liuhanling", "rotation = " + Math.abs(firstDegree - mTargetDirection));
            if ((Math.abs(firstDegree - mTargetDirection) >= 45) && (!isSuccess)) {
                Message msg = Message.obtain(mHandler, COMPASS_SUCCESS);
                mHandler.sendMessage(msg);
                isSuccess = true;
            }
        }
    }


}
