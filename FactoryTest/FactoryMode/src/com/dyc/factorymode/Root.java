package com.dyc.factorymode;

import android.app.Activity;
import android.app.Application;
import java.util.LinkedList;
import java.util.List;
import android.view.Window;
import android.view.WindowManager;


public class Root extends Application {
    private List<Activity> mActivitys = new LinkedList<Activity>();
    private static Root instance;

    static boolean isWifi = true;
    static boolean isBt = true;
    static boolean isGsensor = true;
    static boolean isMedia = true;
    static boolean isCamera = true;
    static boolean isSd = true;
    static boolean isLcd = false;
    static boolean isAudio = true;
    static boolean isGps = false;
    static boolean isVib = false;
    static boolean isProxSensor = false;
    static boolean isHallSensor = false;
    static boolean isKey = false;
    static boolean openBt;
    static boolean openWifi;
    static boolean openGps;
    static boolean haveLED = false;
    static boolean haveGSensor = false;
    static boolean haveProxSensor = false;
    static boolean haveHallSensor = false;
    static boolean haveGps = false;
    static boolean haveFrontCamera = false;
    static int SatelliteNum = 0;
    static String wifiMacAddress = null;
    static int mDefaultScreenMode = 0;
    static int mDefaultScreenBrightness = 178;
    static boolean isWifiConnect = false;
    //duanyecong add
    static boolean haveCameraFrontLed = false;
    static boolean isChinese = true;

    @Override
    public void onCreate() {
        super.onCreate();
        /*Thread.setDefaultUncaughtExceptionHandler(new java.lang.Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
            }
        });*/
        MyUpdateLanguage.updateLanguage(this,isChinese);
    }

    /**
     * 单例模式中获取唯一实例
     */
    public static Root getInstance() {
        if (null == instance) {
            instance = new Root();
        }
        return instance;
    }

    void addActivity(Activity activity) {
        if (mActivitys != null) {
            if (!mActivitys.contains(activity)) {
                mActivitys.add(activity);
            } else {
                mActivitys.add(activity);
            }
        }
    }

    void exit() {
        if (mActivitys != null && mActivitys.size() > 0) {
            for (Activity activity : mActivitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }
    void setScreenBrightness(float brightness , Activity activity){
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = brightness;
        localWindow.setAttributes(localLayoutParams);
    }
        
}
