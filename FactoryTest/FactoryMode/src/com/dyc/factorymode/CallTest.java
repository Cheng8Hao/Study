package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.Context;


public class CallTest extends Activity implements View.OnClickListener{

    private Button callphone;
    private Button success;
    private Button mfail;
    private int testmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.call_test);
        Root.getInstance().addActivity(this);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        initview();
    }
    
    private void initview(){
        callphone = (Button) findViewById(R.id.call);
        success = (Button) findViewById(R.id.call_success_btn);
        mfail = (Button) findViewById(R.id.call_fail_btn);
        callphone.setOnClickListener(this);
        mfail.setOnClickListener(this);
        success.setOnClickListener(this);
        success.setEnabled(false);
    }
    
    @Override
    public void onClick(View v) {
    if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
                case R.id.call:
                    success.setEnabled(true);
                    /*Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:112"));
                    startActivity(intent);*/
                    startActivity(new Intent(CallTest.this, FingerprintTestActivity.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.call_success_btn:
                    StartUtil.StartNextActivity("Dial",1,testmode,CallTest.this,TestReport.class);
                    break;
                case R.id.call_fail_btn:
                    StartUtil.StartNextActivity("Dial",2,testmode,CallTest.this,TestReport.class);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
