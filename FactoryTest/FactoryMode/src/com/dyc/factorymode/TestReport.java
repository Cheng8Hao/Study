package com.dyc.factorymode;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.os.SystemProperties;
import android.view.WindowManager;
import android.view.KeyEvent;
import java.util.ArrayList;
import com.dyc.factorymode.util.QRCodeUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.net.Uri;
import com.dyc.factorymode.fmradio.FmMainActivity;

public class TestReport extends Activity {

    private static final String TAG = "TestReport_Sagereal";
    private TextView not_test;
    private TextView test_pass_result;
    private ListView test_fail_result;
    private TextView testresult;
    private ImageView ivBarcodeQr;
    private String str1, str2, str3;
    private TestItem item;
    private int itempass = 0;
    private int count = 0;
    private int [] resultArr =  null ;
    private String testfailArray[]=new String[TestItem.ALL_TEST_ITEM_STRID1.length];
    private int array_count = 0;
    private int usbs, tps, sensors, imeis, audios, headsets, fms, lcds, cameras, autos, recs, louds, keys,compasss,refaudios,dials,fingerprints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.test_report);
        Root.getInstance().addActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initview();
        MySharedPreferences();
        updateview();
    }

    public void initview() {
        SharedPreferences.Editor editor = getSharedPreferences("testmode", MODE_PRIVATE).edit();
        editor.putInt("isautotest", 3);
        editor.commit();
        str1 = getString(R.string.not_test)+"\n";
        str2 = getString(R.string.test_pass_report)+"\n";
        str3 = getString(R.string.test_fail_report)+"\n";
        testresult = (TextView) findViewById(R.id.test_result);
        ivBarcodeQr = (ImageView) findViewById(R.id.iv_barcode_qr);
        not_test = (TextView) findViewById(R.id.not_test);
        test_pass_result = (TextView) findViewById(R.id.test_pass);
        test_fail_result = (ListView) findViewById(R.id.test_fail);
        test_fail_result.setAdapter(new Myadapter());
        listItemOnclick();
    }
     
    public void listItemOnclick(){
        test_fail_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item_title = (TextView) view.findViewById(R.id.test_failitem);
                String s = item_title.getText().toString();
                StartFailActivity(s,R.string.test_imei,TestReport.this,IMEITest.class);
                StartFailActivity(s,R.string.test_usb,TestReport.this,UsbTest.class);
                StartFailActivity(s,R.string.test_audio,TestReport.this,AudioLoopback.class);
                StartFailActivity(s,R.string.test_headset,TestReport.this,HeadsetLoopback.class);
                StartFailActivity(s,R.string.test_fm,TestReport.this,FmMainActivity.class);
                StartFailActivity(s,R.string.test_loud,TestReport.this,LoudSpeaker.class);
                StartFailActivity(s,R.string.test_ctp,TestReport.this,MainActivity.class);
                StartFailActivity(s,R.string.test_sensor,TestReport.this,SensorTest.class);
                StartFailActivity(s,R.string.test_lcd,TestReport.this,TestLCD.class);
                StartFailActivity(s,R.string.test_camera,TestReport.this,CameraTest.class);
                StartFailActivity(s,R.string.test_rec,TestReport.this,RecTest.class);
                StartFailActivity(s,R.string.test_gps,TestReport.this,AutoTest.class);
                StartFailActivity(s,R.string.test_key,TestReport.this,KeyTestActivity.class);
                StartFailActivity(s,R.string.test_tp,TestReport.this,MainActivity.class);
                StartFailActivity(s,R.string.test_refaudio,TestReport.this,RefAudioLoopback.class);
                StartFailActivity(s,R.string.test_compass,TestReport.this,CompassTest.class);
                StartFailActivity(s,R.string.test_call,TestReport.this,CallTest.class);
                StartFailActivity(s,R.string.test_fingerprint,TestReport.this,FingerprintTestActivity.class);
                 /*if(s.equals(getString(R.string.test_call))){
                     startActivity(new Intent(TestReport.this, CallTest.class));
                     overridePendingTransition(0, 0);
                 }*/
            }
        });

    }
    private void StartFailActivity(String str , int id ,Activity paramActivity , Class<?> cls){
         if(str.equals(getString(id))){
             paramActivity.startActivity(new Intent(paramActivity, cls));
             paramActivity.overridePendingTransition(0, 0);
             paramActivity.finish();
         }
    }
    
    class Myadapter extends BaseAdapter{

        @Override
        public int getCount() {
           return count+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(convertView!=null){
                view=convertView;
            }else{
                view=LayoutInflater.from(getApplicationContext()).inflate(R.layout.test_fail_item,parent,false);

            }
            // 初始化数据
            TextView test_failItem = (TextView) view.findViewById(R.id.test_failitem);
            test_failItem.setText(testfailArray[position]);
            return view;
        }
    }

    public void MySharedPreferences() {
        SharedPreferences result = getSharedPreferences("result", Context.MODE_PRIVATE);
        imeis = result.getInt("Imei", 0);
        usbs = result.getInt("Usb", 0);
        audios = result.getInt("Audioloop", 0);
        headsets = result.getInt("Headloop", 0);
        fms = result.getInt("Fm", 0);
        louds = result.getInt("Loudspeaker", 0);
        tps = result.getInt("Tp", 0);
        sensors = result.getInt("Sensor", 0);
        lcds = result.getInt("Lcd", 0);
        cameras = result.getInt("Camera", 0);
        recs = result.getInt("Rec", 0);
        autos = result.getInt("Gps", 0);
        keys = result.getInt("Key", 0);
        compasss = result.getInt("Compass", 0);
        refaudios = result.getInt("Refaudio", 0);
        dials = result.getInt("Dial", 0);
        fingerprints = result.getInt("Fingerprint", 0);
        //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 begin
        if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
           resultArr = new int[]{usbs,imeis,tps,sensors,audios,headsets,fms,louds,lcds,cameras,recs,autos,keys,fingerprints};
        } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
           resultArr = new int[]{usbs,imeis,tps,sensors,compasss,audios,headsets,fms,louds,lcds,cameras,recs,autos,keys,fingerprints};
        }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
           resultArr = new int[]{usbs,imeis,tps,sensors,audios,refaudios,headsets,fms,louds,lcds,cameras,recs,autos,keys,fingerprints};
        }else{
           resultArr = new int[]{usbs,imeis,tps,sensors,compasss,audios,refaudios,headsets,fms,louds,lcds,cameras,recs,autos,keys,fingerprints};
        }
        //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 end
    }
    public void updateview() {
        if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
            count=TestItem.ALL_TEST_ITEM_STRID4.length - 1;
        } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
            count=TestItem.ALL_TEST_ITEM_STRID3.length - 1;
        }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
            count=TestItem.ALL_TEST_ITEM_STRID2.length - 1;
        }else{
            count=TestItem.ALL_TEST_ITEM_STRID1.length - 1;
        }
        for (int i = 0; i < count; i++) {
            TestItem item = new TestItem(i);
            CheckResult(resultArr[i],item.getTestTitle());
        }
        not_test.setText(str1);
        test_pass_result.setText(str2);
        //test_fail_result.setText(str3);
        if (itempass == count) {
            String serialNumber = SystemProperties.get("vendor.gsm.serial");//android p 获取手机序列号
            if (serialNumber.length() > 10) {
                serialNumber = serialNumber.substring(0, 10);
            }
            android.util.Log.d(TAG, "serialNumber===" + serialNumber);
            ivBarcodeQr.setImageBitmap(QRCodeUtil.creatBarcode(TestReport.this, serialNumber, 800, 150, true));
            ivBarcodeQr.setVisibility(View.VISIBLE);
            testresult.setVisibility(View.GONE);
        }
    }
    
    private void CheckResult(int result ,int id){
        if (result == 0) {
             str1 +="\n"+getString(id)+"\n";
        } else if (result == 1) {
             itempass=itempass+1;
             str2 +="\n"+getString(id)+"\n";
        } else if (result == 2){
             //str3 +="\n"+getString(id)+"\n";
             testfailArray[array_count]=getString(id);
             array_count = array_count+1;
        }
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(TestReport.this, TestMainActivity.class);
            startActivity(intent);
           finish();
            return true;
        }
            return super.onKeyDown(keyCode, event);
    }
}
