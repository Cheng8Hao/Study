package com.dyc.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.dyc.factorymode.util.ShellExe;
import android.provider.Settings;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.Iterator;
import android.os.UserHandle;
import java.util.List;
import java.util.ArrayList;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.view.WindowManager;


public class UsbTest extends Activity implements View.OnClickListener {

    private static final String TAG = "UsbTest_Sagereal";
    private Button nextButton;
    private Button mfail;
    private boolean ispass = false;
    private String ChargeStatus = "";
    private String str = "";
    private int BatteryV = 0;
    private int BatteryL = 0;
    private TextView mPowerShow;
    private TextView remindshow;
    private TextView remindvibrateshow;
    private boolean mRun = false;
    private boolean isCharging = false;
    private boolean issuccess = true;
    private LocationManager locationManager;
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
    private WifiManager.ActionListener mConnectListener;
    private boolean isUnregister;
    private boolean isSccueed;
    private List<ScanResult> mWifiList;
    private WifiManager mWifiManager = null;
    private int testmode;

    BroadcastReceiver mGetBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (paramIntent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                BatteryL = (int) paramIntent.getIntExtra("level", 0);
                BatteryV = (int) paramIntent.getIntExtra("voltage", 0);
                switch (paramIntent.getIntExtra("plugged", 1)) {
                    case 1:
                        ChargeStatus = getString(R.string.power_chongdian) + "(AC)";
                        isCharging = true;
                        break;
                    case 2:
                        ChargeStatus = getString(R.string.power_chongdian) + "(USB)";
                        isCharging = true;
                        break;
                    default:
                        ChargeStatus = getString(R.string.power_weichongdian);
                        isCharging = false;
                        break;
                }
            }
        }
    };

    class readBatteryFile extends Thread {
        public void run() {
            while (mRun) {
                Message localMessage = new Message();
                localMessage.what = 0;
                mHandler.sendMessage(localMessage);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            if (paramMessage.what == 0) {
                showPowerInfomation();
            }
        }
    };

    private void showPowerInfomation() {
        str = getString(R.string.power_status_now)
                + ChargeStatus
                + "\n\n" + getString(R.string.power_now)
                + BatteryL + "%"
                + "\n\n" + getString(R.string.power_dianya_now)
                + BatteryV + "mV"
                + "\n\n";
        mPowerShow.setText(str);
        if (isCharging) {
            remindshow.setText(R.string.remind_usb_out_text);
            remindshow.setTextColor(Color.GREEN);
            checkTestResult();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        setContentView(R.layout.usb_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        Root.getInstance().haveGps = FeatureOption.MTK_GPS_SUPPORT;
        registerReceiver(mGetBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        mRun = true;
        new readBatteryFile().start();
        initview();
        //GPS搜星
        Root.getInstance().openGps = Settings.Secure.isLocationProviderEnabled(
                getContentResolver(), LocationManager.GPS_PROVIDER);
        if (Root.getInstance().haveGps) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            android.util.Log.d(TAG, "Root.getInstance().haveGps==" + Root.getInstance().haveGps);
            openGPSSettings();
        }
        // 监听wifi，搜索装置
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (!FeatureOption.SAGEREAL_FACTORYTEST_VERSION) {
            // get wifi state is open or close
            Root.getInstance().openWifi = mWifiManager.isWifiEnabled();
            mWifiManager.setWifiEnabled(true);
        }
        isSccueed = false;
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(scanResultsReceiver, wifiFilter);
        isUnregister = true;

    }

    public void onClick(View v) {
        if (!ButtonUtils.isFastDoubleClick(v.getId())) {
            switch (v.getId()) {
                case R.id.usb_success_btn:
                    if (ispass) {
                        StartUtil.StartNextActivity("Usb",1,testmode,UsbTest.this,IMEITest.class);
                    }
                    break;
                case R.id.usb_fail_btn:
                        StartUtil.StartNextActivity("Usb",2,testmode,UsbTest.this,IMEITest.class);
                    break;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initview() {
        mPowerShow = (TextView) findViewById(R.id.powershow);
        remindshow = (TextView) findViewById(R.id.remindshow);
        remindvibrateshow = (TextView) findViewById(R.id.remindvibrateshow);
        nextButton = (Button) findViewById(R.id.usb_success_btn);
        mfail = (Button) findViewById(R.id.usb_fail_btn);
        nextButton.setEnabled(false);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
    }
    //提前开启搜星
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
            android.util.Log.d(TAG, "count==" + count);
        }
    }

    private void openGPSSettings() {
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(
                getContentResolver(), LocationManager.GPS_PROVIDER);
        android.util.Log.d(TAG, "gpsEnabled = " + gpsEnabled);
        if (!gpsEnabled) {
            int mode = Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
            Intent intent = new Intent(LocationManager.MODE_CHANGED_ACTION);
            sendBroadcastAsUser(intent, UserHandle.ALL);
        }
        getLocation();
    }

    private void getLocation() {
        Location location = locationManager.getLastKnownLocation("gps");
        android.util.Log.d(TAG, "location = " + location);
        locationManager.requestLocationUpdates("gps", 6000, 1, locationListener);

        if (location == null) {
            locationManager.requestLocationUpdates("gps", 6000, 1, locationListener);
        }
        locationManager.addGpsStatusListener(statusListener);
    }

    // 注册GPS监听器
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

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
    //提前开启搜星

    //提前开启WIFI搜索
    BroadcastReceiver scanResultsReceiver = new BroadcastReceiver() {

        @Override
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
                        isSccueed = true;
                        android.util.Log.d(TAG, "unregisterReceiver isConnected = true");
                    } else {
                        android.util.Log.d(TAG, "unregisterReceiver isConnected = false");
                    }
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                Root.getInstance().wifiMacAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
                String str = "NVRAM WARNING";
                mWifiList = mWifiManager.getScanResults();// 搜索到的设备列表
                android.util.Log.d(TAG, "size() = " + mWifiList.size());
                if (mWifiList == null || mWifiList.size() == 0) {
                    return;
                }

                for (int i = 0; i < mWifiList.size(); i++) {
                    ScanResult result = mWifiList.get(i);
                    String name = result.SSID;
                    int type = getSecurity(result);

                    if (name != null && type == 0 && !name.contains(str) && isUnregister) {
                        mWifiList.remove(i);
                        ConnectWifi(result);
                        if (isUnregister) {
                            isUnregister = false;
                            try {
                                context.unregisterReceiver(scanResultsReceiver);
                            } catch (Exception e) {
                                android.util.Log.d(TAG, "context.unregisterReceiver(scanResultsReceiver):" + e);
                            }
                            new Thread(runnable).start();
                        }
                        android.util.Log.d(TAG, "size() = " + mWifiList.size() + "  name = " + name);
                    }
                }
            }
        }
    };
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            synchronized (this) {
                String str = "NVRAM WARNING";
                if (mWifiList != null && mWifiList.size() > 0) {
                    for (int i = 0; i < mWifiList.size(); i++) {
                        if (isSccueed) {
                            return;
                        }
                        try {
                            Thread.sleep(6000);
                        } catch (Exception e) {
                        }
                        ScanResult result = mWifiList.get(0);
                        String name = result.SSID;
                        int type = getSecurity(result);

                        mWifiList.remove(i);

                        if (name != null && type == 0 && !name.contains(str) && !isSccueed) {
                            ConnectWifi(result);
                            android.util.Log.d(TAG, "size()111 = " + mWifiList.size() + "  name111 = " + name);
                        }
                    }
                }
            }
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
                android.util.Log.d(TAG, "networkId = " + networkId);
                mWifiManager.connect(networkId, mConnectListener);
            }
        }
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    //提前开启WIFI搜索
    private void checkTestResult() {
        ispass = true;
        if (issuccess) {
            nextButton.setEnabled(true);
            nextButton.performClick();
            issuccess = false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGetBatteryReceiver);
        mRun = false;
        finish();
    }
}
