package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.telephony.PhoneConstants;
import java.lang.reflect.Method;
import android.view.KeyEvent;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.SystemProperties;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
//import com.mediatek.telephony.TelephonyManagerEx;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Environment;
import java.io.File;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.DiskInfo;
import vendor.mediatek.hardware.nvram.V1_0.INvram;
import com.android.internal.util.HexDump;
import android.app.ProgressDialog;

public class IMEITest extends Activity implements OnClickListener {

    private static final String TAG = "IMEITest_Sagereal";
    private TextView serialnoView;
    private TextView imei1View;
    private TextView imei2View;
    private TextView imsi1View;
    private TextView imsi2View;
    private TextView sim1View;
    private TextView sim2View;
    private Button nextButton;
    private Button mfail;
    private TextView btAddress;
    private TextView wifiAddress;
    private TextView version;
    private TextView isOtpSupportView;
    private TextView teeStatusView;
    private TextView keyStatusView;
    private TextView audioTestView;
    private TextView tp_version;
    private String audioTestFlag = "";
    private String serialnoStr = "";
    private String str_tpversion;
    public static final String mSNForFactory = "Factory Need Write SN";
    private String imei1Str = "";
    private String imei2Str = "";
    private String imsi1Str = "";
    private String imsi2Str = "";
    private String versionStr = "";
    private String wifiAddressStr = null;
    private String btMacAddress = null;
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
    private String keyStr = "";
    private String tpversion = "";
    private Resources res;
    private WifiManager mWifiManager;
    private int testmode;
    private boolean isMplatform;
    private boolean ispass = true;
    private boolean mRun = true;
    private ProgressDialog progressDialog;
    private boolean isSnTest = true, isImeitest = true, isImsitest = true, isSimTest = false, isVersionTest = true, isaudiotest = true, isBtTest = true, isWifiTest = true, isgooglekey = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        setContentView(R.layout.imei_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        res = getResources();
        isMplatform = SystemProperties.get("ro.build.version.sdk").equals("23");
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 begin
        keyStr += SystemProperties.get("vendor.soter.teei.googlekey.status", "FAIL");
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 end
        android.util.Log.d(TAG, "keyStr="+keyStr);
        serialnoStr = res.getString(R.string.imei_serialno);
        audioTestFlag = "";
        imei1Str = res.getString(R.string.imei_imei1);
        imei2Str = res.getString(R.string.imei_imei2);
        imsi1Str = res.getString(R.string.imei_imsi1);
        imsi2Str = res.getString(R.string.imei_imsi2);
        sim1Str = res.getString(R.string.status_sim1);
        sim2Str = res.getString(R.string.status_sim2);
        status_nosim = res.getString(R.string.status_nosim);
        status_unknow = res.getString(R.string.status_unknow);
        status_netlock = res.getString(R.string.status_netlock);
        status_pinlock = res.getString(R.string.status_pinlock);
        status_puklock = res.getString(R.string.status_puklock);
        tpversion = res.getString(R.string.tpversion);
        status_good = res.getString(R.string.status_good);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            // get wifi state is open or close
            mWifiManager.setWifiEnabled(true);
            progressDialog.show();
            new OpenWifiThread().start();
            
        }
        versionStr += SystemProperties.get("ro.mediatek.version.release", "V00");
		/*versionStr += SystemProperties.get("ro.custom.build.srver1", "V00");
		versionStr += SystemProperties.get("ro.custom.build.srver2", "V00");
		versionStr = SystemProperties.get("ro.sagereal.version.release", "V00V00V00");*/
        try {
            str_tpversion = readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
        if (str_tpversion != null) {
            //redmine 165266 lihongmin add for factorymode:Tp_version 20190225 beign
            //String[] split = str_tpversion.split("\\.");
            //str_tpversion = "V" + getHexString(split[0]) + "." + getHexString(split[1]);
            //tp_version.setText(tpversion + str_tpversion);
            tp_version.setText(tpversion + "V" + str_tpversion);
            //redmine 165266 lihongmin add for factorymode:Tp_version 20190225 end
            tp_version.setTextColor(Color.GREEN);
        }
        serialnoStr += SystemProperties.get("vendor.gsm.serial");//android p 获取手机序列号
        serialnoView.setText(serialnoStr);
        if (mSNForFactory.equals(SystemProperties.get("vendor.gsm.serial"))) {
            isSnTest = false;
            serialnoView.setTextColor(Color.RED);
        }
    }

    public String getHexString(String str) {
        String s = Integer.toHexString(Integer.parseInt(str));
        return s;
    }

    private String readFromFile() throws IOException {
        //redmine 165266 lihongmin add for factorymode:Tp_version 20190225 beign
        File file = new File("/sys/devices/platform/bus/1100a000.i2c/i2c-1/1-005d/fts_fw_version");
        //redmine 165266 lihongmin add for factorymode:Tp_version 20190225 end
        if (!file.exists() || file.isDirectory()) {
            android.util.Log.d("dyc_read", "file not exit");
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            sb.append(temp + "");
            temp = br.readLine();
        }
        return sb.toString();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        btMacAddress = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
        if (null == btMacAddress) {
            isBtTest = false;
            btAddress.setText(res.getString(R.string.bt_address) + "null");
            btAddress.setTextColor(Color.RED);
        } else {
            btAddress.setText(res.getString(R.string.bt_address) + btMacAddress);
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        wifiAddressStr = wifiInfo == null ? null : wifiInfo.getMacAddress();
        if (wifiAddressStr == null) {
            isWifiTest = false;
            wifiAddress.setText(res.getString(R.string.wifi_address) + "null");
            wifiAddress.setTextColor(Color.RED);
        }else if(wifiAddressStr.equals("02:00:00:00:00:00")){
            isWifiTest = false;
            wifiAddress.setText(res.getString(R.string.wifi_address) + wifiAddressStr);
            wifiAddress.setTextColor(Color.RED);
        }else {
            isWifiTest = true;
            wifiAddress.setText(res.getString(R.string.wifi_address) + wifiAddressStr);
            wifiAddress.setTextColor(Color.GREEN);
        }
        if (btMacAddress != null && wifiAddressStr != null) {
            if (btMacAddress.equals(wifiAddressStr)) {
                btAddress.setTextColor(Color.RED);
                wifiAddress.setTextColor(Color.RED);
                ispass = false;
            }
        }
        if (FeatureOption.MTK_EMMC_SUPPORT_OTP) {
            isOtpSupportView.setTextColor(Color.GREEN);
            isOtpSupportView.setText(res.getString(R.string.otp_support) + res.getString(R.string.otp_support_yes));
            isOtpSupportView.setVisibility(View.VISIBLE);
        } else {
            isOtpSupportView.setVisibility(View.GONE);
        }
        if (Integer.valueOf(SystemProperties.get("ro.build.version.sdk")).intValue() >= 26) {//我司Android 8.0后支持tee
            teeStatusView.setTextColor(Color.GREEN);
            teeStatusView.setText(res.getString(R.string.tee_integrated) + res.getString(R.string.tee_integrated_yes));
        } else {
            teeStatusView.setVisibility(View.GONE);
        }
        // android.util.Log.d(TAG, "getGoogleKeyState() == " + getGoogleKeyState());
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 begin
        if (keyStr.equals("OK")){
            keyStatusView.setTextColor(Color.GREEN);
            keyStatusView.setText(res.getString(R.string.key_integrated) + res.getString(R.string.key_integrated_ok));
        } else {
            isgooglekey = false;
            keyStatusView.setTextColor(Color.RED);
            keyStatusView.setText(res.getString(R.string.key_integrated) + res.getString(R.string.key_integrated_fail));
        }
        //Redmine165265 chenghao M for check googlekey status 2019-02-20 end
        testFinish();
    }
    class OpenWifiThread extends Thread {
        public void run() {
            while (mRun) {
                if (mWifiManager.isWifiEnabled()) {
                    progressDialog.dismiss();
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    wifiAddressStr = wifiInfo == null ? null : wifiInfo.getMacAddress();
                    if (wifiAddressStr == null) {
                        isWifiTest = false;
                        wifiAddress.setText(res.getString(R.string.wifi_address) + "null");
                        wifiAddress.setTextColor(Color.RED);
                    }else if(wifiAddressStr.equals("02:00:00:00:00:00")){
                        isWifiTest = false;
                        wifiAddress.setText(res.getString(R.string.wifi_address) + wifiAddressStr);
                        wifiAddress.setTextColor(Color.RED);
                    }else {
                        isWifiTest = true;
                        wifiAddress.setText(res.getString(R.string.wifi_address) + wifiAddressStr);
                        wifiAddress.setTextColor(Color.GREEN);
                    }
                    testFinish();
                }
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        }
    }
    // public static boolean getGoogleKeyState() {
    //  return GoogleKeyNative.getGoogleKeyState() == 0;
    // }

    /**
     * Sim Test
     */
    private void simTest() {
        int SimState1 = 0;
        int SimState2 = 0;
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        android.util.Log.d(TAG, "tm.getPhoneCount() == " + tm.getPhoneCount());
        if (tm.getPhoneCount() == 2) {
            SimState1 = TelephonyManager.getDefault().getSimState(0);
            SimState2 = TelephonyManager.getDefault().getSimState(1);
            imei1Str += tm.getDeviceId(PhoneConstants.SIM_ID_1);
            imei2Str += tm.getDeviceId(PhoneConstants.SIM_ID_2);
            imei1View.setText(imei1Str);
            if (tm.getDeviceId(PhoneConstants.SIM_ID_1) == null) {
                isImeitest = false;
                imei1View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("000000000000000")) {
                isImeitest = false;
                imei1View.setTextColor(Color.RED);
            }
            imei2View.setText(imei2Str);

            if (tm.getDeviceId(PhoneConstants.SIM_ID_2) == null) {
                isImeitest = false;
                imei2View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_2).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_2).equals("000000000000000")) {
                isImeitest = false;
                imei2View.setTextColor(Color.RED);
            }
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);
            sim2Status = tm.getSimState(PhoneConstants.SIM_ID_2);
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1View.setText(sim1Str + status_nosim);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1View.setText(sim1Str + status_unknow);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_netlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_pinlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_puklock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_good);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
            switch (sim2Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim2View.setText(sim2Str + status_nosim);
                    sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim2View.setText(sim2Str + status_unknow);
                    sim2View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    isSimTest = true;
                    sim2View.setText(sim2Str + status_netlock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    isSimTest = true;
                    sim2View.setText(sim2Str + status_pinlock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    isSimTest = true;
                    sim2View.setText(sim2Str + status_puklock);
                    sim2View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimTest = true;
                    sim2View.setText(sim2Str + status_good);
                    sim2View.setTextColor(Color.GREEN);
                    break;
            }
            if (TelephonyManager.SIM_STATE_ABSENT != sim1Status && TelephonyManager.SIM_STATE_UNKNOWN != sim1Status) {
                int subId = PhoneConstants.SIM_ID_1;
                int[] subIds = SubscriptionManager.getSubId(PhoneConstants.SIM_ID_1);
                if (subIds != null && subIds.length > 0) {
                    subId = subIds[0];
                    if (SubscriptionManager.isValidSubscriptionId(subId)) {
                        //isImsitest = true;
                        imsi1Str += tm.getSubscriberId(subId);
                    } else {
                        imsi1Str += "null";
                        // isImsitest = false;
                        imsi1View.setTextColor(Color.RED);
                    }
                } else {
                    imsi1Str += "null";
                    //isImsitest = false;
                    imsi1View.setTextColor(Color.RED);
                }
            } else {
                imsi1Str += "null";
                //isImsitest = false;
                imsi1View.setTextColor(Color.RED);
            }

            if (TelephonyManager.SIM_STATE_ABSENT != sim2Status && TelephonyManager.SIM_STATE_UNKNOWN != sim2Status) {
                int subId = PhoneConstants.SIM_ID_2;
                int[] subIds = SubscriptionManager.getSubId(PhoneConstants.SIM_ID_2);
                if (subIds != null && subIds.length > 0) {
                    subId = subIds[0];
                    if (SubscriptionManager.isValidSubscriptionId(subId)) {
                        //isImsitest = true;
                        imsi2Str += tm.getSubscriberId(subId);
                    } else {
                        imsi2Str += "null";
                        //isImsitest = false;
                        imsi2View.setTextColor(Color.RED);
                    }
                } else {
                    imsi2Str += "null";
                    //isImsitest = false;
                    imsi2View.setTextColor(Color.RED);
                }
            } else {
                imsi2Str += "null";
                //isImsitest = false;
                imsi2View.setTextColor(Color.RED);
            }
            imsi1View.setText(imsi1Str);
            imsi2View.setText(imsi2Str);
        } else if (tm.getPhoneCount() == 1) {
            sim2View.setVisibility(View.GONE);
            imsi2View.setVisibility(View.GONE);
            imei2View.setVisibility(View.GONE);
            SimState1 = TelephonyManager.getDefault().getSimState(0);
            imei1Str += tm.getDeviceId(PhoneConstants.SIM_ID_1);
            imei1View.setText(imei1Str);
            if (tm.getDeviceId(PhoneConstants.SIM_ID_1) == null) {
                isImeitest = false;
                imei1View.setTextColor(Color.RED);
            } else if (tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("354648020000251") || tm.getDeviceId(PhoneConstants.SIM_ID_1).equals("000000000000000")) {
                isImeitest = false;
                imei1View.setTextColor(Color.RED);
            }
            sim1Status = tm.getSimState(PhoneConstants.SIM_ID_1);
            switch (sim1Status) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim1View.setText(sim1Str + status_nosim);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim1View.setText(sim1Str + status_unknow);
                    sim1View.setTextColor(Color.RED);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_netlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_pinlock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_puklock);
                    sim1View.setTextColor(Color.GREEN);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isSimTest = true;
                    sim1View.setText(sim1Str + status_good);
                    sim1View.setTextColor(Color.GREEN);
                    break;
            }
            imsi1Str += tm.getSubscriberId(PhoneConstants.SIM_ID_1);
            imsi1View.setText(imsi1Str);
            if (tm.getSubscriberId(PhoneConstants.SIM_ID_1) == null) {
                //isImsitest = false;
                imsi1View.setTextColor(Color.RED);
            } else {
                //isImsitest = true;
            }
        }
    }

    private void initView() {
        serialnoView = (TextView) findViewById(R.id.serialnoView);
        audioTestView = (TextView) findViewById(R.id.audioTestView);
        imei1View = (TextView) findViewById(R.id.imei1View);
        imei2View = (TextView) findViewById(R.id.imei2View);
        imsi1View = (TextView) findViewById(R.id.imsi1View);
        imsi2View = (TextView) findViewById(R.id.imsi2View);
        tp_version = (TextView) findViewById(R.id.tp_version);
        imsi1View.setVisibility(View.GONE);
        imsi2View.setVisibility(View.GONE);
        sim1View = (TextView) findViewById(R.id.sim1View);
        sim2View = (TextView) findViewById(R.id.sim2View);
        nextButton = (Button) findViewById(R.id.imei_success_btn);
        mfail = (Button) findViewById(R.id.imei_fail_btn);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        if (testmode == 2) {
            nextButton.setVisibility(View.GONE);
            mfail.setVisibility(View.GONE);
        }
        btAddress = (TextView) findViewById(R.id.btAddress);
        wifiAddress = (TextView) findViewById(R.id.wifiAddress);
        version = (TextView) findViewById(R.id.version);
        isOtpSupportView = (TextView) findViewById(R.id.otpSupport);
        teeStatusView = (TextView) findViewById(R.id.teeStatus);
        keyStatusView = (TextView) findViewById(R.id.keyStatus);
        keyStatusView.setTextColor(Color.RED);
        if (versionStr != "") {
            version.setText(res.getString(R.string.version) + versionStr);
        } else {
            isVersionTest = false;
            version.setText(res.getString(R.string.version) + "null");
            version.setTextColor(Color.RED);
        }
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        mfail.setOnClickListener(this);
        audioTestFlag += readAudioFlagFromNvram();
        android.util.Log.d(TAG, "audioTestFlag = " + audioTestFlag);
        if ("11".equals(audioTestFlag)) {
            isaudiotest = true;
            audioTestView.setText(res.getString(R.string.imei_audioflag) + "-" + audioTestFlag);
            audioTestView.setTextColor(Color.GREEN);
        } else {
            isaudiotest = false;
            audioTestView.setText(res.getString(R.string.test_audio_flag) + "-" + "00");
            audioTestView.setTextColor(Color.RED);
        }
        simTest();
    }

    static final int NVRAM_AUDIO_FLAG_OFFSET = 520;
    static final int NVRAM_AUDIO_FLAG_DIGITS = 2;
    static final String NVRAM_PRODUCT_INFO = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
    public String readAudioFlagFromNvram(){
		String deviceCode = "00";
        try {
            INvram agent = INvram.getService();
            if(agent!=null){
				try{
					String buff = agent.readFileByName(NVRAM_PRODUCT_INFO, NVRAM_AUDIO_FLAG_OFFSET+NVRAM_AUDIO_FLAG_DIGITS);
					// Remove \0 in the end
					byte[] pro_info = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
					byte[] device_Code = new byte[NVRAM_AUDIO_FLAG_DIGITS];
					for (int i = 0; i < NVRAM_AUDIO_FLAG_DIGITS; i++) {
                        device_Code[i] = pro_info[NVRAM_AUDIO_FLAG_OFFSET+i];
                    }
					deviceCode = new String(device_Code, "utf-8");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return deviceCode;
    }

    @Override
    public void onClick(View v) {
        if (!ButtonUtils.isFastDoubleClick(v.getId())) {
            switch (v.getId()) {
                case R.id.imei_success_btn:
                    StartUtil.StartNextActivity("Imei",1,testmode,IMEITest.this,MainActivity.class);
                    break;
                case R.id.imei_fail_btn:
                    StartUtil.StartNextActivity("Imei",2,testmode,IMEITest.this,MainActivity.class);
                    break;
            }
        }
    }

    private void testFinish() {
        if (ispass) {
            if (isSnTest && isImeitest && isSimTest && isVersionTest && isaudiotest && isBtTest && isWifiTest && isgooglekey) {
                nextButton.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (testmode == 2) {
                startActivity(new Intent(IMEITest.this, TestMainActivity.class));
                finish();
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRun = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
