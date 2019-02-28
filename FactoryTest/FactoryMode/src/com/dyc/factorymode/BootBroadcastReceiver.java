package com.dyc.factorymode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.Uri;
import android.widget.Toast;
//modify for audiotest begin
import vendor.mediatek.hardware.nvram.V1_0.INvram;
import android.os.Handler;
import android.widget.Toast;
import java.util.ArrayList;
import com.android.internal.util.HexDump;
//modify for audiotest end
//Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 begin
import android.os.SystemProperties;
import android.app.AlertDialog;
import android.view.WindowManager;
//Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 end

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    private final String SECRET_CODE = "android.provider.Telephony.SECRET_CODE";
    private final Uri myUri = Uri.parse("android_secret_code://96");
    //Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 begin
    private final Uri phoneInfoUri = Uri.parse("android_secret_code://888");
    private final Uri factoryResetUri = Uri.parse("android_secret_code://0");
    //Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 end
    static final String emergency_boot = "android.dyc.action.FACTORY.START";
    //modify for audiotest begin
	private String audioFlag = "";
	static final String WRITE_AUDIO_FLAG = "11";
	static final String CLEAR_AUDIO_FLAG = "00";
	static final int NVRAM_AUDIO_FLAG_OFFSET = 520;
	static final int NVRAM_AUDIO_FLAG_DIGITS = 2;
	static final String NVRAM_PRODUCT_INFO = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
	Handler mHandler=new Handler();
	//modify for audiotest end
	@Override
	public void onReceive(Context context, Intent intent) {

		System.out.println("BootBroadcastReceiver = "+action_boot);
		if (intent.getAction().equals(action_boot) && FeatureOption.SAGEREAL_FACTORYTEST_VERSION) {
			WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Root.getInstance().openWifi = mWifiManager.isWifiEnabled();
			mWifiManager.setWifiEnabled(true);
			
			Intent startIntent = new Intent(context, MainActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
		}
		String action = intent.getAction();
        android.util.Log.e("dyc_BootBroadcastReceiver", "action=" + action);
        //modify for audiotest begin
        if("sagereal.intent.WRITE_FLAG".equals(action)){
            audioFlag = WRITE_AUDIO_FLAG;
			mHandler.post(new Runnable() {
		        @Override
		        public void run() {
		            restoreAudioFlagIntoNvram(audioFlag,context);
		        }
			});
        }else if("sagereal.intent.CLEAR_FLAG".equals(action)){
            audioFlag = CLEAR_AUDIO_FLAG;
			mHandler.post(new Runnable() {
		        @Override
		        public void run() {
		            restoreAudioFlagIntoNvram(audioFlag,context);
		        }
			});
        }
		//modify for audiotest end
        if (SECRET_CODE.equals(action)) {
            Uri uri = intent.getData();
            try {
                Intent factoryModeIntent = new Intent();
                if (uri.equals(myUri)) {
                    factoryModeIntent.setComponent(new android.content.ComponentName("com.dyc.factorymode", "com.dyc.factorymode.TestMainActivity"));
                }
                //Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 begin
                if (uri.equals(phoneInfoUri)){
                	StringBuffer msg = new StringBuffer("")
					   .append("model:  " + SystemProperties.get("ro.product.model") + "\n")
					   .append("brand:  " + SystemProperties.get("ro.product.brand") + "\n")
					   .append("name:  " + SystemProperties.get("ro.product.name") + "\n")
					   .append("device:  " + SystemProperties.get("ro.product.device") + "\n")
					   .append("board:  " + SystemProperties.get("ro.product.board") + "\n")
					   .append("manufacturer:  " + SystemProperties.get("ro.product.manufacturer") + "\n")
					   .append("id:  " + SystemProperties.get("ro.build.display.id") + "\n")
					   .append("gmsversion:  " + SystemProperties.get("ro.com.google.gmsversion"));
					   AlertDialog infoAlertDialog = new AlertDialog.Builder(context)
					   .setTitle("Phone Information")
					   .setMessage(msg)
					   .setPositiveButton("ok",null)
					   .setNegativeButton("cancel",null).create();
					   infoAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					   infoAlertDialog.show();
                }
                
                if (uri.equals(factoryResetUri)){
                	AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle("\u786e\u5b9a\u8981\u6062\u590d\u51fa\u5382\u8bbe\u7f6e\u5417\uff1f");
						builder.setPositiveButton("\u786e\u5b9a", new android.content.DialogInterface.OnClickListener() {
							public void onClick(android.content.DialogInterface dialog, int which) {
								               Intent intent = new Intent(Intent.ACTION_FACTORY_RESET);
								               intent.setPackage("android");
								               intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
								               intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
								               intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, false);
								               context.sendBroadcast(intent);
							}
						});
						builder.setNegativeButton("\u53d6\u6d88", null);
						AlertDialog alertDialog = builder.create();
						alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						alertDialog.show();
                }
                //Readmine 157779 qianfeifei add for *#*#888#*#* && *#*#0#*#* 2018-12-24 end
                factoryModeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(factoryModeIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       if (emergency_boot.equals(action)) {
            abortBroadcast();
            try {
                Intent emergencyIntent = new Intent();
                emergencyIntent.setComponent(new android.content.ComponentName("com.dyc.factorymode", "com.dyc.factorymode.TestMainActivity"));
                emergencyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(emergencyIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	//modify for audiotest begin
	public int restoreAudioFlagIntoNvram(String backCode,Context context){
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
                Toast.makeText(context, toast,Toast.LENGTH_LONG).show();
            }
        }catch(android.os.RemoteException e3){
            android.util.Log.e("panhaoda1234", "e3 = "+e3);
        }
        return err;
    }
	//modify for audiotest end
}
