package com.dyc.factorymode;

import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.content.SharedPreferences;

public class DiagonalTestActivity extends Activity {

    private static final String TAG = "DiagonalTestActivity_Sagereal";
    private boolean isStart = false;
    private GestureOverlayView mOverlay;
    private Intent intent;
    private WakeLock mWakeLock;
    private int testmode;

    public class MyView extends GestureOverlayView implements OnGestureListener {
        private HashMap<Integer, Boolean> count_oblique1 = new HashMap();
        private HashMap<Integer, Boolean> count_oblique2 = new HashMap();
        private Canvas mCanvas;
        private final Paint mPathPaint;
        private final ArrayList<PointerState> mPointers = new ArrayList();
        private int mScreenHeight;
        private int mScreenWidth;
        private final Paint mPaint;

        public MyView(Context context, int height, int width) {
            super(context);
            android.util.Log.d(TAG, "MyView() success");
            this.mScreenHeight = height;
            this.mScreenWidth = width;
            this.mPathPaint = new Paint();
            this.mPathPaint.setAntiAlias(false);
            this.mPathPaint.setARGB(255, 255, 0, 0);
            this.mPathPaint.setStrokeWidth(8.0f);
            addOnGestureListener(this);
            mPaint = new Paint();
            mPaint.setAntiAlias(false);
            mPaint.setARGB(255, 255, 0, 0);
            mPaint.setStrokeWidth(1.0f);
        }

        private void detect_points_draw(int x, int y, Paint paint) {
            int update_py, update_px, update_x, update_y;
            if ((this.mScreenHeight * x) - (this.mScreenWidth * y) > this.mScreenWidth * -50 && (this.mScreenHeight * x) - (this.mScreenWidth * y) < this.mScreenHeight * 50) {
                Log.d(TAG, "detect_points_draw111");
                android.util.Log.d(TAG, "MyView() success");
                if (y / 50 == 0) {
                    paint.setARGB(255, 255, 0, 0);
                    mCanvas.drawLine(50.0f, 0.0f, (float) ((((mScreenWidth - 50) * 50) / (mScreenHeight - 50)) + 50), 50.0f, paint);
                    mCanvas.drawLine(0.0f, 50.0f, (float) ((((mScreenWidth - 50) * 50) / (mScreenHeight - 50)) + 50), 50.0f, paint);
                    //paint.setARGB(255, 255, 255, 255);
                } else {
                    update_py = (y / 50) * 50;
                    update_px = (((this.mScreenWidth - ((this.mScreenWidth * 50) / this.mScreenHeight)) * ((y / 50) * 50)) / (this.mScreenHeight - 50)) - ((this.mScreenWidth * 50) / this.mScreenHeight);
                    update_x = update_px + ((this.mScreenWidth * 50) / this.mScreenHeight);
                    update_y = update_py + 50;
                    paint.setARGB(255, 255, 0, 0);
                    this.mCanvas.drawLine((float) update_px, (float) update_py, (float) update_x, (float) update_y, paint);
                    this.mCanvas.drawLine((float) update_px, (float) update_py, (float) (update_x + 50), (float) update_py, paint);
                    this.mCanvas.drawLine((float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_px) + 50), (float) update_py, (float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_x) + 50), (float) update_y, paint);
                    this.mCanvas.drawLine((float) update_x, (float) update_y, (float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_x) + 50), (float) update_y, paint);
                }
                //this.count_oblique1.put(Integer.valueOf(y / 50), Boolean.valueOf(true));
            }
            if (((this.mScreenWidth * this.mScreenHeight) - (this.mScreenWidth * y)) - (this.mScreenHeight * x) <= this.mScreenHeight * 50 && ((this.mScreenWidth * this.mScreenHeight) - (this.mScreenWidth * y)) - (this.mScreenHeight * x) >= this.mScreenWidth * -50) {
                Log.d(TAG, "detect_points_draw222");
                update_py = (y / 50) * 50;
                update_px = (this.mScreenWidth - 50) - ((((y / 50) * 50) * this.mScreenWidth) / this.mScreenHeight);
                update_x = update_px - ((this.mScreenWidth * 50) / this.mScreenHeight);
                update_y = update_py + 50;
                paint.setARGB(255, 255, 0, 0);
                this.mCanvas.drawLine((float) update_px, (float) update_py, (float) update_x, (float) update_y, paint);
                this.mCanvas.drawLine((float) update_x, (float) update_y, (float) ((update_x + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_y, paint);
                this.mCanvas.drawLine((float) update_px, (float) update_py, (float) ((update_px + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_py, paint);
                if (y / 50 != 0) {
                    this.mCanvas.drawLine((float) ((update_px + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_py, (float) ((update_x + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_y, paint);
                }
                //this.count_oblique2.put(Integer.valueOf(y / 50), Boolean.valueOf(true));
            }
        }

        private void detect_points(int x, int y) {
            int update_py, update_px, update_x, update_y;
            if ((this.mScreenHeight * x) - (this.mScreenWidth * y) > this.mScreenWidth * -50 && (this.mScreenHeight * x) - (this.mScreenWidth * y) < this.mScreenHeight * 50) {
                Log.d(TAG, "detect_points111");
                if (y / 50 == 0) {
                    this.mPathPaint.setARGB(255, 0, 255, 0);
                    this.mCanvas.drawLine(50.0f, 0.0f, (float) ((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + 50), 50.0f, this.mPathPaint);
                    this.mCanvas.drawLine(0.0f, 50.0f, (float) ((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + 50), 50.0f, this.mPathPaint);
                    this.mPathPaint.setARGB(255, 255, 255, 255);
                } else {
                    update_py = (y / 50) * 50;
                    update_px = (((this.mScreenWidth - ((this.mScreenWidth * 50) / this.mScreenHeight)) * ((y / 50) * 50)) / (this.mScreenHeight - 50)) - ((this.mScreenWidth * 50) / this.mScreenHeight);
                    update_x = update_px + ((this.mScreenWidth * 50) / this.mScreenHeight);
                    update_y = update_py + 50;
                    this.mPathPaint.setARGB(255, 0, 255, 0);
                    this.mCanvas.drawLine((float) update_px, (float) update_py, (float) update_x, (float) update_y, this.mPathPaint);
                    this.mCanvas.drawLine((float) update_px, (float) update_py, (float) (update_x + 50), (float) update_py, this.mPathPaint);
                    this.mCanvas.drawLine((float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_px) + 50), (float) update_py, (float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_x) + 50), (float) update_y, this.mPathPaint);
                    this.mCanvas.drawLine((float) update_x, (float) update_y, (float) (((((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50)) + update_x) + 50), (float) update_y, this.mPathPaint);
                }
                this.count_oblique1.put(Integer.valueOf(y / 50), Boolean.valueOf(true));
            }
            if (((this.mScreenWidth * this.mScreenHeight) - (this.mScreenWidth * y)) - (this.mScreenHeight * x) <= this.mScreenHeight * 50 && ((this.mScreenWidth * this.mScreenHeight) - (this.mScreenWidth * y)) - (this.mScreenHeight * x) >= this.mScreenWidth * -50) {
                Log.d(TAG, "detect_points222");
                update_py = (y / 50) * 50;
                update_px = (this.mScreenWidth - 50) - ((((y / 50) * 50) * this.mScreenWidth) / this.mScreenHeight);
                update_x = update_px - ((this.mScreenWidth * 50) / this.mScreenHeight);
                update_y = update_py + 50;
                this.mPathPaint.setARGB(255, 0, 255, 0);
                this.mCanvas.drawLine((float) update_px, (float) update_py, (float) update_x, (float) update_y, this.mPathPaint);
                this.mCanvas.drawLine((float) update_x, (float) update_y, (float) ((update_x + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_y, this.mPathPaint);
                this.mCanvas.drawLine((float) update_px, (float) update_py, (float) ((update_px + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_py, this.mPathPaint);
                if (y / 50 != 0) {
                    this.mCanvas.drawLine((float) ((update_px + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_py, (float) ((update_x + 50) + (((this.mScreenWidth - 50) * 50) / (this.mScreenHeight - 50))), (float) update_y, this.mPathPaint);
                }
                this.count_oblique2.put(Integer.valueOf(y / 50), Boolean.valueOf(true));
            }
        }

        public void draw(Canvas paramCanvas) {
            super.draw(paramCanvas);
            Log.d(TAG, "begin draw()");
            this.mCanvas = paramCanvas;
            int i = this.mPointers.size();
            for (int x = 0; x < mScreenWidth; x = x + 10) {
                for (int y = 0; y < mScreenHeight; y = y + 10) {
                    detect_points_draw(x, y, mPaint);
                }
            }
            for (int j = 0; j < i; j++) {
                PointerState localPointerState = (PointerState) this.mPointers.get(j);
                int k = localPointerState.mXs.size();
                int m = 0;
                while (m < k) {
                    float f1 = ((Float) localPointerState.mXs.get(m)).floatValue();
                    float f2 = ((Float) localPointerState.mYs.get(m)).floatValue();
                    if (!Float.isNaN(f1)) {
                        m++;
                        detect_points((int) f1, (int) f2);
                        m++;
                    } else {
                        return;
                    }
                }
            }
            boolean detect_flags = true;
            int tt = 0;
            Iterator it1 = this.count_oblique1.values().iterator();
            Iterator it2 = this.count_oblique2.values().iterator();
            Log.d(TAG, "=======AAAAAAAAAAAAAAAAAAdraw()! ======" + true);
            while (tt < this.mScreenHeight / 50) {
                if (!it1.hasNext() || !it2.hasNext()) {
                    detect_flags = false;
                    break;
                } else if (!((Boolean) it1.next()).booleanValue() || !((Boolean) it2.next()).booleanValue()) {
                    detect_flags = false;
                    break;
                } else {
                    tt++;
                }
            }
            Log.d(TAG, "draw->tt:" + tt + ",res:" + (this.mScreenHeight / 50) + ",detect_flags:" + detect_flags);
            if (detect_flags && !DiagonalTestActivity.this.isStart) {
                DiagonalTestActivity.this.isStart = true;
                Log.d(TAG, "draw->finish DiagonalTestActivity.this.finish();");
                SharedPreferences.Editor editor = getSharedPreferences("result", MODE_PRIVATE).edit();
                editor.putInt("Tp", 1);
                editor.commit();
                if (testmode == 0) {
                    startActivity(new Intent(DiagonalTestActivity.this, SensorTest.class));
                    finish();
                }
                if (testmode == 1) {
                    finish();
                }
            }
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            if (this.mPointers.size() > 0) {
                int historySize = event.getHistorySize();
                int i = 0;
                while (i < historySize + 1) {
                    float x = i < historySize ? event.getHistoricalX(i) : event.getX();
                    float y = i < historySize ? event.getHistoricalY(i) : event.getY();
                    ((PointerState) this.mPointers.get(this.mPointers.size() - 1)).mXs.add(Float.valueOf(x));
                    ((PointerState) this.mPointers.get(this.mPointers.size() - 1)).mYs.add(Float.valueOf(y));
                    i++;
                }
            }
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
        }

        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            this.mPointers.add(new PointerState());
        }
    }

    public static class PointerState {
        private final ArrayList<Float> mXs = new ArrayList();
        private final ArrayList<Float> mYs = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.diagonal_test);
        Root.getInstance().addActivity(this);
        Root.getInstance().setScreenBrightness(1,this);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        ((LinearLayout) findViewById(R.id.out_layout)).setSystemUiVisibility(16777216);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        this.mOverlay = (GestureOverlayView) findViewById(R.id.myGestures1);
        this.mOverlay.addView(new MyView(this, localDisplayMetrics.heightPixels, localDisplayMetrics.widthPixels));
        Log.d(TAG, "DiagonalTestActivity  onCreate!");
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED);
        }
        acquireWakeLock();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void acquireWakeLock() {
        if (mWakeLock == null) {
            final PowerManager pm =
                    (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "cameraTest");
            mWakeLock.setReferenceCounted(false);
        }
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    protected void onStop() {
        super.onStop();
        releaseWakeLock();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                StartUtil.StartNextActivity("Tp",1,testmode,DiagonalTestActivity.this,SensorTest.class);
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
}
