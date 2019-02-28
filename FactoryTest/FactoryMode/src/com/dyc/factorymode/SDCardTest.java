package com.dyc.factorymode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.os.storage.StorageManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent; 
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.DiskInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.ComponentName;
import com.dyc.factorymode.fmradio.FmMainActivity;
//liangshuang add

public class SDCardTest extends Activity implements OnClickListener {
	String TAG = "SDCardTest";
	private TextView sdcard_inner_info;
	private TextView sdcard_inner_total_capacity;
	private TextView sdcard_inner_available_capacity;
	private TextView sdcard_inner_write;
	private TextView sdcard_inner_read;
	private TextView sd1_result;
	private TextView sdcard_outer_info;
	private TextView sdcard_outer_total_capacity;
	private TextView sdcard_outer_available_capacity;
	private TextView sdcard_outer_write;
	private TextView sdcard_outer_read;
	private TextView sd2_result;
	private Button nextButton;
	private Button mfinishbtn;
	private boolean isInternalSDCardOk = false;
	private int clickflag = 1;
	private boolean isMplatform;
	public final static boolean MTK_2SDCARD_SWAP = SystemProperties.get("ro.mtk_2sdcard_swap").equals("1");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
		setContentView(R.layout.sdcard_test);
		Root.getInstance().addActivity(this);		
		sdcard_inner_info = (TextView)findViewById(R.id.sdcard_inner_info);
		sdcard_inner_total_capacity = (TextView)findViewById(R.id.sdcard_inner_total_capacity);
		sdcard_inner_available_capacity = (TextView)findViewById(R.id.sdcard_inner_available_capacity);
		sdcard_inner_write = (TextView)findViewById(R.id.sdcard_inner_write);
		sdcard_inner_read = (TextView)findViewById(R.id.sdcard_inner_read);
		sd1_result = (TextView)findViewById(R.id.sd1_result);
		
		sdcard_outer_info = (TextView)findViewById(R.id.sdcard_outer_info);
		sdcard_outer_total_capacity = (TextView)findViewById(R.id.sdcard_outer_total_capacity);
		sdcard_outer_available_capacity = (TextView)findViewById(R.id.sdcard_outer_available_capacity);
		sdcard_outer_write = (TextView)findViewById(R.id.sdcard_outer_write);
		sdcard_outer_read = (TextView)findViewById(R.id.sdcard_outer_read);
		sd2_result = (TextView)findViewById(R.id.sd2_result);
		
		nextButton = (Button)findViewById(R.id.nextButton);
		mfinishbtn = (Button) findViewById(R.id.finish_btn);
		SharedPreferences result = getSharedPreferences("testmode", +Context.MODE_PRIVATE);
        int testmode = result.getInt("isautotest", 0);
        if(testmode==1){
            nextButton.setVisibility(View.GONE);
            mfinishbtn.setVisibility(View.VISIBLE);
        }
        if(testmode==0){
            nextButton.setVisibility(View.VISIBLE);
            mfinishbtn.setVisibility(View.GONE);
        }
		nextButton.setOnClickListener(this);
		mfinishbtn.setOnClickListener(this);
		isMplatform = SystemProperties.get("ro.build.version.sdk").equals("23");

		if(MTK_2SDCARD_SWAP){
			sdCardSwap();
		}else{
			sdCardNoSwap();
		}
	
	}
    private String getExternalSdPath(Context context) {
            String externalPath = "";
            StorageManager mStorageManager = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            StorageVolume[] volumes = mStorageManager.getVolumeList();
            for (StorageVolume volume : volumes) {
                String volumePathStr = volume.getPath();
                android.util.Log.e("liyang", "volumePathStr="+volumePathStr);
                android.util.Log.e("liyang", "volume.getState()="+volume.getState());
                if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(volume.getState())) {
                    VolumeInfo volumeInfo = mStorageManager.findVolumeById(volume.getId());
                    android.util.Log.e("liyang", "volume.isEmulated())="+volume.isEmulated());
                    if (volume.isEmulated()) {
                        String viId = volumeInfo.getId();
                        android.util.Log.e("liyang", "viId="+viId);
                        // If external sd card, the viId will be like "emulated:179,130"
                        if (!viId.equalsIgnoreCase("emulated")) {
                            externalPath = volumePathStr;
                            android.util.Log.e("liyang", "externalPath="+externalPath);
                            break;
                        }
                    } else {
                        DiskInfo diskInfo = volumeInfo.getDisk();
                        if (diskInfo == null) {
                            continue;
                        }
                        String diId = diskInfo.getId();
                        // If external sd card, the diId will be like "disk:179,128"
                        if (!diId.equalsIgnoreCase("disk:179,0")) {
                            externalPath = volumePathStr;
                             android.util.Log.e("liyang", "2externalPath="+externalPath);
                            break;
                        }
                    }
                } else {
                } 
            }
            return externalPath;
        } 

	private void sdCardSwap() {
		// TODO Auto-generated method stub
		boolean isHaveInternalSD = false;
		File internalSDCard = new File("/storage/sdcard");
		String file = internalSDCard.toString();
		try{
			IMountService ms = getMs();
			isHaveInternalSD = ms.getVolumeState(file).equals(Environment.MEDIA_MOUNTED);
			} catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
		}
		
		if(isHaveInternalSD){
			long freeSpace = internalSDCard.getFreeSpace();
			long totalSpace = internalSDCard.getTotalSpace();
			String path = getExternalSdPath(this) + "/FactoryTest.txt";
			boolean isWriteSuccess = testWrite(path);
			boolean isReadSuccess = testRead(path);
			
			sdcard_inner_info.setText(getResources().getString(R.string.sdcard_info));
			sdcard_inner_info.setTextColor(Color.BLUE);
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 begin
			sdcard_inner_total_capacity.setText(getResources().getString(R.string.sdcard_total_capacity) + getSpaceStr(totalSpace));
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 end
			sdcard_inner_total_capacity.setTextColor(Color.BLUE);
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 begin
			sdcard_inner_available_capacity.setText(getResources().getString(R.string.sdcard_available_capacity) + getSpaceStr(freeSpace));
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 end
			sdcard_inner_available_capacity.setTextColor(Color.BLUE);
			
			if(isWriteSuccess){
				sdcard_inner_write.setText(R.string.sdcard_write_pass);
				sdcard_inner_write.setTextColor(Color.BLUE);
			}else{
				sdcard_inner_write.setText(R.string.sdcard_write_fail);
				sdcard_inner_write.setTextColor(Color.RED);
			}
			if(isReadSuccess){
				sdcard_inner_read.setText(R.string.sdcard_read_pass);
				sdcard_inner_read.setTextColor(Color.BLUE);
			}else{
				sdcard_inner_read.setText(R.string.sdcard_read_fail);
				sdcard_inner_read.setTextColor(Color.RED);
			}
			isInternalSDCardOk  = isWriteSuccess && isReadSuccess;
			if(isInternalSDCardOk){
				sd1_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_pass));
				sd1_result.setTextColor(Color.BLUE);
			}else{
				sd1_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_not_pass));
				sd1_result.setTextColor(Color.RED);
			}
			
		}else{
			sdCardNoSwap();
			return;
		}
		
		boolean isHaveExternalSD = false;
		File externalSDCard = Environment.getExternalStorageDirectory();
		String externalFile = externalSDCard.toString();
		try {
			IMountService ms = getMs();
			isHaveExternalSD = ms.getVolumeState(externalFile).equals(Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(isHaveExternalSD){
			long freeSpace = externalSDCard.getFreeSpace();
			long totalSpace = externalSDCard.getTotalSpace();
			String path = "/storage/sdcard0/FactoryTest.txt";
			boolean isWriteSuccess = testWrite(path);
			boolean isReadSuccess = testRead(path);
			
			sdcard_outer_info.setText(getResources().getString(R.string.sdcard_outer_info));
			sdcard_outer_info.setTextColor(Color.BLUE);
//redmine29247 xiejianlong modify for force stopped when sdcard is full 2015.2.5 begin
			sdcard_outer_total_capacity.setText(getResources().getString(R.string.sdcard_total_capacity) + getSpaceStr(totalSpace));
//redmine29247 xiejianlong modify for force stopped when sdcard is full 2015.2.5 end
			sdcard_outer_total_capacity.setTextColor(Color.BLUE);
//redmine29247 xiejianlong modify for force stopped when sdcard is full 2015.2.5 begin
	
			sdcard_outer_available_capacity.setText(getResources().getString(R.string.sdcard_available_capacity) + getSpaceStr(freeSpace));
//redmine29247 xiejianlong modify for force stopped when sdcard is full 2015.2.5 end
			sdcard_outer_available_capacity.setTextColor(Color.BLUE);
			
			if(isWriteSuccess){
				sdcard_outer_write.setText(R.string.sdcard_write_pass);
				sdcard_outer_write.setTextColor(Color.BLUE);
			}else{
				sdcard_outer_write.setText(R.string.sdcard_write_fail);
				sdcard_outer_write.setTextColor(Color.RED);
			}
			if(isReadSuccess){
				sdcard_outer_read.setText(R.string.sdcard_read_pass);
				sdcard_outer_read.setTextColor(Color.BLUE);
			}else{
				sdcard_outer_read.setText(R.string.sdcard_read_fail);
				sdcard_outer_read.setTextColor(Color.RED);
			}
			isInternalSDCardOk  = isWriteSuccess && isReadSuccess;
			if(isInternalSDCardOk){
				sd2_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_pass));
				sd2_result.setTextColor(Color.BLUE);
			}else{
				sd2_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_not_pass));
				sd2_result.setTextColor(Color.RED);
			}
		}else{
			sdcard_outer_info.setText(getResources().getString(R.string.sdcardouter_notpass_result));
			sdcard_outer_info.setTextColor(Color.RED);
			sdcard_outer_total_capacity.setVisibility(View.INVISIBLE);
			sdcard_outer_available_capacity.setVisibility(View.INVISIBLE);
			sdcard_outer_write.setVisibility(View.INVISIBLE);
			sdcard_outer_read.setVisibility(View.INVISIBLE);
			sd2_result.setVisibility(View.INVISIBLE);
		}
		
	}
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 begin
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
//redmine31735 jiangcunbin modify for force stopped when sdcard is full 2015-04-20 end

	 private IMountService getMs() {
		// TODO Auto-generated method stub
		IBinder service = ServiceManager.getService("mount");
		if (service != null) {
			return IMountService.Stub.asInterface(service);
		} else {
			Log.e(TAG, "Can't get mount service");
		}
		return null;
	}

	private boolean testRead(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		BufferedReader bufferedReader = null;
		if (file.exists()){
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				if(bufferedReader.readLine().equals("123321")){
					boolean flag = file.delete();
					if(flag){
						Log.i(TAG, "file.delete psaa");
					}else{
						Log.i(TAG, "file.delete fail");
					}
				}
				bufferedReader.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}finally{
				//bufferedReader.close();
			}
		}else{
			Log.i(TAG, "file read fial");
			return false;
		}
		return true;
	}

	private boolean testWrite(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		FileOutputStream fileOutputStream = null;
		if(file.exists()){
			file.delete();
		}
		try {
			Log.i(TAG, "path==="+path);
			boolean isSuccess = file.createNewFile();
			if (isSuccess){
				Log.i(TAG, "Write pass");
				 fileOutputStream = new FileOutputStream(file);
				String testString = "123321"; 
				fileOutputStream.write(testString.getBytes());
			}else{
				Log.i(TAG, "createNewFile fail");
			    return false;
			}
			fileOutputStream.close();

		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.i(TAG, "Write fail");
			return false;
		}finally{
			//fileOutputStream.close();
		}
		return true;
	}

	private void sdCardNoSwap() {
		// TODO Auto-generated method stub
		boolean isHaveInternalSD = false;
		File internalSDCard = Environment.getExternalStorageDirectory();
		Log.i("jiangcunbin", "internalSDCard="+internalSDCard.toString());
		String file = internalSDCard.toString();
		StorageManager storageManager = (StorageManager) getSystemService(
		                STORAGE_SERVICE);
		try {
		//liangshuang modify 
			IMountService ms = getMs();
			isHaveInternalSD = getMs().getVolumeState(file).equals(
					Environment.MEDIA_MOUNTED);
			  if(storageManager != null){
				  isHaveInternalSD = storageManager.getVolumeState(file).equals(
							Environment.MEDIA_MOUNTED);
			 }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Log.i("jiangcunbin", "isHaveInternalSD="+isHaveInternalSD);
		if (isHaveInternalSD) {
			long freeSpace = internalSDCard.getFreeSpace();
			long totalSpace = internalSDCard.getTotalSpace();
			String path = "/storage/sdcard0/FactoryTest.txt";
			boolean isWriteSuccess = testWrite(path);
			boolean isReadSuccess = testRead(path);
			
			sdcard_inner_info.setText(getResources().getString(R.string.sdcard_info));
			sdcard_inner_info.setTextColor(Color.BLUE);
			sdcard_inner_total_capacity.setText(getResources().getString(R.string.sdcard_total_capacity) + getSpaceStr(totalSpace));
			sdcard_inner_total_capacity.setTextColor(Color.BLUE);
			sdcard_inner_available_capacity.setText(getResources().getString(R.string.sdcard_available_capacity) + getSpaceStr(freeSpace));
			sdcard_inner_available_capacity.setTextColor(Color.BLUE);			
			if(isWriteSuccess){
				sdcard_inner_write.setText(R.string.sdcard_write_pass);
				sdcard_inner_write.setTextColor(Color.BLUE);
			}else{
				sdcard_inner_write.setText(R.string.sdcard_write_fail);
				sdcard_inner_write.setTextColor(Color.RED);
			}
			if(isReadSuccess){
				sdcard_inner_read.setText(R.string.sdcard_read_pass);
                                sdcard_inner_read.setTextColor(Color.BLUE);
			}else{
				sdcard_inner_read.setText(R.string.sdcard_read_fail);
				sdcard_inner_read.setTextColor(Color.RED);
			}
			isInternalSDCardOk  = isWriteSuccess && isReadSuccess;
			if(isInternalSDCardOk){
				sd1_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_pass));
				sd1_result.setTextColor(Color.BLUE);
			}else{
				sd1_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_not_pass));
				sd1_result.setTextColor(Color.RED);
			}
			
		}else{
			sdcard_inner_info.setText(getResources().getString(R.string.sdcard_notpass_result));
			sdcard_inner_info.setTextColor(Color.RED);
		}
		
		boolean isHaveExternalSD = false;
		File externalSDCard = null;
		if(isMplatform){
			externalSDCard = new File("/mnt/m_external_sd");
		}else{
			externalSDCard = new File(getExternalSdPath(this));
		}
		Log.i("jiangcunbin", "externalSDCard="+externalSDCard.toString());
		try {
		//liangshuang modify 
			IMountService ms = getMs();
			isHaveExternalSD = ms.getVolumeState("/storage/sdcard1").equals(
					Environment.MEDIA_MOUNTED);
			  if(storageManager != null){
				  isHaveExternalSD = storageManager.getVolumeState(externalSDCard.toString()).equals(
							Environment.MEDIA_MOUNTED);
			 }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		if (isHaveExternalSD) {
			long freeSpace = externalSDCard.getFreeSpace();
			long totalSpace = externalSDCard.getTotalSpace();
			String path;
			if(isMplatform){
				path= "/mnt/m_external_sd/TecnoFactoryTest.txt";
			}else{
				path= getExternalSdPath(this) + "/TecnoFactoryTest.txt";
			}			
			boolean isWriteSuccess = testWrite(path);
			boolean isReadSuccess = testRead(path);
			
			sdcard_outer_info.setText(getResources().getString(R.string.sdcard_outer_info));
			sdcard_outer_info.setTextColor(Color.BLUE);
			sdcard_outer_total_capacity.setText(getResources().getString(R.string.sdcard_total_capacity) + getSpaceStr(totalSpace));
			sdcard_outer_total_capacity.setTextColor(Color.BLUE);
			sdcard_outer_available_capacity.setText(getResources().getString(R.string.sdcard_available_capacity) + getSpaceStr(freeSpace));
			sdcard_outer_available_capacity.setTextColor(Color.BLUE);
			if(isWriteSuccess){
				sdcard_outer_write.setText(R.string.sdcard_write_pass);
				sdcard_outer_write.setTextColor(Color.BLUE);
			}else{
				sdcard_outer_write.setText(R.string.sdcard_write_fail);
				sdcard_outer_write.setTextColor(Color.RED);
			}
			if(isReadSuccess){
				sdcard_outer_read.setText(R.string.sdcard_read_pass);
				sdcard_outer_read.setTextColor(Color.BLUE);
			}else{
				sdcard_outer_read.setText(R.string.sdcard_read_fail);
				sdcard_outer_read.setTextColor(Color.RED);
			}
			isInternalSDCardOk  = isWriteSuccess && isReadSuccess;
			if(isInternalSDCardOk){
				sd2_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_pass));
				sd2_result.setTextColor(Color.BLUE);
			}else{
				sd2_result.setText(getResources().getString(R.string.sdcard_result) + getResources().getString(R.string.sdcard_not_pass));
				sd2_result.setTextColor(Color.RED);
			}
		}else{
			sdcard_outer_info.setText(getResources().getString(R.string.sdcardouter_notpass_result));
			sdcard_outer_info.setTextColor(Color.RED);
			sdcard_outer_total_capacity.setVisibility(View.INVISIBLE);
			sdcard_outer_available_capacity.setVisibility(View.INVISIBLE);
			sdcard_outer_write.setVisibility(View.INVISIBLE);
			sdcard_outer_read.setVisibility(View.INVISIBLE);
			sd2_result.setVisibility(View.INVISIBLE);
		}
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.nextButton:
		    if (clickflag == 1){
		    Intent intent = new Intent();
		    SharedPreferences.Editor editor = getSharedPreferences("result", MODE_PRIVATE).edit();
            editor.putBoolean("Sdcard", true);
            editor.commit();
		    intent.setClass(SDCardTest.this, FmMainActivity.class);
		     //intent.setComponent(new ComponentName(SDCardTest.this, "com.dyc.factorymode.fmradio.FmMainActivity"));
		     //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent);
		    //startActivity(intent);
		    clickflag = 2;
		    }
		    break;
		case R.id.finish_btn:
            finish();
            break;
       }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Intent intent = new Intent();
			intent.setClass(SDCardTest.this, HeadsetLoopback.class);
			startActivity(intent);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			onResume();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
    	MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
    	super.onResume();
	    clickflag = 1;
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
}

