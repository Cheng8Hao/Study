package com.dyc.factorymode;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.content.pm.ActivityInfo;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import android.os.Handler;
import android.graphics.Point;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;
import android.media.SoundPool;
import android.media.AudioManager;
import java.io.FileNotFoundException;
import android.os.SystemProperties;

public class CameraTest extends Activity implements Callback, OnClickListener {

    private final static String TAG = "CameraTest_Sagereal";
    private android.hardware.Camera camera;
    private SurfaceView mpreview;
    private SurfaceHolder mSurfaceHolder;
    private Uri mUri;//照片保存路径
    /**
     * 创建文件名
     **/
    String mFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    public static boolean isFront = false;
    public static boolean istake = false;
    private Button nextButton;
    private Button mfail;
    private ImageView takeButton;
    private ImageView thumbsView;
    private ImageView picpreview;
    private Point p;
    private static String backStr = "";
    private static String frontStr = "";
    private static int viewHight = 0;
    private static int viewWidth = 0;
    private Resources res;
    private static String SUB_CAMERA_LED_MODE_OFF = "off";
    private static String SUB_CAMERA_LED_MODE_LOW = "low";
    private static String SUB_CAMERA_LED_MODE_HIGH = "high";
    private int testmode;
    private TextView remindcamera;
    private SoundPool soundPool;//添加拍照点击音效
    private int soundID;
    public static boolean isFirstFront = false; 
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        Root.getInstance().haveFrontCamera = FeatureOption.SAGEREAL_FACTORYTEST_FRONTCAMERA;
        android.util.Log.d("dyc_camera111","haveFrontCamera = "+SystemProperties.get("ro.sr_ftest_fcamera"));
        android.util.Log.d("dyc_camera","haveFrontCamera = "+Root.getInstance().haveFrontCamera);
        Root.getInstance().haveCameraFrontLed = FeatureOption.SAGEREAL_FACTORYTEST_CAMERA_FRONT_LED;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_test);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        initSound();
        res = getResources();
        flag= res.getString(R.string.remind_front_camera_tv);
        backStr = res.getString(R.string.backStr);
        frontStr = res.getString(R.string.frontStr);
        remindcamera = (TextView) findViewById(R.id.remind_camera);
        nextButton = (Button) findViewById(R.id.camera_success_btn);
        mfail = (Button) findViewById(R.id.camera_fail_btn);
        SharedPreferences result = getSharedPreferences("testmode", Context.MODE_PRIVATE);
        testmode = result.getInt("isautotest", 0);
        nextButton.setOnClickListener(this);
        mfail.setOnClickListener(this);
        takeButton = (ImageView) findViewById(R.id.takepickture);
        takeButton.setOnClickListener(this);
        thumbsView = (ImageView) findViewById(R.id.thumbsView);
        picpreview = (ImageView) findViewById(R.id.picture_preview);
        thumbsView.setOnClickListener(this);
        picpreview.setOnClickListener(this);
        thumbsView.setEnabled(false);
        BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!Root.getInstance().openBt) {
            mBluetooth.disable();
        }
        mpreview = (SurfaceView) this.findViewById(R.id.camera_preview);
        WindowManager wmManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        p = new Point();
        wmManager.getDefaultDisplay().getSize(p);
        ViewGroup.LayoutParams lp = mpreview.getLayoutParams();
        int tempHeight = p.y - nextButton.getHeight();
        if (tempHeight / 4 < p.x / 3) {   //根据设备屏幕的长宽比，确定预览窗口的大小
            lp.height = p.y;
            lp.width = (int) lp.height * 3 / 4;
        } else {
            lp.width = p.x;
            lp.height = lp.width * 4 / 3;
        }
        mpreview.setLayoutParams(lp);
        picpreview.setLayoutParams(lp);//照片大小设置为预览界面大小
        mSurfaceHolder = mpreview.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Toast toast = Toast.makeText(this, res.getString(R.string.flash_light), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
        toast.show();
    }

    private void initSound() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(CameraTest.this, R.raw.cameraclick, 1);
    }

    private void playSound() {
        soundPool.play(
                soundID, 1.0f, 1.0f, 0, 0, 1
        );
    }

    private static Camera.Size getNear(List<Camera.Size> list, int target) {
        if (list.size() == 0) {
            try {
                throw new Exception();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                android.util.Log.d("xie", "本设备不支持4：3预览拍照");
            }
        }
        if (list.size() == 1) return list.get(0);
        int min = 0;
        int max = list.size() - 1;
        int mid = (int) (min + max) / 2;

        while (max - min > 1) {
            if (target == list.get(mid).height) {
                return list.get(mid);
            } else if (target < list.get(mid).height) {
                max = mid;

            } else if (target > list.get(mid).height) {
                min = mid;
            }
            mid = (int) (min + max) / 2;
        }
        if (list.get(max).height - target < target - list.get(min).height) {
            return list.get(max);
        } else {
            return list.get(min);
        }
    }

    private void initCamera(int face) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestPreviewSize = supportPreviewSizes.get(0);
        for (int i = 0; i < supportPreviewSizes.size(); i++) {
            if ((float) supportPreviewSizes.get(i).width / supportPreviewSizes.get(i).height == 4.0f / 3) {
                bestPreviewSize = supportPreviewSizes.get(i);
            }
        }
        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        List<Camera.Size> pictureList = new ArrayList<Camera.Size>();
        for (int i = 0; i < supportedPictureSizes.size(); i++) {

            if ((float) supportedPictureSizes.get(i).width / supportedPictureSizes.get(i).height == 4.0f / 3) {
                pictureList.add(supportedPictureSizes.get(i));
            }
        }
        Camera.Size result = getNear(pictureList, p.x);
        parameters.setPictureSize(result.width, result.height);
        parameters.setPictureFormat(PixelFormat.JPEG);
        camera.setParameters(parameters);
        // 开始预览
        camera.startPreview();
        camera.cancelAutoFocus();//add to auto focus 2018.11.05
        String str = (String) nextButton.getText();
        android.util.Log.d("jiangcunbin112", "str = " + str);
        if (face == CAMERA_FACING_FRONT) {
            //parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            if (Root.getInstance().haveCameraFrontLed) {
                //setSubCameraLed(SUB_CAMERA_LED_MODE_HIGH);
            }
        } else if (face == CAMERA_FACING_BACK) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        }
        List<String> focusModes = parameters.getSupportedFocusModes();
        for (int i = 0; i < focusModes.size(); i++) {
            android.util.Log.d("jiangcunbin", "focusModes =" + focusModes.get(i));
        }
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {

            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        }
        camera.setParameters(parameters);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        android.util.Log.d(TAG, "init camera");
        if (camera != null) {
            if (!isFirstFront){
                initCamera(CAMERA_FACING_BACK);
            } else {
                if (camera != null){
                    camera.release();
                    camera = null;
                }
                openCamera(CAMERA_FACING_FRONT);
            }
            camera.autoFocus(new AutoFocusCallback() {
                public void onAutoFocus(boolean isSuccess, Camera camera) {
                    if (isSuccess && camera != null) {
                        android.util.Log.d(TAG, "AtuoFocus Success!");
                        camera.cancelAutoFocus();//add to auto focus 2018.11.05
                        //Toast.makeText(CameraTest.this, "auto focus", Toast.LENGTH_SHORT).show();//相机自动对焦
                    }
                }
            });
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 打开后置摄像头
        try {
            // TODO camera驱动挂掉,处理??
            camera = Camera.open(CAMERA_FACING_BACK);
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            Root.getInstance().isCamera = false;
            if (null != camera) {
                camera.release();
            }
            camera = null;
        }

        if (null == camera) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(res.getString(R.string.warn));
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(res.getString(R.string.camera_error));
            builder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            return;
                        }
                    });
            builder.show();
            mpreview.setVisibility(View.GONE);
        } else {

            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int cameraCount = 0;
        final CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        android.util.Log.d(TAG, "cameraCount= " + cameraCount);
        if (!ButtonUtils.isFastDoubleClick(v.getId())) {
        switch (v.getId()) {
            case R.id.camera_success_btn:
                String str = (String) nextButton.getText();
                picpreview.setImageBitmap(null);
                Outpreview();
                if ((Root.getInstance().haveFrontCamera) && (str.equals(res.getString(R.string.test_lcd_camera)))) {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                    remindcamera.setText(res.getString(R.string.remind_front_camera_tv));
                    openCamera(CAMERA_FACING_FRONT);
                    takeButton.setEnabled(true);
                    android.util.Log.d(TAG, "facing= " + cameraInfo.facing);
                    nextButton.setText(res.getString(R.string.test_success));
                    nextButton.setTextColor(Color.GREEN);
                    nextButton.setEnabled(false);
                    isFront = true;
                    istake = false;
                    isFirstFront = true;
                    thumbsView.setVisibility(View.GONE);
                    picpreview.setVisibility(View.GONE);
                } else {
                    takeButton.setEnabled(true);
                    if (!isFront) {
                        nextButton.setEnabled(true);
                    }
                    soundPool.release();
                    StartUtil.StartNextActivity("Camera",1,testmode,CameraTest.this,RecTest.class);
                }
                break;
            case R.id.camera_fail_btn:
                    soundPool.release();
                    StartUtil.StartNextActivity("Camera",2,testmode,CameraTest.this,RecTest.class);
                break;
            case R.id.takepickture:
                takeButton.setEnabled(false);
                if (!isFront) {
                    nextButton.setEnabled(false);
                }
                if (!istake) {
                    nextButton.setEnabled(true);
                    istake = true;
                }
                playSound();
                thumbsView.setEnabled(true);
                picpreview.setVisibility(View.VISIBLE);
                thumbsView.setVisibility(View.GONE);
                camera.takePicture(null, null, pictureCallback);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(2 * 1000);
                            mHandler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.thumbsView:
                picpreview.setVisibility(View.VISIBLE);
                thumbsView.setVisibility(View.GONE);
                takeButton.setEnabled(false);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                    picpreview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                /*try {
                    Intent intentview = new Intent(Intent.ACTION_VIEW);
                    intentview.setPackage("com.android.gallery3d");
                    intentview.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentview.setDataAndType(mUri, "image/*");
                    startActivity(intentview);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intentview = new Intent(Intent.ACTION_VIEW);
                    intentview.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentview.setDataAndType(mUri, "image/*");
                    startActivity(intentview);
                }*/
                break;
             case R.id.picture_preview:
                 picpreview.setImageBitmap(null);
                 Outpreview();
                break;
            default:
                break;
            }
        }
    }

    //照片回调
    PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            File mMediaFileDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
            if (!mMediaFileDir.exists()) {
                mMediaFileDir.mkdir();
            }
            File mPictureFile = new File(mMediaFileDir.getPath() + File.separator + "IMG_" + mFileName + ".jpg");
            if (mPictureFile == null) {
                return;
            }
            try {
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);   // 获得图片
                Matrix matrix = new Matrix();
                if (isFront) {
                    //matrix.setRotate(270,bm.getWidth()/2,bm.getHeight()/2);
                    matrix.setScale(-1, 1);
                    matrix.postTranslate(0,0);
                    matrix.postRotate(270);
                } else {
                    matrix.postRotate(90);
                }
                Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                thumbsView.setImageBitmap(bm1);
                picpreview.setImageBitmap(bm1);
                camera.stopPreview();
                camera.startPreview();//拍完照继续预览
                
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mPictureFile));
                bm1.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                bos.flush();//输出
                bos.close();//关闭
                mUri = Uri.fromFile(mPictureFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            //takeButton.setEnabled(true);
            if (!isFront) {
                nextButton.setEnabled(true);
            }
        }
    };
    Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(500);
                mHandler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void openCamera(int face) {
        try {
            camera = Camera.open(face);
            camera.setDisplayOrientation(270);
            camera.setPreviewDisplay(mSurfaceHolder);
            initCamera(face);

        } catch (IOException exception) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
    }
    
    private void Outpreview() {
        picpreview.setVisibility(View.GONE);
        thumbsView.setVisibility(View.VISIBLE);
        takeButton.setEnabled(true);
   }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!Root.getInstance().haveFrontCamera) {
            nextButton.setText(res.getString(R.string.test_success));
            nextButton.setTextColor(Color.GREEN);
            isFront = false;
        } else {
            nextButton.setText(res.getString(R.string.test_lcd_camera));
            if(remindcamera.getText()==flag){
            nextButton.setText(res.getString(R.string.test_success));
            }
            else {
                isFront = false;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
             Outpreview();
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
    
    protected void onDestroy(){
        soundPool.release();
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        isFirstFront = false;
    }

    private void setSubCameraLed(String state) {
        Intent intent = new Intent("sagereal.intent.action.sub_camera_led");
        intent.putExtra("sub_camera_led", state);
        sendBroadcast(intent);
        if (!state.equals(SUB_CAMERA_LED_MODE_OFF)) {
            Toast toast = Toast.makeText(this, R.string.sub_camera_led_light, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
            toast.show();
        }
    }

}
