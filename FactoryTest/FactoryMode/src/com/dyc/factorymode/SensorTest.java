package com.dyc.factorymode;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
//add nfc
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.app.PendingIntent;
import android.content.IntentFilter.MalformedMimeTypeException;
//add nfc
//Redmine 81152 liuxiangnan 80_N平台霍尔传感器未完善，编译报错，暂时注释掉 2016.12.07 begin
/// add by panminjie for redmine 27561 (lid feature) begin @{
//import android.lid.LidManager;
//import android.os.ILidStateListener;
/// @} panminjie add end
//Redmine 81152 liuxiangnan 80_N平台霍尔传感器未完善，编译报错，暂时注释掉 2016.12.07 end
//jiangcunbin add for FM setup too slowly in *906# 2015.05.15 
//jiangcunbin add for FM setup too slowly in *906# 2015.05.15

// Redmine81157 jiangqiao  add lid for flip cover 2017-03-16 begin
//import android.lid.LidManager;
//import android.os.ILidStateListener;
// Redmine81157 jiangqiao  add lid for flip cover 2017-03-16 end

public class SensorTest extends Activity implements OnClickListener {

    private static final String TAG = "SensorTest_DYC";
    private Button nextButton;
    private Button mfail;
    private Button reGsensorButton;
    private Button reBtButton;
    private TextView gsensorView;
    private TextView btView;
    private TextView psensorView;
    private TextView hallSensorView;
    private Button reHallSensorButton;
    //private LidManager mLidManager;
    // @} panminjie add end
    private TextView ledTest;
    private final static int BT_SUCCESS = 3;
    private final static int BT_FAULT = 4;
    private final static int GSENSOR_SUCCESS = 5;
    private final static int GSENSOR_FAULT = 6;
    private final static int PSENSOR_SUCCESS = 11;
    private final static int PSENSOR_FAULT = 12;
    private final static int LSENSOR_FAULT = 13;
    private final static int LSENSOR_SUCCESS = 14;
    private final static int HALL_SENSOR_SUCCESS = 21;
    private final static int HALL_SENSOR_FAULT = 22;
    private static final int HALL_SENSOR_FAULT_TIME = 20000;
    private final static int NEXT_BUTTON_ENABLE = 23;
    private int mHallSensorFaultMsgTag = 0;
    private BluetoothAdapter mBluetooth = null;
    private boolean btFlags = false;
    private SensorManager sensorMgr;
    private SensorEventListener sensorListener;
    private MyProxSensor mProxSensor;
    private Sensor mSensor;
    private int testmode;
    private Resources res;
    private AudioManager mAudioManager;
    private int fmDefaultVolume = 0;
    private boolean isFailed = false;
    private ImageView show;
    private float x;
    private float y;
    private float z;
    private TextView mGsensorXYZ;
    private int flag_test = 0;
    private boolean testpass = true, isbtpass = false, isgsensorpass = false, ispsensorpass = false,isnfcpass = false,islsensorpass =false;
    private SensorManager mgsensorManager;
    private SensorEventListener mGSensorListener;
    private SensorEventListener mLightSensorListener;
    //add nfc
    private TextView nfcView;
	private Button reNFC;
	private NfcAdapter mNfcAdapter;
	private IntentFilter[] nfcIntentFilter;
	private PendingIntent nfcPenIntent; 
	private String[][] techLists;
	private String[] techNFcA = new String[] {NfcA.class.getName()};
	private String[] techNFcB = new String[] {NfcB.class.getName()};
	private String[] MifareClassic = new String[] {MifareClassic.class.getName()};
	private String[] MifareUltralight = new String[] {MifareUltralight.class.getName()};
	private boolean nfcTestSucessFlag = false;
	private int mNfcFaultMsgTag = 1;
	private boolean nfcStatus = false;
	private final static int NFC_SUCCESS = 24;
	private final static int NFC_FAULT = 25;
    //add nfc
    private float[] xx;
    private SensorManager mLightSensorManager;
    private int count = 0;
    private TextView showNUM;
    private TextView lsensorView;
    /**
     * 卫星状态监听器
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.psensor_test);
        res = getResources();
        Root.getInstance().addActivity(this);
        Root.getInstance().haveGSensor = FeatureOption.SAGEREAL_FACTORYTEST_GSENSOR;
        android.util.Log.d("dyc_sensor","haveGSensor =　"+FeatureOption.SAGEREAL_FACTORYTEST_GSENSOR);
        Root.getInstance().haveProxSensor = FeatureOption.SAGEREAL_FACTORYTEST_PROXSENSOR;
        android.util.Log.d("dyc_sensor","haveProxSensor =　"+FeatureOption.SAGEREAL_FACTORYTEST_PROXSENSOR);
        /// add by panminjie for redmine 27561 (lid feature) begin @{
        //jiangqiao add  begin
		/*
	 	mLidManager = (LidManager) getSystemService(Context.LID_SERVICE);
		Root.getInstance().haveHallSensor = mLidManager.getHallSupport();
	 	if (Root.getInstance().haveHallSensor) {
			 mLidManager.addLidStateListener(new ILidStateListener.Stub() {
				public void onLidStateChange() {
					if (mLidManager.isLidClosed()) {
						Message msg = Message.obtain(mHandler, HALL_SENSOR_SUCCESS);
						mHandler.sendMessage(msg);
					}
				}
			  });
	  	}
		*/
//		/// @} panminjie add end
        //jiangqiao add end
//Redmine 81152 liuxiangnan 80_N平台霍尔传感器未完善，编译报错，暂时注释掉 2016.12.07 end
        Root.getInstance().haveFrontCamera = FeatureOption.SAGEREAL_FACTORYTEST_FRONTCAMERA;
        Root.getInstance().SatelliteNum = 0;
        // 设置广播信息过滤，监听BT
        IntentFilter btFilter = new IntentFilter();
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        btFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, btFilter);
        initView();
    }

    private void initView() {
        lsensorView = (TextView)findViewById(R.id.lsensorView);
        if(!FeatureOption.SAGEREAL_FACTORYTEST_LIGHT_SENSOR){
          lsensorView.setVisibility(View.GONE);
          islsensorpass = true;
        }
        showNUM = (TextView)findViewById(R.id.show_lsensor);
        mGsensorXYZ = (TextView) findViewById(R.id.gsensor_xyz);
        show = (ImageView) findViewById(R.id.show_gsensor);
        mgsensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor localSensor = mgsensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
            
            }
            public void onSensorChanged(SensorEvent paramSensorEvent) {
                x = paramSensorEvent.values[0];
                y = paramSensorEvent.values[1];
                z = paramSensorEvent.values[2];
                int i = (int) x;
                int j = (int) y;
                int k = (int) z;
                int m = getMax(i, j, k);
                mGsensorXYZ.setText(getString(R.string.num) + x + ",  " + y + ",  " + z);
                if (Math.abs(i) == m) {
                    if (i < 0) {
                        show.setImageResource(R.drawable.gsensor_x_2);
                        flag_test = flag_test | 0x02;
                    } else {
                        show.setImageResource(R.drawable.gsensor_x);
                        flag_test = flag_test | 0x01;
                    }
                } else if (j == m) {
                    show.setImageResource(R.drawable.gsensor_y);
                    flag_test = flag_test | 0x04;
                } else {
                    show.setImageResource(R.drawable.gsensor_z);
                    flag_test = flag_test | 0x08;
                }
                if((flag_test & 0x0f) == 0x0f){
                Message msg = Message.obtain(mHandler, GSENSOR_SUCCESS);
                mHandler.sendMessage(msg);
                }
                if (Math.abs(x) > 15||Math.abs(y) > 15||Math.abs(z) > 15) {
                Message msg = Message.obtain(mHandler, GSENSOR_FAULT);
                mHandler.sendMessage(msg);
            } 
                testFinish();
            }
        };
        //mgsensorManager.registerListener(mGSensorListener, localSensor, 1);
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        // get bt state is open or close
        Root.getInstance().openBt = mBluetooth.isEnabled();
        gsensorView = (TextView) findViewById(R.id.gsensorView);
        btView = (TextView) findViewById(R.id.btView);
        psensorView = (TextView) findViewById(R.id.pSensorView);
        /// add by panminjie for redmine 27561 (lid feature) begin @{
        hallSensorView = (TextView) findViewById(R.id.hallSensorView);
        reHallSensorButton = (Button) findViewById(R.id.reHallSensor);
        reHallSensorButton.setOnClickListener(this);
        try {
            // if (!this.getResources().getBoolean(com.mediatek.internal.R.bool.config_sagereal_hall_support)) {
            if (false) {
                android.util.Log.d(TAG, "HW may support hall,but config is false!");
                hallSensorView.setVisibility(View.GONE);
                reHallSensorButton.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /// @} panminjie add end
        //add nfc
        nfcView = (TextView) findViewById(R.id.nfcView);
		reNFC = (Button) findViewById(R.id.reNFC);
		reNFC.setOnClickListener(this);
		if(!FeatureOption.SAGEREAL_FACTORYTEST_NFC){
	    	isnfcpass = true;
			nfcView.setVisibility(View.GONE);
			reNFC.setVisibility(View.GONE);
		}
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent nfcIntent = new Intent(this,getClass());
		nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		nfcPenIntent = PendingIntent.getActivity(this,0,nfcIntent,0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try{
				ndef.addDataType("*/*");
		}catch(MalformedMimeTypeException e){
			throw new RuntimeException("ndef fail in factory test",e);
		}
		nfcIntentFilter = new IntentFilter[] {ndef,tag,tech};
		techLists=new String[][] {techNFcA,techNFcB,MifareClassic,MifareUltralight};
		if(mNfcAdapter != null){
		 nfcStatus = mNfcAdapter.isEnabled();
		}
		IntentFilter shutDownIntentFilter = new IntentFilter();
        	shutDownIntentFilter.addAction(Intent.ACTION_SHUTDOWN);
        	shutDownIntentFilter.addAction(Intent.ACTION_REBOOT);
        	registerReceiver(shutDownBroadcastReceiver,shutDownIntentFilter);
		//add nfc
        nextButton = (Button) findViewById(R.id.bt_success_btn);
        mfail = (Button) findViewById(R.id.bt_fail_btn);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        reGsensorButton = (Button) findViewById(R.id.reGsensor);
        reBtButton = (Button) findViewById(R.id.reBt);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
        reGsensorButton.setOnClickListener(this);
        reBtButton.setOnClickListener(this);
        mLightSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor localLightSensor = mLightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (localLightSensor != null) {
            mLightSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
            }
            public void onSensorChanged(SensorEvent paramSensorEvent) {
	            SensorTest.this.xx=paramSensorEvent.values;
	            count++;
	            android.util.Log.d("chenghao","SensorTest.this.x=="+SensorTest.this.xx[0]+"====count="+count);
	            if(count > 1) {
                Message msg = Message.obtain(mHandler, LSENSOR_SUCCESS);
                mHandler.sendMessage(msg);
	            }
	            //Redmine165182 chenghao M for light Sensor 2018-02-23 begin 
	            showNUM.setText(SensorTest.this.getString(R.string.num) + SensorTest.this.xx[0]);
	            //Redmine165182 chenghao M for light Sensor 2018-02-23 end 
            }
            };
            mLightSensorManager.registerListener(mLightSensorListener, localLightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
                Message msg = Message.obtain(mHandler, LSENSOR_FAULT);
                mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.bt_success_btn:
                startNext();
                closeNfcStatus();
                if(FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
                        StartUtil.StartNextActivity("Sensor",1,testmode,SensorTest.this,CompassTest.class);
                    }else{
                        StartUtil.StartNextActivity("Sensor",1,testmode,SensorTest.this,AudioLoopback.class);
                    }
                break;
            case R.id.bt_fail_btn:
                closeNfcStatus();
                if(FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
                        StartUtil.StartNextActivity("Sensor",2,testmode,SensorTest.this,CompassTest.class);
                    }else{
                        StartUtil.StartNextActivity("Sensor",2,testmode,SensorTest.this,AudioLoopback.class);
                    }
                break;
            case R.id.reGsensor:
                flag_test=0;
                gsensorView.setTextColor(Color.RED);
                gsensorView.setText(res.getString(R.string.app_name_gsensor));
                mHandler.post(gSensorRunnable);
                break;
            case R.id.reBt:
                btView.setTextColor(Color.RED);
                btView.setText(res.getString(R.string.app_name_bt));
                if (mBluetooth.isEnabled()) {
                    mBluetooth.startDiscovery();
                } else {
                    mBluetooth.enable();
                }
                break;
            /// add by panminjie for redmine 27561 (lid feature) begin @{
            case R.id.reHallSensor:
                hallSensorView.setText(res.getString(R.string.app_name_hall_sensor));
                hallSensorView.setTextColor(Color.RED);
                Root.getInstance().isHallSensor = false;
                Message msg = Message.obtain(mHandler, HALL_SENSOR_FAULT);
                msg.arg1 = ++mHallSensorFaultMsgTag;
                mHandler.sendMessageDelayed(msg, HALL_SENSOR_FAULT_TIME);
                /// @} panminjie add end
                break;
           //add nfc     
            case R.id.reNFC:
			    nfcView.setText(res.getString(R.string.nfc_test));
			    nfcView.setTextColor(Color.RED);
			    isnfcpass = false;
			    if(mNfcAdapter!=null){
				    mNfcAdapter.enableForegroundDispatch (this, nfcPenIntent, nfcIntentFilter, techLists);
			    }
			    checkNfcStatus();
                Message msg1 = Message.obtain(mHandler, NFC_FAULT);
                msg1.arg1 = ++mNfcFaultMsgTag;
                mHandler.sendMessageDelayed(msg1, HALL_SENSOR_FAULT_TIME);
		    	break;
		    //add nfc
           }
        }

    }
    //add nfc
    	private void checkNfcStatus(){
		if (mNfcAdapter != null) {
			boolean isEnabled = mNfcAdapter.isEnabled();
			if (!isEnabled) {
				mNfcAdapter.enable();
			}
		}
	}

	private void closeNfcStatus(){
		if (mNfcAdapter != null) {
			boolean isEnabled = mNfcAdapter.isEnabled();
			if (isEnabled) {
				if (!nfcStatus) {
					mNfcAdapter.disable();
				}
			}
		}
	}

	BroadcastReceiver shutDownBroadcastReceiver = new BroadcastReceiver() {
		@Override
	       public void onReceive(Context context, Intent intent) {
	       	if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) || intent.getAction().equals(Intent.ACTION_REBOOT) ){
				closeNfcStatus();
	          }
	       }
	};
    //add nfc
    Runnable gSensorRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (this) {
                Sensor rientation = null;
                sensorListener = mGSensorListener;
                List<Sensor> sensorList = sensorMgr.getSensorList(Sensor.TYPE_ALL);
                int size = sensorList.size();
                for (int i = 0; i < size; i++) {
                    int type = sensorList.get(i).getType();
                    if (type == Sensor.TYPE_ACCELEROMETER) {
                        rientation = sensorList.get(i);
                        break;
                    }
                }

                if (rientation != null) {
                    sensorMgr.registerListener(sensorListener, rientation,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }
    };

    /*class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            System.out.println("x = " + x);
            System.out.println("y = " + y);
            System.out.println("z = " + z);
            if (Math.abs(z) > 15) {
                Message msg = Message.obtain(mHandler, GSENSOR_FAULT);
                mHandler.sendMessage(msg);
                sensorMgr.unregisterListener(sensorListener);
            } else {
                Message msg = Message.obtain(mHandler, GSENSOR_SUCCESS);
                mHandler.sendMessage(msg);
                sensorMgr.unregisterListener(sensorListener);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    }*/

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String BtName = device.getName();
                String BtAddress = device.getAddress();
                android.util.Log.d(TAG, "BtName = " + BtName);
                android.util.Log.d(TAG, "BtAddress = " + BtAddress);
                if (BtName != null || BtAddress != null) {
                    Message msg = Message.obtain(mHandler, BT_SUCCESS);
                    Bundle bundle = new Bundle();
                    if (BtName != null) {
                        bundle.putString("name", BtName);
                    } else {
                        bundle.putString("name", BtAddress);
                    }

                    mBluetooth.cancelDiscovery();
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                System.out.println("state = " + mBluetooth.getState());
                if (mBluetooth.getState() == 12) {
                    // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
                    mBluetooth.startDiscovery();
                }
            }
        }
    };

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        android.util.Log.d("dyc_nbwtf","msg.what==="+msg.what);
            switch (msg.what) {
                case BT_SUCCESS:
                    isbtpass = true;
                    testFinish();
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("name");
                    btView.setText(res.getString(R.string.bt_success) + str);
                    btView.setTextColor(Color.GREEN);
                    Root.getInstance().isBt = true;
                    break;
                case BT_FAULT:
                    btView.setText(res.getString(R.string.bt_fault));
                    btView.setTextColor(Color.RED);
                    Root.getInstance().isBt = false;
                    break;
                case GSENSOR_SUCCESS:
                    isgsensorpass = true;
                    testFinish();
                    //mHandler.removeCallbacks(gSensorRunnable);
                    gsensorView.setText(res.getString(R.string.gsensor_success));
                    gsensorView.setTextColor(Color.GREEN);
                    Root.getInstance().isGsensor = true;
                    break;
                case GSENSOR_FAULT:
                    mHandler.removeCallbacks(gSensorRunnable);
                    gsensorView.setText(res.getString(R.string.gsensor_fault));
                    gsensorView.setTextColor(Color.RED);
                    Root.getInstance().isGsensor = false;
                    break;
                case PSENSOR_SUCCESS:
                    ispsensorpass = true;
                    testFinish();
                    psensorView.setText(res.getString(R.string.psensor_success));
                    psensorView.setTextColor(Color.GREEN);
                    Root.getInstance().isProxSensor = true;
                    break;
                case PSENSOR_FAULT:
                    ispsensorpass = false;
                    testFinish();
                    psensorView.setText(res.getString(R.string.psensor_fault));
                    psensorView.setTextColor(Color.RED);
                    Root.getInstance().isProxSensor = false;
                    break;
                case LSENSOR_SUCCESS:
                    islsensorpass = true;
                    testFinish();
                    lsensorView.setText(res.getString(R.string.lsensor_success));
                    lsensorView.setTextColor(Color.GREEN);
                    break;
                case LSENSOR_FAULT:
                    islsensorpass = false;
                    testFinish();
                    lsensorView.setText(res.getString(R.string.lsensor_fault));
                    lsensorView.setTextColor(Color.RED);
                    break;
                    
                case HALL_SENSOR_SUCCESS:
                    hallSensorView.setText(res.getString(R.string.hall_sensor_success));
                    hallSensorView.setTextColor(Color.GREEN);
                    Root.getInstance().isHallSensor = true;
                    break;
                case HALL_SENSOR_FAULT:
                    if (!Root.getInstance().isHallSensor && (msg.arg1 == mHallSensorFaultMsgTag)) {
                        hallSensorView.setText(res.getString(R.string.hall_sensor_fault));
                        hallSensorView.setTextColor(Color.RED);
                        Root.getInstance().isHallSensor = false;
                    }
                    break;
                case NEXT_BUTTON_ENABLE:
                    //nextButton.setEnabled(true);
                    break;
                //add nfc    
                case NFC_SUCCESS:
                    isnfcpass = true;
                    testFinish();
	                break;
		        case NFC_FAULT:
                      if (!isnfcpass && msg.arg1 == mNfcFaultMsgTag){
                       nfcView.setTextColor(Color.RED);
                       nfcView.setText(res.getString(R.string.nfc_test_fail));
                      }
                    break;
                //add nfc      
                default:
                    return;
            }
        }
    };

    class MyProxSensor implements SensorEventListener {

        private final SensorManager mSensorManager;
        private float mMaxRange;

        MyProxSensor() {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (mSensor != null) {
                mMaxRange = mSensor.getMaximumRange();
            } else {
                Message msg = Message.obtain(mHandler, PSENSOR_FAULT);
                mHandler.sendMessage(msg);
            }
        }

        private void startRegisterListener() {
            if (mSensorManager != null && mSensor != null) {
                mSensorManager.registerListener(this, mSensor,
                        SensorManager.SENSOR_DELAY_UI);
            } else {
                Root.getInstance().isProxSensor = false;
            }
        }

        private void stopRegisterListener() {
            if (mSensorManager != null && mSensor != null) {
                mSensorManager.unregisterListener(this);
                mSensor = null;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            try {
                // Keep it until we get a phone supporting this feature
                android.util.Log.d(TAG, "event.values.length =  " + event.values.length);
                if ((event.values != null) && (event.values.length > 0)) {
                    if (event.values[0] < mMaxRange) {
                        android.util.Log.d(TAG, "---Near--- ");
                        Message msg = Message.obtain(mHandler, PSENSOR_SUCCESS);
                        mHandler.sendMessage(msg);
                        mProxSensor.stopRegisterListener();
                        mProxSensor = null;
                    } else {
                        android.util.Log.d(TAG, "---Far away--- ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        nextButton.setEnabled(false);
        //add nfc
        if (nfcTestSucessFlag) {
			nfcTestSucessFlag = false;
			return;
		}
		nfcView.setText(res.getString(R.string.nfc_test));
		nfcView.setTextColor(Color.RED);
		//add nfc
        gsensorView.setText(res.getString(R.string.app_name_gsensor));
        gsensorView.setTextColor(Color.RED);
        btView.setText(res.getString(R.string.app_name_bt));
        btView.setTextColor(Color.RED);
        psensorView.setText(res.getString(R.string.app_name_psensor));
        psensorView.setTextColor(Color.RED);
        hallSensorView.setText(res.getString(R.string.app_name_hall_sensor));
        hallSensorView.setTextColor(Color.RED);
        // G-sensor is have or no
        if (Root.getInstance().haveGSensor) {
            new Thread(gSensorRunnable).start();
        } else {
            isgsensorpass = true;
            gsensorView.setVisibility(View.GONE);
            reGsensorButton.setVisibility(View.GONE);
        }

        // prox-Sensor is have or no
        if (Root.getInstance().haveProxSensor) {
            if (mProxSensor == null) {
                mProxSensor = new MyProxSensor();
                mProxSensor.startRegisterListener();
            }
        } else {
            psensorView.setVisibility(View.GONE);
            ispsensorpass = true;

        }
        

        if (Root.getInstance().haveHallSensor) {
            Root.getInstance().isHallSensor = false;
            Message msg = Message.obtain(mHandler, HALL_SENSOR_FAULT);
            msg.arg1 = ++mHallSensorFaultMsgTag;
            mHandler.sendMessageDelayed(msg, HALL_SENSOR_FAULT_TIME);
        } else {
            hallSensorView.setVisibility(View.GONE);
            reHallSensorButton.setVisibility(View.GONE);
        }
        if (mBluetooth.isEnabled()) {
            mBluetooth.startDiscovery();
        } else {
            mBluetooth.enable();
        }
        //add nfc
        if(FeatureOption.SAGEREAL_FACTORYTEST_NFC){
           Message msg1 = Message.obtain(mHandler, NFC_FAULT);
           msg1.arg1 = ++mNfcFaultMsgTag;
           mHandler.sendMessageDelayed(msg1, HALL_SENSOR_FAULT_TIME);
        }
        if(mNfcAdapter!=null){
			mNfcAdapter.enableForegroundDispatch (this, nfcPenIntent, nfcIntentFilter, techLists);
		}
		checkNfcStatus();
		//add nfc
		testFinish();
    }

    private void startNext() {
        if (Root.getInstance().haveProxSensor && mSensor != null) {
            mProxSensor.stopRegisterListener();
            mProxSensor = null;
        }
    }

    private void testFinish() {
    //android.util.Log.d("dyc_wtf","isbtpass=="+isbtpass+"   isgsensorpass=="+isgsensorpass+"  ispsensorpass==  "+ispsensorpass+"  isnfcpass==  "+isnfcpass);
        if ((flag_test & 0x0f) == 0x0f && isbtpass && isgsensorpass && ispsensorpass && isnfcpass && islsensorpass) {
            nextButton.setEnabled(true);
            if(testpass){
                testpass=false;
                nextButton.performClick();
            }
        }
    }

    private int getMax(int paramInt1, int paramInt2, int paramInt3) {
        int[] arrayOfInt = new int[3];
        arrayOfInt[0] = Math.abs(paramInt1);
        arrayOfInt[1] = paramInt2;
        arrayOfInt[2] = paramInt3;
        Arrays.sort(arrayOfInt);
        return arrayOfInt[(arrayOfInt.length - 1)];
    }
    //add nfc
	@Override
	protected void onNewIntent (Intent intent) {
		android.util.Log.d("cshy","onNewIntent"+intent.getAction());
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())||
			NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			nfcView.setTextColor(Color.GREEN);
			nfcView.setText(res.getString(R.string.nfc_test_success));
			nfcTestSucessFlag = true;
			closeNfcStatus();
			Message msg = Message.obtain(mHandler, NFC_SUCCESS);
			mHandler.sendMessage(msg);
		}
	}
	//add nfc

    @Override
    protected void onPause() {
        super.onPause();
        //add nfc
        if(mNfcAdapter!=null){
			mNfcAdapter.disableForegroundDispatch (this);
		}
		closeNfcStatus();
		//add nfc
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //add nfc
        unregisterReceiver(shutDownBroadcastReceiver);
        //add nfc
        mLightSensorManager.unregisterListener(mLightSensorListener);
        if(mHandler!=null&&gSensorRunnable!=null){
        mHandler.removeCallbacks(gSensorRunnable);
        }
        if(sensorMgr!=null&&sensorListener!=null){
        sensorMgr.unregisterListener(sensorListener);
        }
    }
}
