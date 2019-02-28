package com.dyc.factorymode;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.media.AudioManager;
import android.content.Context;
import android.media.MediaPlayer;
import java.io.FileDescriptor;
import java.io.IOException;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.ComponentName;
import android.media.MediaPlayer.OnErrorListener;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.PhoneConstants;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.ServiceState;
import java.io.File;
import android.os.Environment;
import android.os.storage.IMountService;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.StorageManager;
import android.os.SystemProperties;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.DiskInfo;
import android.widget.LinearLayout;
import android.text.TextUtils;
//add to listent sim1&sim2 dbm/asu 2019.02.22 begin
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
//add to listent sim1&sim2 dbm/asu 2019.02.22 end

public class AutoTest extends Activity implements OnClickListener {

    private static final String TAG = "AutoTest_Sagereal";
    private Button nextButton;
    private Button mfail;
    private Button callButton;
    private Button reBut;
    private Button reWifi;
    private Resources res;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor afd;
    private WifiManager mWifiManager = null;
    private TextView gpsView;
    private LinearLayout gpsitem;
    private List<ScanResult> mWifiList;
    private TextView sim1View;
    private TextView sim2View;
    private int SimState1 = 0;
    private int SimState2 = 0;
    private String sim2Str = "";
    private String sim1Str = "";
    private int sim1Status = 0;
    private int sim2Status = 0;
    private String status_nosim = "";
    private String status_unknow = "";
    private String status_netlock = "";
    private String status_pinlock = "";
    private String status_puklock = "";
    private String status_good = "";
    private String sim_date_test = "";
    private String sim_asu = "";
    private String sim_dbm = "";
    private String app_name_wifi = "";
    private TelephonyManager Tel;//TelephonyManager类的对象
    //private MyPhoneStateListener MyListener;
    private int state;
    private TextView wifiView;
    private TextView wifidate;
    private TextView sim1date;
    private TextView sim2date;
    private TextView testTime;
    private TextView tv_timer;
    private TextView wifitv1;
    private TextView wifitv2;
    private TextView wifitv3;
    private TextView Sd1View;
    private TextView Sd2View;
    private TextView Sd1date;
    private TextView Sd2date;
    private boolean isMplatform;
    private ImageView wifiim1;
    private ImageView wifiim2;
    private ImageView wifiim3;
    private boolean isStopCount = false;
    private Handler mHandler = new Handler();
    private long timer = 0;
    private String timeStr = "";
    private final static int GPS_SUCCESS = 7;
    private final static int GPS_FAULT = 8;
    private final static int WIFI_SUCCESS = 1;
    private final static int WIFI_FAULT = 2;
    private final static int WIFI_TESTING = 3;
    private boolean isUnregister;
    private boolean isSdTest = false, isGpsTest = false, isWifilistTest = false, isSimview = false , isSimdate = false;
    private int testmode;
    private String mFilePath = "cn_1k.MP3";
    private WifiManager.ActionListener mConnectListener;
    private LocationManager locationManager;
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
    public final static String AUDIOPROFILE_SERVICE = "";
    private int mDefaultVolume = -1;
    //add to listent sim1&sim2 dbm/asu 2019.02.22 begin
    private MyPhoneStateListener1 MyListener1;
    private MyPhoneStateListener2 MyListener2;
    private  int subid1,subid2;
    private SubscriptionManager mSubscriptionManager;
    private SubscriptionInfo sub0,sub1;
    //add to listent sim1&sim2 dbm/asu 2019.02.22 end
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        Root.getInstance().addActivity(this);
        setContentView(R.layout.gps_wifi_test);
        android.util.Log.d(TAG, "onCreate success");
        res = getResources();
        initview();
        //add to listent sim1&sim2 dbm/asu 2019.02.22 begin
        //MyListener = new MyPhoneStateListener(); //Return the handle to a system-level service by name.通过名字获得一个系统级服务
        //Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//信号强度dbm/asu监听
        //Tel.listen(MyListener, PhoneStateListener.LISTEN_SERVICE_STATE);//sim卡服务状态监听
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        sub0 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0);
        if(sub0 != null) {
            subid1 = sub0.getSubscriptionId();
            MyListener1 = new MyPhoneStateListener1(subid1);
            Tel.listen(MyListener1, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//卡1信号强度dbm/asu监听
        }
        sub1 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1);
        if(sub1 != null) {
            subid2 = sub1.getSubscriptionId();
            MyListener2 = new MyPhoneStateListener2(subid2);
            Tel.listen(MyListener2, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//卡2信号强度dbm/as监听
        }
        //add to listent sim1&sim2 dbm/asu 2019.02.22 end
        countTimer();//搜星计时
        android.util.Log.d(TAG, "FeatureOption.MTK_2SDCARD_SWAP===" + FeatureOption.MTK_2SDCARD_SWAP);
        sdCardTest();//检测内存信息
        simTest();//检测SIM信息
        isMplatform = SystemProperties.get("ro.build.version.sdk").equals("23");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // if wifi default close,and close wifi
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        isUnregister = false;
        isSccueed = false;
        //Redmine88913 zhousankui modified for wifi cyclic serach 2017-03-16 Begin
        IntentFilter wifiFilter2 = new IntentFilter();
        wifiFilter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, wifiFilter2);
        //Redmine88913 zhousankui modified for wifi cyclic serach 2017-03-16 End
        new Thread(WifiTest).start();
        if (FeatureOption.MTK_GPS_SUPPORT) {
            showGps();
            new Thread(new MyThread()).start();
        } else {
            isGpsTest = true;
            gpsitem.setVisibility(View.GONE);
        }
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        reWifi.performClick();
        mConnectListener = new WifiManager.ActionListener() {
            public void onSuccess() {
                android.util.Log.d(TAG, "mConnectListener onSuccess");
                //Redmine88913 zhousankui modified for wifi cyclic serach 2017-03-16 Begin

				/*
				if(isUnregister){
					isUnregister = false;
					try {
						unregisterReceiver(scanResultsReceiver);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				isSccueed = true;
				*/
                //Redmine88913 zhousankui modified for wifi cyclic serach 2017-03-16 End
            }
            public void onFailure(int reason) {
                android.util.Log.d(TAG, "onFailure reason = " + reason);
            }
        };
        testFinish();
    }
        /**
     * 卫星状态监听器
     */
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            GpsStatus status = locationManager.getGpsStatus(null); // 取当前状态
            updateGpsStatus(event, status);
        }
    };

    private void updateGpsStatus(int event, GpsStatus status) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            int count = 0;
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);
                count++;
            }
            Root.getInstance().SatelliteNum = numSatelliteList.size();
            System.out.println("搜索卫星个数：" + Root.getInstance().SatelliteNum);
            if (numSatelliteList.size() >= 3) {
                isStopCount = true;
                tv_timer.setTextColor(Color.GREEN);
                testTime.setTextColor(Color.GREEN);
                gpsView.setText(res.getString(R.string.gps_success));
                gpsView.setTextColor(Color.GREEN);
                isGpsTest = true;
                testFinish();
                Root.getInstance().isGps = true;
            }
        }
    }
    
    @Override
        public void onClick(View v) {
        if (!ButtonUtils.isFastDoubleClick(v.getId())) {
            switch (v.getId()) {
                case R.id.gps_success_btn:
                    if (Root.getInstance().haveGps && !Root.getInstance().openGps) {
                        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
                        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.NETWORK_PROVIDER, false);
                    }
                    if (!Root.getInstance().openWifi && mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(false);
                    }
                    int id = mWifiManager.getConnectionInfo().getNetworkId();
                    if ((id >= 0) && (!Root.getInstance().isWifiConnect)) {
                        mWifiManager.removeNetwork(id);
                    }
                    StartUtil.StartNextActivity("Gps",1,testmode,AutoTest.this,KeyTestActivity.class);
                    break;
                case R.id.gps_fail_btn:
                    StartUtil.StartNextActivity("Gps",2,testmode,AutoTest.this,KeyTestActivity.class);
                    break;
                case R.id.reGps:  
                    boolean isOpenGps = locationManager.isProviderEnabled("gps");
                    if (!isOpenGps) {
                        android.util.Log.d(TAG, "isOpenGps = " + isOpenGps);
                        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
                    }
                    gpsView.setText(R.string.app_name_gps);
                    gpsView.setTextColor(Color.RED);
                    locationManager.requestLocationUpdates("gps", 6000, 1, locationListener);
                    locationManager.addGpsStatusListener(statusListener);
                    new Thread(new MyThread()).start();
                    break;
                case R.id.reWifi: 
                    wifiView.setText(app_name_wifi);
                    new Thread(WifiTest).start();
                    break;
                default:
                    break;
              }
            }
         }
        private void initview(){
                sim1View = (TextView) findViewById(R.id.sim1View);
                sim2View = (TextView) findViewById(R.id.sim2View);
                sim1date = (TextView) findViewById(R.id.sim1date);
                gpsitem = (LinearLayout) findViewById(R.id.gps_item);
                sim2date = (TextView) findViewById(R.id.sim2date);
                sim1Str = res.getString(R.string.status_sim1);
                sim2Str = res.getString(R.string.status_sim2);
                status_nosim = res.getString(R.string.status_nosim);
                status_unknow = res.getString(R.string.status_unknow);
                status_netlock = res.getString(R.string.status_netlock);
                status_pinlock = res.getString(R.string.status_pinlock);
                status_puklock = res.getString(R.string.status_puklock);
                status_good = res.getString(R.string.status_good);
                sim_date_test = res.getString(R.string.sim_date_test);
                sim_asu = res.getString(R.string.sim_asu);
                sim_dbm = res.getString(R.string.sim_dbm);
                app_name_wifi = res.getString(R.string.app_name_wifi);
                Sd1View = (TextView) findViewById(R.id.Sd1View);
                Sd2View = (TextView) findViewById(R.id.Sd2View);
                Sd1date = (TextView) findViewById(R.id.Sd1date);
                Sd2date = (TextView) findViewById(R.id.Sd2date);
                wifiView = (TextView) findViewById(R.id.wifiview);
                wifidate = (TextView) findViewById(R.id.wifidate);
                gpsView = (TextView) findViewById(R.id.GpsView);
                testTime = (TextView) findViewById(R.id.testTime);
                tv_timer = (TextView) findViewById(R.id.GpsTime);
                wifitv1 = (TextView) findViewById(R.id.wifi_tv1);
                wifitv2 = (TextView) findViewById(R.id.wifi_tv2);
                wifitv3 = (TextView) findViewById(R.id.wifi_tv3);
                wifiim1 = (ImageView) findViewById(R.id.wifi_im1);
                wifiim2 = (ImageView) findViewById(R.id.wifi_im2);
                wifiim3 = (ImageView) findViewById(R.id.wifi_im3);
                nextButton = (Button) findViewById(R.id.gps_success_btn);
                reWifi = (Button) findViewById(R.id.reWifi);
                mfail = (Button) findViewById(R.id.gps_fail_btn);
                reBut = (Button) findViewById(R.id.reGps);
                nextButton.setEnabled(false);
                mfail.setOnClickListener(this);
                nextButton.setOnClickListener(this);
                reBut.setOnClickListener(this);
                reWifi.setOnClickListener(this);

        }

    //对SIM 卡检测............
    private void simTest() {

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm.getPhoneCount() == 2) {
            // SimState1 = TelephonyManager.getDefault().getSimState(0);
            //SimState2 = TelephonyManager.getDefault().getSimState(1);
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);
            sim2Status = tm.getSimState(PhoneConstants.SIM_ID_2);
            android.util.Log.d(TAG, "sim1Status===" + sim1Status);
            android.util.Log.d(TAG, "sim2Status===" + sim2Status);
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1View.setText(sim1Str + status_unknow);
                    //sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1View.setText(sim1Str + status_nosim);
                    //sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim1View.setText(sim1Str + status_pinlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim1View.setText(sim1Str + status_puklock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim1View.setText(sim1Str + status_netlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimview = true;
                    testFinish();
                    sim1View.setText(sim1Str + status_good);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
            switch (sim2Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim2View.setText(sim2Str + status_nosim);
                    //sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim2View.setText(sim2Str + status_unknow);
                    //sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim2View.setText(sim2Str + status_netlock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim2View.setText(sim2Str + status_pinlock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim2View.setText(sim2Str + status_puklock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimview = true;
                    testFinish();
                    sim2View.setText(sim2Str + status_good);
                    sim2View.setTextColor(Color.GREEN);
                    break;
            }


        } else if (tm.getPhoneCount() == 1) {
            sim2View.setVisibility(View.GONE);
            sim2date.setVisibility(View.GONE);
            //  SimState1 = TelephonyManager.getDefault().getSimState(0);//获取SIM卡具体状态
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);//获取SIM卡是否插入
            android.util.Log.d(TAG, "sim1Status===" + sim1Status);
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1View.setText(sim1Str + status_nosim);
                    //sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1View.setText(sim1Str + status_unknow);
                    //sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim1View.setText(sim1Str + status_netlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim1View.setText(sim1Str + status_pinlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim1View.setText(sim1Str + status_puklock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimview = true;
                    testFinish();
                    sim1View.setText(sim1Str + status_good);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
        }
    }
    //add to listent sim1&sim2 dbm/asu 2019.02.22 begin
    /*
      SIM1信号监听
     */
    private class MyPhoneStateListener1 extends PhoneStateListener {
        public MyPhoneStateListener1(int subId) {
            super();
            ReflectUtil.setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            state = serviceState.getState();
            android.util.Log.e(TAG, "state==" + state);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int signalDbm = signalStrength.getDbm();
            int signalAsu = signalStrength.getAsuLevel();
            android.util.Log.d("zhang"," 111 signalDbm="+signalDbm+"   signalAsu="+signalAsu);
            if ((ServiceState.STATE_OUT_OF_SERVICE == state) || (ServiceState.STATE_POWER_OFF == state)) {
                signalDbm = 0;
                signalAsu = 0;
            }
            if (-1 == signalDbm) {
                signalDbm = 0;
            }
            if (-1 == signalAsu) {
                signalAsu = 0;
            }
            String asu = String.valueOf(signalAsu);
            String dBm = String.valueOf(signalDbm);
            if (sim1Status != 1) {
                isSimdate = true;
                sim1date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim1date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim1date.setTextColor(Color.RED);
                } else {
                    sim1date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
            if (sim1Status != 1) {
                isSimdate = true;
                sim1date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim1date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim1date.setTextColor(Color.RED);
                } else {
                    sim1date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
        }

    }
    /*
      SIM2信号监听
     */
    private class MyPhoneStateListener2 extends PhoneStateListener {
        public MyPhoneStateListener2(int subId) {
            super();
            ReflectUtil.setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            state = serviceState.getState();
            android.util.Log.e(TAG, "state==" + state);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int signalDbm = signalStrength.getDbm();
            int signalAsu = signalStrength.getAsuLevel();
            android.util.Log.d("zhang"," 2222 signalDbm="+signalDbm+"   signalAsu="+signalAsu);
            if ((ServiceState.STATE_OUT_OF_SERVICE == state) || (ServiceState.STATE_POWER_OFF == state)) {
                signalDbm = 0;
                signalAsu = 0;
            }
            if (-1 == signalDbm) {
                signalDbm = 0;
            }
            if (-1 == signalAsu) {
                signalAsu = 0;
            }
            String asu = String.valueOf(signalAsu);
            String dBm = String.valueOf(signalDbm);
            if (sim2Status != 1) {
                isSimdate = true;
                sim2date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim2date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim2date.setTextColor(Color.RED);
                } else {
                    sim2date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
            if (sim2Status != 1) {
                isSimdate = true;
                sim2date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim2date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim2date.setTextColor(Color.RED);
                } else {
                    sim2date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
        }

    }
    /*
      反射通过 SIM卡的subId来获取信号强度
     */
    public static class ReflectUtil {

        public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
            Field field = getAccessibleField(obj, fieldName);

            if (field == null) {
                throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
            }

            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public static Field getAccessibleField(final Object obj, final String fieldName) {
            if (obj == null) {
                throw new IllegalArgumentException("object can't be null");
            }

            if (fieldName == null || fieldName.length() <= 0) {
                throw new IllegalArgumentException("fieldName can't be blank");
            }

            for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
                try {
                    Field field = superClass.getDeclaredField(fieldName);
                    makeAccessible(field);
                    return field;
                } catch (NoSuchFieldException e) {
                    continue;
                }
            }
            return null;
        }

        public static void makeAccessible(Field field) {
            if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
                field.setAccessible(true);
            }
        }
    }
    
    /*private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            state = serviceState.getState();
            android.util.Log.e(TAG, "state==" + state);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int signalDbm = signalStrength.getDbm();
            int signalAsu = signalStrength.getAsuLevel();
            if ((ServiceState.STATE_OUT_OF_SERVICE == state) || (ServiceState.STATE_POWER_OFF == state)) {
                signalDbm = 0;
                signalAsu = 0;
            }
            if (-1 == signalDbm) {
                signalDbm = 0;
            }
            if (-1 == signalAsu) {
                signalAsu = 0;
            }
            String asu = String.valueOf(signalAsu);
            String dBm = String.valueOf(signalDbm);
            if (sim1Status != 1 && sim2Status != 1) {
                isSimdate = true;
                sim1date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                sim2date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim1date.setTextColor(Color.WHITE);
                    sim2date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim1date.setTextColor(Color.RED);
                    sim2date.setTextColor(Color.RED);
                } else {
                    sim1date.setTextColor(Color.GREEN);
                    sim2date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
            if (sim1Status != 1) {
                isSimdate = true;
                sim1date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim1date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim1date.setTextColor(Color.RED);
                } else {
                    sim1date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
            if (sim2Status != 1) {
                isSimdate = true;
                sim2date.setText(sim_date_test + dBm + sim_dbm + asu + sim_asu);
                if (signalAsu == 0 && signalDbm == 0) {
                    sim2date.setTextColor(Color.WHITE);
                } else if (signalDbm <= -115) {
                    isSimdate = false;
                    sim2date.setTextColor(Color.RED);
                } else {
                    sim2date.setTextColor(Color.GREEN);
                }
                testFinish();
            }
        }

    }*/
    //add to listent sim1&sim2 dbm/asu 2019.02.22 end

    //对SIM 卡检测.............
    //对内存信息检测...............
    private void sdCardTest() {
        boolean isHaveInternalSD = false;
        File internalSDCard;
        android.util.Log.d(TAG, "FeatureOption.MTK_2SDCARD_SWAP====" + FeatureOption.MTK_2SDCARD_SWAP);
        if (FeatureOption.MTK_2SDCARD_SWAP) {
            internalSDCard = new File("/storage/sdcard");
            String file = internalSDCard.toString();
            try {
                IMountService ms = getMs();
                isHaveInternalSD = ms.getVolumeState(file).equals(Environment.MEDIA_MOUNTED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            internalSDCard = Environment.getExternalStorageDirectory();
            String file = internalSDCard.toString();
            StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
            try {
                if (storageManager != null) {
                    isHaveInternalSD = storageManager.getVolumeState(file).equals(
                            Environment.MEDIA_MOUNTED);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        if (isHaveInternalSD) {
            long totalSpace1 = internalSDCard.getTotalSpace();
            android.util.Log.d(TAG, "totalSpace1!!!!!!" + totalSpace1);
            isSdTest = true;
            testFinish();
            Sd1View.setText(res.getString(R.string.sd1_have_view_test));
            Sd1View.setTextColor(Color.GREEN);
            Sd1date.setText(getResources().getString(R.string.total_capacity) + getSpaceStr(totalSpace1));
            Sd1date.setTextColor(Color.GREEN);
        } else {
            isSdTest = false;
            Sd1View.setTextColor(Color.RED);
            Sd1date.setTextColor(Color.RED);
        }
        boolean isHaveExternalSD = false;//判断是否有额外的T卡
        File externalSDCard = null;
        String sdcardPath = getExternalSdStateAndPath();
        if (!TextUtils.isEmpty(sdcardPath)) {
            isHaveExternalSD = true;
            externalSDCard = new File(sdcardPath);
        }
        if (isHaveExternalSD) {
            long totalSpace2 = externalSDCard.getTotalSpace();//获得总内存
            android.util.Log.d(TAG, "totalSpace2!!!!!!" + totalSpace2);
            Sd2View.setText(res.getString(R.string.sd2_have_view_test));
            Sd2View.setTextColor(Color.GREEN);
            Sd2date.setText(getResources().getString(R.string.total_capacity) + getSpaceStr(totalSpace2));
            Sd2date.setTextColor(Color.GREEN);
        } else {
            isSdTest = false;
            Sd2View.setTextColor(Color.RED);
            Sd2date.setTextColor(Color.RED);
        }
    }

    private String getSpaceStr(long space) {
        StringBuilder builder = new StringBuilder();
        long gb = space / 1024 / 1024 / 1024;
        long mb = space / 1024 / 1024;
        long kb = space / 1024;
        if (gb > 0) {
            String gbStr = (float) space / 1024 / 1024 / 1024 + "";
            builder.append(gbStr.substring(0, gbStr.length() >= 6 ? 6 : gbStr.length()) + "GB");
        } else {
            if (mb > 0) {
                String mbStr = (float) space / 1024 / 1024 + "";
                builder.append(mbStr.substring(0, mbStr.length() >= 6 ? 6 : mbStr.length()) + "MB");
            } else {
                if (kb > 0) {
                    String kbStr = (float) space / 1024 + "";
                    builder.append(kbStr.substring(0, kbStr.length() >= 6 ? 6 : kbStr.length()) + "KB");
                } else {
                    builder.append(space + "B");
                }
            }
        }
        return builder.toString();
    }

    private String getExternalSdStateAndPath() {
        StorageManager mStorageManager = StorageManager.from(this);
        final List<VolumeInfo> volumeInfoList = mStorageManager.getVolumes();
        VolumeInfo sdcardVolume = null;
        for (VolumeInfo mVolumeInfo : volumeInfoList) {
            if (mVolumeInfo.getDisk() != null && mVolumeInfo.getDisk().isSd())
                sdcardVolume = mVolumeInfo;
            if (sdcardVolume != null) {
                String sdcardPath = sdcardVolume.getPath() == null ? null : sdcardVolume.getPath().toString();
                String sdcardState = sdcardVolume.getEnvironmentForState(sdcardVolume.getState());
                if (sdcardState.equals(Environment.MEDIA_MOUNTED) && sdcardPath != null) {
                    return sdcardPath;
                }
            }
        }
        return "";
    }

    private IMountService getMs() {
        // TODO Auto-generated method stub
        IBinder service = ServiceManager.getService("mount");
        if (service != null) {
            return IMountService.Stub.asInterface(service);
        } else {
            android.util.Log.d(TAG, "Can't get mount service");
        }
          return null;
    }
    //对内存信息检测...............

    public class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(25000);
                String str = (String) gpsView.getText();
                System.out.println("str = " + str);
                if (str != res.getString(R.string.gps_success)) {
                    Message message = new Message();
                    message.what = GPS_FAULT;
                    handler.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            switch (msg.what) {
                case WIFI_SUCCESS:
                    String name = bun.getString("name");
                    wifiView.setText(res.getString(R.string.wifi_success) + name);
                    wifiView.setTextColor(Color.GREEN);
                    wifidate.setTextColor(Color.GREEN);
                    isWifilistTest = true;
                    testFinish();
                    Root.getInstance().isWifi = true;
                    break;
                case WIFI_TESTING:
                    if (msg.obj instanceof String) {
                        String wifiName = (String) msg.obj;
                        wifiView.setText(app_name_wifi);
                        //  wifiView.setTextColor(Color.RED);
                        wifidate.setText("WIFI:" + wifiName);
                    }
                    break;
                case WIFI_FAULT:
                    wifiView.setText(res.getString(R.string.wifi_fault));
                    //wifiView.setTextColor(Color.RED);
                    Root.getInstance().isWifi = false;
                    break;
                case GPS_SUCCESS:
                    isGpsTest = true;
                    testFinish();
                    gpsView.setText(res.getString(R.string.gps_success));
                    gpsView.setTextColor(Color.GREEN);
                    Root.getInstance().isGps = true;
                    break;
                case GPS_FAULT:
                    isGpsTest = false;
                    testFinish();
                    gpsView.setText(res.getString(R.string.gps_fault));
                    gpsView.setTextColor(Color.RED);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Runnable TimerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isStopCount) {
                timer += 1000;
                timeStr = getFormatHMS(timer);
                tv_timer.setText(timeStr);
            }
            countTimer();
        }
    };

    private void countTimer() {
        mHandler.postDelayed(TimerRunnable, 1000);
    }

    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void showGps() {
        if (Root.getInstance().SatelliteNum > 2) {
            isStopCount = true;
            tv_timer.setTextColor(Color.GREEN);
            testTime.setTextColor(Color.GREEN);
            gpsView.setText(res.getString(R.string.gps_success));
            gpsView.setTextColor(Color.GREEN);
            isGpsTest = true;
            testFinish();
            Root.getInstance().isGps = true;
        } else {
            isStopCount = false;
            boolean isOpenGps = locationManager.isProviderEnabled("gps");
            if (!isOpenGps) {
                android.util.Log.d(TAG, "showGps isOpenGps = " + isOpenGps);
                Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
            }
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates("gps", 6000, 1, locationListener);
            locationManager.addGpsStatusListener(statusListener); // 注册状态信息回调
        }
    }

    Runnable WifiTest = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            mWifiManager.setWifiEnabled(true);
            if (!isUnregister) {
                isUnregister = true;
                // 监听wifi，搜索装置
                IntentFilter wifiFilter = new IntentFilter();
                wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(scanResultsReceiver, wifiFilter);
            }
            isConnectedWifi();
        }
    };

    private void isConnectedWifi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            isSccueed = true;
            String name = mWifiManager.getConnectionInfo().getSSID();
            Message msg = Message.obtain(handler, WIFI_SUCCESS);
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } else {
            isSccueed = false;
            mWifiManager.startScan();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    State state = networkInfo.getState();
                    boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
                    if (isConnected) {
                        if (isUnregister) {
                            isUnregister = false;
                            try {
                                context.unregisterReceiver(scanResultsReceiver);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                        context.unregisterReceiver(receiver);
                        isSccueed = true;
                        String name = mWifiManager.getConnectionInfo().getSSID();
                        System.out.println("name = " + name);
                        Message msg = Message.obtain(handler, WIFI_SUCCESS);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        android.util.Log.d(TAG, "unregisterReceiver isConnected = false");
                    }
                }
            }
        }

        ;
    };

    BroadcastReceiver scanResultsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                Root.getInstance().wifiMacAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
                String str = "NVRAM WARNING";
                mWifiList = mWifiManager.getScanResults();// 搜索到的设备列表
                android.util.Log.d(TAG, "mWifiList.size() = " + mWifiList.size());
                if (mWifiList == null || mWifiList.size() == 0) {
                    isWifilistTest = false;
                    return;
                } else {
                    wifiView.setTextColor(Color.GREEN);
                    isWifilistTest = true;
                    testFinish();
                }
                for (int i = 0; i < mWifiList.size(); i++) {
                    ScanResult result = mWifiList.get(i);
                    String name = result.SSID;
                    android.util.Log.d(TAG, "name === " + name);
                    int type = getSecurity(result);
                    String level = String.valueOf(Math.abs(result.level));
                    if (Math.abs(result.level) < 50) {
                        wifitv1.setText(name);
                        wifiim1.setImageResource(R.drawable.ic_wifi_signal_4_dark);
                    } else if (Math.abs(result.level) < 70) {
                        wifitv2.setText(name);
                        wifiim2.setImageResource(R.drawable.ic_wifi_signal_3_dark);
                    } else if (Math.abs(result.level) < 90) {
                        wifitv3.setText(name);
                        wifiim3.setImageResource(R.drawable.ic_wifi_signal_2_dark);
                    } else {
                        wifitv3.setText(name);
                        wifiim3.setImageResource(R.drawable.ic_wifi_signal_1_dark);
                    }
                    android.util.Log.d(TAG, "level === " + level);

                    if (name != null && type == 0 && !name.contains(str) && isUnregister) {
                        mWifiList.remove(i);
                        ConnectWifi(result);
                        if (isUnregister) {

                            isUnregister = false;
                            try {
                                context.unregisterReceiver(scanResultsReceiver);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            new Thread(runnable).start();
                        }
                        android.util.Log.d(TAG, "start connect mWifiList.size() = " + mWifiList.size() + "  name = " + name);
                    }
                }
            }
        }
    };

    private boolean isSccueed;
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            String str = "NVRAM WARNING";
            if (mWifiList != null && mWifiList.size() > 0) {
                for (int i = 0; i < mWifiList.size(); i++) {
                    try {
                        Thread.sleep(4000);
                    } catch (Exception e) {
                    }
                    if (isSccueed) {
                        return;
                    }
                    ScanResult result = mWifiList.get(i);
                    if (result == null) {
                        continue;
                    }
                    String name = result.SSID;
                    int type = getSecurity(result);

                    if (mWifiList != null && mWifiList.size() > i) {
                        //mWifiList.remove(i);
                    }

                    if (name != null && type == 0 && !name.contains(str) && !isSccueed) {
                        ConnectWifi(result);
                        android.util.Log.d(TAG, "decrease size() = " + mWifiList.size() + "  name = " + name);
                        Message.obtain(handler, WIFI_TESTING, name).sendToTarget();
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                        }
                    } else {
                        android.util.Log.d(TAG, "KeyTest->failure");
                    }
                }
            }
            if (!isSccueed) {
                Message.obtain(handler, WIFI_FAULT).sendToTarget();
            }
            android.util.Log.d(TAG, "KeyTest->wifi test end");
        }
    };

    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return 1;
        } else if (result.capabilities.contains("PSK")) {
            return 2;
        } else if (result.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    private void ConnectWifi(ScanResult result) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + result.SSID + "\"";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiConfiguration tempConfig = this.IsExsits(result.SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
            int networkId = mWifiManager.addNetwork(config);
            if (networkId != -1) {
                System.out.println("networkId = " + networkId);
                mWifiManager.connect(networkId, mConnectListener);
            }
        }
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 注册GPS监听器
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            gpsView.setText(res.getString(R.string.gps_success));
            gpsView.setTextColor(Color.GREEN);
            isGpsTest = true;
            testFinish();
            Root.getInstance().isGps = true;
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    };

    private void testFinish() {
        if (isSdTest && isGpsTest && isWifilistTest && isSimview && isSimdate) {
            nextButton.setEnabled(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            unregisterReceiver(scanResultsReceiver);
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        //Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(TimerRunnable);
    }
}
