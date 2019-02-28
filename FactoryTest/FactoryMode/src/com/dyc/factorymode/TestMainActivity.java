package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.view.KeyEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.graphics.Color;
import android.provider.Settings;
import android.net.Uri;
import android.content.ContentResolver;
import vendor.mediatek.hardware.nvram.V1_0.INvram;
import android.os.Handler;
import android.widget.Toast;
import java.util.ArrayList;
import com.android.internal.util.HexDump;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;


public class TestMainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "TestMainActivity_Sagereal";
    private Button autotest;
    private Button manualtest;
    private Button imeitest;
    private Button reporttest;
    private Button chinese;
    private Button english;
    private Button reset;
    private Button audiotest;
    private Button factoryversion;
    private DrawerLayout drawerLayout;
    private BluetoothAdapter bluetooth;
    private int value;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private String audioFlag = "";
	static final String WRITE_AUDIO_FLAG = "11";
	static final String CLEAR_AUDIO_FLAG = "00";
	static final int NVRAM_AUDIO_FLAG_OFFSET = 520;
	static final int NVRAM_AUDIO_FLAG_DIGITS = 2;
	static final String NVRAM_PRODUCT_INFO = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
	Handler mHandler=new Handler();
	private IntentFilter intentFilter;
    private AudioFlagReceiver audioFlagReceiver;
    private Button getgmsinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        android.util.Log.d(TAG, "onCreate success");
        setContentView(R.layout.test_main);
        Root.getInstance().addActivity(this);
        setActivityBrightness(255, this);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }
        
        intentFilter = new IntentFilter();
        intentFilter.addAction("sagereal.intent.WRITE_FLAG");
        intentFilter.addAction("sagereal.intent.CLEAR_FLAG");
        audioFlagReceiver = new AudioFlagReceiver();
        registerReceiver(audioFlagReceiver, intentFilter);
        
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        autotest = (Button) findViewById(R.id.auto_test);
        manualtest = (Button) findViewById(R.id.manual_test);
        chinese = (Button) findViewById(R.id.chinese);
        english = (Button) findViewById(R.id.english);
        imeitest = (Button) findViewById(R.id.imei_test);
        reporttest = (Button) findViewById(R.id.report_test);
        reset = (Button) findViewById(R.id.reset_btn);
        factoryversion = (Button) findViewById(R.id.factory_version);
        audiotest = (Button) findViewById(R.id.audio_test);
        getgmsinfo = (Button) findViewById(R.id.get_gms_info);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        chinese.setOnClickListener(this);
        english.setOnClickListener(this);
        autotest.setOnClickListener(this);
        manualtest.setOnClickListener(this);
        audiotest.setOnClickListener(this);
        reporttest.setOnClickListener(this);
        imeitest.setOnClickListener(this);
        reset.setOnClickListener(this);
        factoryversion.setOnClickListener(this);
        getgmsinfo.setOnClickListener(this);
    }

    public void setActivityBrightness(int brightness, Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            value = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            android.util.Log.d("dyc_brightness", "Exception");
        }
        android.util.Log.d("dyc_brightness", "value_before=" + value);
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        activity.getContentResolver().notifyChange(uri, null);
    }

    @Override
    public void onClick(View v) {

        SharedPreferences.Editor editor = getSharedPreferences("testmode", MODE_PRIVATE).edit();
        switch (v.getId()) {
            case R.id.factory_version:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.get_gms_info:
                startActivity(new Intent(TestMainActivity.this, GmsInfotest.class));
                break;
            case R.id.auto_test:
                editor.putInt("isautotest", 0);
                editor.commit();
                startActivity(new Intent(TestMainActivity.this, UsbTest.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.manual_test:
                editor.putInt("isautotest", 1);
                editor.commit();
                startActivity(new Intent(TestMainActivity.this, ManualTest.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.audio_test:
                Settings.Global.putInt(TestMainActivity.this.getContentResolver(), "sagereal_adbenable", 1);
                Settings.Global.putInt(TestMainActivity.this.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
                audiotest.setTextColor(Color.GREEN);
                pm.goToSleep(SystemClock.uptimeMillis());
                wakeLock.acquire();
                wakeLock.release();
                break;
            case R.id.imei_test:
                editor.putInt("isautotest", 2);
                editor.commit();
                startActivity(new Intent(TestMainActivity.this, IMEITest.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.report_test:
                startActivity(new Intent(TestMainActivity.this, TestReport.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.reset_btn:
                AlertDialog.Builder dialog = new AlertDialog.Builder(TestMainActivity.this);
                dialog.setMessage(R.string.reset_dialog_test);
                dialog.setPositiveButton(R.string.dialog_yes, new loginClick());
                dialog.setNegativeButton(R.string.dialog_cancel, new exitClick());
                dialog.create();
                dialog.show();
                break;
            case R.id.chinese:
                Root.getInstance().isChinese = true;
                TestMainActivity.this.recreate();
                TestMainActivity.this.overridePendingTransition(0, 0);
                break;
            case R.id.english:
                Root.getInstance().isChinese = false;
                TestMainActivity.this.recreate();
                TestMainActivity.this.overridePendingTransition(0, 0);
                break;
            default:
                break;
        }
    }
    
    class AudioFlagReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("sagereal.intent.WRITE_FLAG".equals(action)){
                audioFlag = WRITE_AUDIO_FLAG;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        restoreAudioFlagIntoNvram(audioFlag);
                    }
                });
            }
            if("sagereal.intent.CLEAR_FLAG".equals(action)) {
                audioFlag = CLEAR_AUDIO_FLAG;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        restoreAudioFlagIntoNvram(audioFlag);
                    }
                });
            }
        }
    }

    
    public int restoreAudioFlagIntoNvram(String backCode){
        int err = 0;
        int i = 0;
        try{
            INvram agent = INvram.getService();
            if(agent!=null){
                try{
                    byte[] device_Code = backCode.getBytes("utf-8");
                    String buff = agent.readFileByName(NVRAM_PRODUCT_INFO, NVRAM_AUDIO_FLAG_OFFSET+NVRAM_AUDIO_FLAG_DIGITS);
                    // Remove \0 in the end
                    byte[] pro_info = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
                    for (i = 0; i < NVRAM_AUDIO_FLAG_DIGITS; i++) {
                        pro_info[NVRAM_AUDIO_FLAG_OFFSET+i] = device_Code[i];
                    }
                    ArrayList<Byte> dataArray = new ArrayList<Byte>(NVRAM_AUDIO_FLAG_OFFSET+NVRAM_AUDIO_FLAG_DIGITS);
                    for (i = 0; i < NVRAM_AUDIO_FLAG_OFFSET+NVRAM_AUDIO_FLAG_DIGITS; i++) {
                        dataArray.add(i, new Byte(pro_info[i]));
                    }
                    err = agent.writeFileByNamevec(NVRAM_PRODUCT_INFO, NVRAM_AUDIO_FLAG_OFFSET+NVRAM_AUDIO_FLAG_DIGITS, dataArray);
                }catch (java.io.UnsupportedEncodingException e1) {
                    android.util.Log.e("panhaoda1234", "e1 = "+e1);
                } catch (android.os.RemoteException e2) {
                    android.util.Log.e("panhaoda1234", "e2 = "+e2);
                }
            }
            String toast = "Success to set audio flag to "+"["+backCode+"]";
            if(0==err){
               Toast.makeText(TestMainActivity.this, toast,Toast.LENGTH_SHORT).show();
            }
        }catch(android.os.RemoteException e3){
            android.util.Log.e("panhaoda1234", "e3 = "+e3);
        }
        return err;
    }

    class loginClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_FACTORY_RESET);
            intent.setPackage("android");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
            intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, false);
            sendBroadcast(intent);
            dialog.dismiss();
        }

    }

    class exitClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            bluetooth.disable();
            Root.getInstance().exit();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.Global.getInt(TestMainActivity.this.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1) {
            audiotest.setTextColor(Color.GREEN);
        } else {
            audiotest.setTextColor(Color.WHITE);
        }
        MyUpdateLanguage.updateLanguage(this, Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(audioFlagReceiver);
        super.onDestroy();
    }
}
