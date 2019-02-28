package com.dyc.factorymode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity_Sagereal";
    public static final int ClearCanvas_ID = Menu.FIRST;
    MyView v = null;
    private int mZoom = 1;
    private StatusBarManager mstatusBarManager;
    private View mView;
    int mDefaultScreenMode;
    int mDefaultScreenBrightness;
    int mScreenMode = 0;
    int mScreenBrightness = 178;
    private int testmode;
    private static StringBuilder sb = new StringBuilder("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mstatusBarManager = (StatusBarManager) getSystemService("statusbar");
        mstatusBarManager.disable(StatusBarManager.DISABLE_MASK);
        mView = getWindow().getDecorView();
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);   
		//mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED);
        WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int id = mWifiManager.getConnectionInfo().getNetworkId();
        android.util.Log.d(TAG, "id = " + id);
        if (id >= 0) {
            Root.getInstance().isWifiConnect = true;
        } else {
            Root.getInstance().isWifiConnect = false;
        }
        try {
            Root.getInstance().mDefaultScreenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            Root.getInstance().mDefaultScreenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mScreenMode);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mScreenBrightness);
            android.util.Log.d(TAG, "screenMode =" + Root.getInstance().mDefaultScreenMode);
            android.util.Log.d(TAG, "screenBrightness =" + Root.getInstance().mDefaultScreenBrightness);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        v = new MyView(this);
        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.touch_view));
        setContentView(v);
        Toast.makeText(this, getResources().getString(R.string.ctp_pass_fail), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                StartUtil.StartNextActivity("Tp",1,testmode,MainActivity.this,SensorTest.class);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                StartUtil.StartNextActivity("Tp",2,testmode,MainActivity.this,SensorTest.class);
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

    @Override
    public void onResume() {
        super.onResume();
        mstatusBarManager.disable(StatusBarManager.DISABLE_MASK);
        mView.setSystemUiVisibility(View.STATUS_BAR_DISABLE_HOME 
            | View.STATUS_BAR_DISABLE_BACK 
            | View.STATUS_BAR_DISABLE_RECENT
            | View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);  
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final SharedPreferences preferences = this.getSharedPreferences(
                "touch_screen_settings", android.content.Context.MODE_PRIVATE);
        String file = preferences.getString("filename", "N");
        if (!file.equals("N")) {
            String[] cmd = {"/system/bin/sh", "-c",
                    "echo [ENTER_HAND_WRITING] >> " + file}; // file

			/*int ret;
			try {
				ret = execCommand(cmd);
				if (0 == ret) {
					Toast.makeText(this, "Start logging...", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(this, "Logging failed!", Toast.LENGTH_LONG)
							.show();
				}
			} catch (IOException e) {
				
			}*/

        }

    }

    @Override
    public void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mstatusBarManager.disable(StatusBarManager.DISABLE_NONE);

        final SharedPreferences preferences = this.getSharedPreferences(
                "touch_screen_settings", android.content.Context.MODE_PRIVATE);
        String file = preferences.getString("filename", "N");
        if (!file.equals("N")) {
            String[] cmd = {"/system/bin/sh", "-c",
                    "echo [LEAVE_HAND_WRITING] >> " + file}; // file

			/*int ret;
			try {
				ret = execCommand(cmd);
				if (0 == ret) {
					Toast.makeText(this, "Stop logging...", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(this, "Logging failed!", Toast.LENGTH_LONG)
							.show();
				}
			} catch (IOException e) {
				
			}*/

        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ClearCanvas_ID, 0, "Clean Table.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case ClearCanvas_ID:
                v.Clear();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(mi);
    }

    public static int execCommand(String[] command) throws IOException {

        // start the ls command running
        // String[] args = new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        // read the ls output		
        //sb = new StringBuilder("");
        sb.delete(0, sb.length());
        try {
            if (proc.waitFor() != 0) {
//				sb.append(ERROR);
                return -1;
            } else {
                String line;
                while ((line = bufferedreader.readLine()) != null) {
                    sb.append(line);
                    //sb.append('\n');
                }
                return 0;
            }
        } catch (InterruptedException e) {
//			sb.append(ERROR);
//			Xlog.i(TAG,"get:sb-- " + sb);
            return -1;
        }
    }

    public class PT {
        public Float x;
        public Float y;

        public PT(Float x, Float y) {
            this.x = x;
            this.y = y;
        }
    }

    ;

    public class MyView extends View {
        //private final Paint mTextPaint;
        //private final Paint mTextBackgroundPaint;
        //private final Paint mTextLevelPaint;
        private final Paint mPaint;
        private final Paint mTargetPaint;
        private final FontMetricsInt mTextMetrics = new FontMetricsInt();
        public ArrayList<ArrayList<PT>> mLines = new ArrayList<ArrayList<PT>>();
        ArrayList<PT> curLine;
        public ArrayList<VelocityTracker> mVelocityList = new ArrayList<VelocityTracker>();
        private int mHeaderBottom;
        private boolean mCurDown;
        private int mCurX;
        private int mCurY;
        private float mCurPressure;
        private int mCurWidth;
        private VelocityTracker mVelocity;

        public MyView(Context c) {
            super(c);

            DisplayMetrics dm = new DisplayMetrics();
            dm = MainActivity.this.getApplicationContext()
                    .getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            if ((480 == screenWidth && 800 == screenHeight)
                    || (800 == screenWidth && 480 == screenHeight)) {
                mZoom = 2;
            }

			/*mTextPaint = new Paint();
			mTextPaint.setAntiAlias(true);
			mTextPaint.setTextSize(10 * mZoom);
			mTextPaint.setARGB(255, 0, 0, 0);
			mTextBackgroundPaint = new Paint();
			mTextBackgroundPaint.setAntiAlias(false);
			mTextBackgroundPaint.setARGB(128, 0, 0, 255);
			mTextLevelPaint = new Paint();
			mTextLevelPaint.setAntiAlias(false);
			mTextLevelPaint.setARGB(192, 255, 0, 0);*/
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setARGB(255, 255, 255, 255);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(2);
            mTargetPaint = new Paint();
            mTargetPaint.setAntiAlias(false);
            mTargetPaint.setARGB(192, 255, 0, 0);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1);
            mTargetPaint.setStrokeWidth(3);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            //mTextPaint.getFontMetricsInt(mTextMetrics);
            mHeaderBottom = -mTextMetrics.ascent + mTextMetrics.descent + 2;
        }

        @Override
        protected void onDraw(Canvas canvas) {
			/*int w = getWidth() / 5;
			int base = -mTextMetrics.ascent + 1;
			int bottom = mHeaderBottom;
			canvas.drawRect(0, 0, w - 1, bottom, mTextBackgroundPaint);
			canvas.drawText("X: " + mCurX, 1, base, mTextPaint);

			canvas.drawRect(w, 0, (w * 2) - 1, bottom, mTextBackgroundPaint);
			canvas.drawText("Y: " + mCurY, 1 + w, base, mTextPaint);

			canvas.drawRect(w * 2, 0, (w * 3) - 1, bottom,mTextBackgroundPaint);
			canvas.drawRect(w * 2, 0, (w * 2) + (mCurPressure * w) - 1, bottom,mTextLevelPaint);
			canvas.drawText("Pres: " + mCurPressure, 1 + w * 2, base,mTextPaint);

			canvas.drawRect(w * 3, 0, (w * 4) - 1, bottom,mTextBackgroundPaint);
			int Xvelocity = mVelocity == null ? 0 : (int) (Math.abs(mVelocity.getXVelocity()) * 1000);
			canvas.drawText("XVel: " + Xvelocity, 1 + w * 3, base, mTextPaint);

			nvas.drawRect(w * 4, 0, getWidth(), bottom, mTextBackgroundPaint);
			int Yvelocity = mVelocity == null ? 0 : (int) (Math.abs(mVelocity.getYVelocity()) * 1000);
			canvas.drawText("YVel: " + Yvelocity, 1 + w * 4, base, mTextPaint);*/

            int lineSz = mLines.size();
            int k = 0;
            for (k = 0; k < lineSz; k++) {
                ArrayList<PT> m = mLines.get(k);

                float lastX = 0, lastY = 0;
                mPaint.setARGB(255, 0, 255, 255);
                int sz = m.size();
                int i = 0;
                for (i = 0; i < sz; i++) {
                    PT n = m.get(i);
                    if (i > 0) {
                        canvas.drawLine(lastX, lastY, n.x, n.y, mTargetPaint);
                        canvas.drawPoint(lastX, lastY, mPaint);
                    }

                    lastX = n.x;
                    lastY = n.y;
                }

                VelocityTracker v = mVelocityList.get(k);
                if (v != null) {
                    mPaint.setARGB(255, 255, 0, 0);
                    float xVel = v.getXVelocity() * (1000 / 60);
                    float yVel = v.getYVelocity() * (1000 / 60);
                    canvas.drawLine(lastX, lastY, lastX + xVel, lastY + yVel,
                            mPaint);
                } else {
                    canvas.drawPoint(lastX, lastY, mPaint);
                }

                if (mCurDown) {
                    canvas.drawLine(0, (int) mCurY, getWidth(), (int) mCurY,
                            mTargetPaint);
                    canvas.drawLine((int) mCurX, 0, (int) mCurX, getHeight(),
                            mTargetPaint);
                    int pressureLevel = (int) (mCurPressure * 255);
                    mPaint
                            .setARGB(255, pressureLevel, 128,
                                    255 - pressureLevel);
                    canvas.drawPoint(mCurX, mCurY, mPaint);
                    canvas.drawCircle(mCurX, mCurY, mCurWidth, mPaint);
                }

            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {

                mVelocity = VelocityTracker.obtain();
                mVelocityList.add(mVelocity);

                curLine = new ArrayList<PT>();
                mLines.add(curLine);
            }
            mVelocity.addMovement(event);
            mVelocity.computeCurrentVelocity(1);
            final int N = event.getHistorySize();
            for (int i = 0; i < N; i++) {
                curLine.add(new PT(event.getHistoricalX(i), event
                        .getHistoricalY(i)));
            }
            curLine.add(new PT(event.getX(), event.getY()));
            mCurDown = action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_MOVE;
            mCurX = (int) event.getX();
            mCurY = (int) event.getY();
            mCurPressure = event.getPressure();
            mCurWidth = (int) (event.getSize() * (getWidth() / 3));

            invalidate();
            return true;
        }

        public void Clear() {
            for (ArrayList<PT> m : mLines) {
                m.clear();
            }
            mLines.clear();
            mVelocityList.clear();
            invalidate();
        }

    }
}
