package com.dyc.factorymode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.PhoneConstants;

import android.graphics.Color;
import android.view.WindowManager;
import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;

import com.dyc.factorymode.util.FileUtil;

public class GmsInfotest extends Activity {

    public static final String mSNForFactory = "Factory Need Write SN";
    private BluetoothAdapter bluetooth;
    private TextView softtypeView;
    private TextView softversionView;
    private TextView googlekeyView;
    private TextView imei1View;
    private TextView imei2View;
    private TextView sim1View;
    private TextView sim2View;
    private TextView btAddressView;
    private TextView wifiAddressView;
    private String btMacAddress = null;
    private String wifiAddress = null;
    private TextView serialnoView;
    private TextView chargestatusView;
    private TextView powernowView;
    private int sim1Status = 0;
    private int sim2Status = 0;
    private String status_nosim = "";
    private String status_unknow = "";
    private String status_netlock = "";
    private String status_pinlock = "";
    private String status_puklock = "";
    private String status_good = "";
    private String ChargeStatus = "";
    private boolean isCharging = false;
    private boolean mRun = false;
    private Resources res;
    private WifiManager mWifiManager;
    private int BatteryV = 0;
    private int BatteryL = 0;
    private boolean firstwrite = true;
    private String softtypeStr = "Softtype :";
    private String serialnoStr = "Serialno :";
    private String softversionStr = "Softversion :";
    private String sim1Str = "Sim1 :";
    private String sim2Str = "Sim2 :";
    private String imei1Str = "Imei1 :";
    private String imei2Str = "Imei2 :";
    private String btMacAddressStr = "BtMacAddress :";
    private String wifiAddressStr = "WifiAddress :";
    private String chargestatusStr = "Chargestatus :";
    private String powernowStr = "Powernow :";
    private String googlekeyStr = "Googlekey :";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gms_infotest);
        initview();
        getinfo();
    }

    private void initview() {
        softtypeView = (TextView) findViewById(R.id.soft_type);
        softversionView = (TextView) findViewById(R.id.softversionView);
        imei1View = (TextView) findViewById(R.id.imei1View);
        imei2View = (TextView) findViewById(R.id.imei2View);
        sim1View = (TextView) findViewById(R.id.sim1View);
        sim2View = (TextView) findViewById(R.id.sim2View);
        btAddressView = (TextView) findViewById(R.id.btAddress);
        wifiAddressView = (TextView) findViewById(R.id.wifiAddress);
        serialnoView = (TextView) findViewById(R.id.serialnoView);
        chargestatusView = (TextView) findViewById(R.id.charge_status);
        powernowView = (TextView) findViewById(R.id.power_nows);
        googlekeyView = (TextView) findViewById(R.id.googlekeyView);
        res = getResources();
        mRun = true;
        status_nosim = res.getString(R.string.status_nosim);
        status_unknow = res.getString(R.string.status_unknow);
        status_netlock = res.getString(R.string.status_netlock);
        status_pinlock = res.getString(R.string.status_pinlock);
        status_puklock = res.getString(R.string.status_puklock);
        status_good = res.getString(R.string.status_good);
    }

    @Override
    protected void onResume() {
        //Writeinfo();//保存GMS信息到手机内存中，路径：/sdcard/Gmsinfo/gmsfile.txt
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
    protected void onDestroy() {
        unregisterReceiver(mGetBatteryReceiver);
        mRun = false;
        super.onDestroy();
    }

    private void getinfo() {
        /*******************获取充电状态和电池电量**************/
        registerReceiver(mGetBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        new readBatteryFile().start();

        /*******************获取软件版本类型**************/
        softtypeStr += android.os.Build.TYPE;
        softtypeView.setText(softtypeStr);
        
        /*******************获取软件版本号**************/
        softversionStr += SystemProperties.get("ro.mediatek.version.release", "V00");
        softversionView.setText(softversionStr);
        
        /*******************获取Google key状态**************/ 
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 begin
        String googlekeyStatus= SystemProperties.get("vendor.soter.teei.googlekey.status", "FAIL");
        if(googlekeyStatus.equals("OK")){
            googlekeyStr += "PASS";
        }else{
            googlekeyStr += "FAIL";
        }
        googlekeyView.setText(googlekeyStr);
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 end
        
        /*public static boolean getGoogleKeyState() {
            return GoogleKeyNative.getGoogleKeyState() == 0;//瓶伯
         }*/

        /*******************获取蓝牙地址**************/
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }
        btMacAddress = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
        if (btMacAddress == null) {
            btMacAddressStr += "null";
            btAddressView.setText(btMacAddressStr);
        } else {
            btMacAddressStr += btMacAddress;
            btAddressView.setText(btMacAddressStr);
        }

        /*******************获取WiFi地址**************/
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            //progressDialog.show();
            //new OpenWifiThread().start();
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        wifiAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
        if (wifiAddress == null) {
            wifiAddressStr += "null";
            wifiAddressView.setText(wifiAddressStr);
            wifiAddressView.setTextColor(Color.RED);
        } else if (wifiAddress.equals("02:00:00:00:00:00")) {
            wifiAddressStr += wifiAddress;
            wifiAddressView.setText(wifiAddressStr);
            wifiAddressView.setTextColor(Color.RED);
        } else {
            wifiAddressStr += wifiAddress;
            wifiAddressView.setText(wifiAddressStr);
            wifiAddressView.setTextColor(Color.GREEN);
        }
        if (btMacAddress != null && wifiAddress != null) {
            if (btMacAddress.equals(wifiAddress)) {
                btAddressView.setTextColor(Color.RED);
                btAddressView.setTextColor(Color.RED);
            }
        }

        /*******************android p 获取手机序列号**************/
        serialnoStr += SystemProperties.get("vendor.gsm.serial");
        serialnoView.setText(serialnoStr);
        if (mSNForFactory.equals(SystemProperties.get("vendor.gsm.serial"))) {
            serialnoView.setTextColor(Color.RED);
        }

        /*******************获取SIM状态**************/
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm.getPhoneCount() == 2) {
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);
            sim2Status = tm.getSimState(PhoneConstants.SIM_ID_2);
            imei1Str += tm.getDeviceId(PhoneConstants.SIM_ID_1);
            imei2Str += tm.getDeviceId(PhoneConstants.SIM_ID_2);
            imei1View.setText(imei1Str);
            if (tm.getDeviceId(PhoneConstants.SIM_ID_1) == null) {
                imei1View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("000000000000000")) {
                imei1View.setTextColor(Color.RED);
            }
            imei2View.setText(imei2Str);
            if (tm.getDeviceId(PhoneConstants.SIM_ID_2) == null) {
                imei2View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_2).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_2).equals("000000000000000")) {
                imei2View.setTextColor(Color.RED);
            }
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1Str += status_nosim;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1Str += status_unknow;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim1Str += status_netlock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim1Str += status_pinlock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim1Str += status_puklock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    sim1Str += status_good;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
            switch (sim2Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim2Str += status_nosim;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim2Str += status_unknow;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim2Str += status_netlock;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim2Str += status_pinlock;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim2Str += status_puklock;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    sim2Str += status_good;
                    sim2View.setText(sim2Str);
                    sim2View.setTextColor(Color.GREEN);
                    break;
            }

        } else if (tm.getPhoneCount() == 1) {
            sim2View.setVisibility(View.GONE);
            imei2View.setVisibility(View.GONE);
            imei1Str += tm.getDeviceId(PhoneConstants.SIM_ID_1);
            imei1View.setText(imei1Str);
            if (tm.getDeviceId(PhoneConstants.SIM_ID_1) == null) {
                imei1View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("000000000000000")) {
                imei1View.setTextColor(Color.RED);
            }
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1Str += status_nosim;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1Str += status_unknow;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sim1Str += status_netlock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sim1Str += status_pinlock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sim1Str += status_puklock;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    sim1Str += status_good;
                    sim1View.setText(sim1Str);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
        }
    }

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
                chargestatusView.setText(chargestatusStr + ChargeStatus);
                powernowView.setText(powernowStr +BatteryL + "%");
                if(firstwrite){
                    Writeinfo();
                    firstwrite=false;
                }
            }
        }
    };

    private void Writeinfo() {
        String gmsinfo = (softtypeStr + "\n" + softversionStr + "\n" + serialnoStr  + "\n" + imei1Str + "\n" + imei2Str + "\n"
                + btMacAddressStr + "\n" + wifiAddressStr + "\n" + powernowStr +BatteryL + "%" + "\n" + googlekeyStr);
        FileUtil.writeTxtToFile(gmsinfo, "/sdcard/Gmsinfo/", "gmsfile.txt");
    }

}
