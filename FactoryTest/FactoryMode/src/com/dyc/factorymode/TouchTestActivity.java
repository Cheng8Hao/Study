package com.dyc.factorymode;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.os.Build;
import android.content.SharedPreferences;
import android.content.Context;

public class TouchTestActivity extends Activity implements OnTouchListener {
    private static final String TAG = "TouchTestActivity_Sagereal";
    private boolean LastMotionResult = true;
    private boolean drawContinue = true;
    private boolean flagQuit = false;
    private boolean isDrawEnough = true;
    private GestureOverlayView[] lines;
    private String mFirmwareVersionName = null;
    private String mFirmwareVersionNameFinal = null;
    private String mInfoLog = null;
    private Runnable mRunnable;
    private String mStrCurrenCTP = null;
    private String mStrExpectCTP = null;
    private String mStrNoFile = null;
    private WakeLock mWakeLock;
    private float maxX;
    private float maxY;
    private float minX;
    private float minY;
    private boolean motionResult = true;
    private boolean onePointer = false;
    private float sourceX;
    private float sourceY;
    private StatusBarManager statusBarManager;
    private boolean myFinish = false;
    private int testmode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        android.util.Log.d(TAG, "onCreate success");
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        this.statusBarManager = (StatusBarManager) getSystemService("statusbar");
        this.statusBarManager.disable(67043328);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED);dyc_change
        }
        initView();
    }

    private void initView() {
        setContentView(R.layout.touch_test);
        Root.getInstance().addActivity(this);
        Root.getInstance().setScreenBrightness(1,this);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.torch_text);
        myLayout.setBackgroundColor(-16777216);
        myLayout.setSystemUiVisibility(16777216);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService("window");
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        android.util.Log.d(TAG, "WIN height = " + height + ", width = " + width);
        GestureOverlayView height_line1 = (GestureOverlayView) myLayout.findViewById(R.id.height_line1);
        GestureOverlayView height_line2 = (GestureOverlayView) myLayout.findViewById(R.id.height_line2);
        GestureOverlayView height_line3 = (GestureOverlayView) myLayout.findViewById(R.id.height_line3);
        height_line1.getLayoutParams().height = height / 11;
        height_line2.getLayoutParams().height = height / 11;
        height_line3.getLayoutParams().height = height / 11;
        height_line1.setMinimumHeight(height / 11);
        height_line2.setMinimumHeight(height / 11);
        height_line3.setMinimumHeight(height / 11);
        GestureOverlayView width_line1 = (GestureOverlayView) myLayout.findViewById(R.id.width_line1);
        GestureOverlayView width_line2 = (GestureOverlayView) myLayout.findViewById(R.id.width_line2);
        GestureOverlayView width_line3 = (GestureOverlayView) myLayout.findViewById(R.id.width_line3);
        width_line1.getLayoutParams().width = width / 7;
        width_line2.getLayoutParams().width = width / 7;
        width_line3.getLayoutParams().width = width / 7;
        width_line1.setMinimumHeight(width / 7);
        width_line2.setMinimumHeight(width / 7);
        width_line3.setMinimumHeight(width / 7);
        this.lines = new GestureOverlayView[6];
        this.lines[0] = height_line1;
        this.lines[1] = height_line2;
        this.lines[2] = height_line3;
        this.lines[3] = width_line1;
        this.lines[4] = width_line2;
        this.lines[5] = width_line3;
        for (int i = 0; i < 6; i++) {
            this.lines[i].setOnTouchListener(this);
            this.lines[i].setTag(Integer.valueOf(i));
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG, "view id = " + view.getTag() + ",view.getWidth() = " + view.getWidth() + ", view.getHeight() = " + view.getHeight() + ", view.getLeft() = " + view.getLeft() + ", view.getRight() = " + view.getRight() + ", view.getTop() = " + view.getTop() + ", view.getBottom() = " + view.getBottom());
        int i = Integer.parseInt(view.getTag().toString());
        view.setVisibility(0);
        this.drawContinue = isMotionRight(view, motionEvent, view.getWidth() > view.getHeight() ? 0 : 1);
        if (this.drawContinue && motionEvent.getAction() == 1) {
            view.setVisibility(4);
            if (i == 5) {
                if (this.flagQuit) {
                    return true;
                }
                myFinish = true;
                this.flagQuit = true;

            } else {
                this.lines[i + 1].setVisibility(0);
            }
        } else {
            view.setVisibility(0);
            if (motionEvent.getAction() == 1) {
                if (i <= 2) {
                    view.setBackgroundResource(R.drawable.width_line_failure);
                } else {
                    view.setBackgroundResource(R.drawable.height_line_failure);
                }
            }
            Log.d(TAG, "current view draw failure...");
        }
        android.util.Log.d(TAG, "myFinish==" + myFinish);
        if (this.flagQuit && myFinish) {
            startActivity(new Intent(TouchTestActivity.this, DiagonalTestActivity.class));
            finish();
        }
        return true;
    }

    private boolean isMotionRight(View view, MotionEvent event, int direction) {
        boolean z = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.motionResult = true;
                this.minX = (float) view.getLeft();
                this.maxX = (float) view.getRight();
                this.minY = (float) view.getTop();
                this.maxY = (float) view.getBottom();
                this.sourceX = event.getRawX();
                this.sourceY = event.getRawY();
                Log.d(TAG, "ACTION_DOWN event.getAction() = " + event.getAction() + " minX = " + this.minX + " maxX = " + this.maxX + " minY = " + this.minY + " maxY = " + this.maxY + " direction = " + direction + " result = " + this.motionResult + ", event.getRawX() = " + event.getRawX() + ", event.getRawY() = " + event.getRawY());
                onePointer = true;
                this.LastMotionResult = true;
                if (direction != 0 || event.getRawY() < this.minY || event.getRawY() > this.maxY) {
                    if (direction == 1 && this.minX <= event.getRawX() && event.getRawX() <= this.maxX) {
                        this.motionResult = true;
                        break;
                    }
                    this.motionResult = false;
                    break;
                }
                this.motionResult = true;
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP event.getAction() = " + event.getAction() + " minX = " + this.minX + " maxX = " + this.maxX + " minY = " + this.minY + " maxY = " + this.maxY + " direction = " + direction + " result = " + this.motionResult + ", event.getRawX() = " + event.getRawX() + ", event.getRawY() = " + event.getRawY());
                if ((direction != 0 || event.getRawY() < this.minY || event.getRawY() > this.maxY) && (direction != 1 || this.minX > event.getRawX() || event.getRawX() > this.maxX)) {
                    Log.d(TAG, "22 out of texview...");
                    this.motionResult = false;
                }
                if (direction == 0) {
                    boolean z2;
                    if (((double) (Math.abs(event.getRawX() - this.sourceX) / ((float) view.getWidth()))) >= 0.92d) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    this.isDrawEnough = z2;
                    Log.d(TAG, "PERCENT = " + (Math.abs(event.getRawX() - this.sourceX) / ((float) view.getWidth())) + " event.getRawX() = " + event.getRawX() + " event.getRawY() = " + event.getRawY());
                } else {
                    Log.d(TAG, "PERCENT = " + (Math.abs(event.getRawY() - this.sourceY) / ((float) view.getHeight())) + " event.getRawX() = " + event.getRawX() + " event.getRawY() = " + event.getRawY());
                    this.isDrawEnough = ((double) (Math.abs(event.getRawY() - this.sourceY) / ((float) view.getHeight()))) >= 0.92d;
                }
                this.maxY = 0.0f;
                this.minY = 0.0f;
                this.maxX = 0.0f;
                this.minX = 0.0f;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE event.getAction() = " + event.getAction() + " minX = " + this.minX + " maxX = " + this.maxX + " minY = " + this.minY + " maxY = " + this.maxY + " direction = " + direction + " result = " + this.motionResult + ", event.getRawX() = " + event.getRawX() + ", event.getRawY() = " + event.getRawY());
                if (this.motionResult) {
                    if (direction != 0 || event.getRawY() < this.minY || event.getRawY() > this.maxY) {
                        if (direction == 1 && this.minX <= event.getRawX() && event.getRawX() <= this.maxX) {
                            this.motionResult = true;
                            break;
                        }
                        Log.d(TAG, "out of texview...");
                        this.motionResult = false;
                        break;
                    }
                    this.motionResult = true;
                    break;
                }
                break;
        }
        if (this.motionResult) {
            z = this.isDrawEnough;
        }
        if (event.getPointerCount() != 1) {
            onePointer = false;
        }

        this.motionResult = z;
        Log.d(TAG, "event.getAction() = " + event.getAction() + " minX = " + this.minX + " maxX = " + this.maxX + " minY = " + this.minY + " maxY = " + this.maxY + " direction = " + direction + "isDrawEnough = " + this.isDrawEnough + " result = " + this.motionResult);
        this.LastMotionResult = this.motionResult;
        this.isDrawEnough = true;
        return this.motionResult && onePointer;
    }

    protected void onResume() {
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        if (this.mWakeLock == null) {
            this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(26, "wg_wakelock");
            this.mWakeLock.acquire();
        }
        System.putInt(getContentResolver(), "pointer_location_itel", 1);
    }

    protected void onPause() {
        super.onPause();
        if (this.mWakeLock != null && this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
        this.statusBarManager.disable(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                SharedPreferences.Editor editor = getSharedPreferences("result", MODE_PRIVATE).edit();
                editor.putInt("Tp", 2);
                editor.commit();
                if (testmode == 0) {
                    startActivity(new Intent(TouchTestActivity.this, DiagonalTestActivity.class));
                }else if (testmode == 3){
                    startActivity(new Intent(TouchTestActivity.this, DiagonalTestActivity.class));
                }
                finish();
                return true;
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                return true;
            case KeyEvent.KEYCODE_F7:
                return true;
            case KeyEvent.KEYCODE_F8:
                return true;
            case KeyEvent.KEYCODE_F12:
                return true;
            case KeyEvent.KEYCODE_FOCUS:
                return true;
            default:
                return false;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
