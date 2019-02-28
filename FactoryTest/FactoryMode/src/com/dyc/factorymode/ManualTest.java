package com.dyc.factorymode;

import com.dyc.factorymode.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.WindowManager;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.content.SharedPreferences;
import java.util.List;
import java.util.Arrays;
import android.view.KeyEvent;
import com.dyc.factorymode.fmradio.FmMainActivity;
import android.net.Uri;


public class ManualTest extends Activity {

    private static final String TAG = "ManualTest_Sagereal";
    private Context mContext;
    private ItemListViewAdapter mItemListViewAdapter;
    private ArrayList<TestItem> mItemsListView;
    private ListView mListViewItem;
    private TextView resultnumber;
    private int number = 0;
    private int count = 0;
    private int mLastTestItemIndex = 0;
    private int [] resultArr =  null ;
    private int usbs, tps, sensors, imeis, audios, headsets, fms, lcds, cameras, autos, recs, louds, keys,compasss,refaudios,dials,fingerprints;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        setContentView(R.layout.mylist_view);
        android.util.Log.d(TAG, "onCreate success");
        Root.getInstance().addActivity(this);
        MySharedPreferences();
        mContext = this;
        mListViewItem = (ListView) findViewById(R.id.list_test_view);
        resultnumber = (TextView) findViewById(R.id.result_number);
        mItemsListView = getSupportList();
        mItemListViewAdapter = new ItemListViewAdapter(this, mItemsListView);
        mListViewItem.setAdapter(mItemListViewAdapter);
        mListViewItem.setOnItemClickListener(new ListItemClickListener());
        updateview();
    }

    public static ArrayList<TestItem> getSupportList() {
        ArrayList<TestItem> supportArray = new ArrayList<TestItem>();
        int counts = 0;
        if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
            counts=TestItem.ALL_TEST_ITEM_STRID4.length - 1;
        } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
            counts=TestItem.ALL_TEST_ITEM_STRID3.length - 1;
        }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
            counts=TestItem.ALL_TEST_ITEM_STRID2.length - 1;
        }else{
            counts=TestItem.ALL_TEST_ITEM_STRID1.length - 1;
        }
        for (int i = 0; i < counts; i++) {
            TestItem item = new TestItem(i);
            item.setResult(TestItem.DEFAULT);
            supportArray.add(item);
        }
        return supportArray;
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

    protected void updateview() {
       if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
            count=TestItem.ALL_TEST_ITEM_STRID4.length - 1;
        } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
            count=TestItem.ALL_TEST_ITEM_STRID3.length - 1;
        }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
            count=TestItem.ALL_TEST_ITEM_STRID2.length - 1;
        }else{
            count=TestItem.ALL_TEST_ITEM_STRID1.length - 1;
        }
     number = 0;
     for (int i = 0; i < count; i++) {
            TestItem item = new TestItem(i);
            CheckResult(resultArr[i],i);
        }
    }
    private void CheckResult(int result ,int position){
        if (result == 1) {
            number = number + 1;
            mItemsListView.get(position).setResult(TestItem.SUCCESS);
        } else if (result == 2) {
            mItemsListView.get(position).setResult(TestItem.FAIL);
        }
    }

    private class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent localIntent;
            mLastTestItemIndex = position;
            int pos;
            if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
                pos=TestItem.ALL_TEST_ITEM_STRID4[position];
            } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
                pos=TestItem.ALL_TEST_ITEM_STRID3[position];
            }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
                pos=TestItem.ALL_TEST_ITEM_STRID2[position];
            }else{
                pos=TestItem.ALL_TEST_ITEM_STRID1[position];
            }
            if (!ButtonUtils.isFastDoubleClick(pos)) {
                switch (pos) {
                    case R.string.test_imei:
                        startActivity(new Intent(ManualTest.this, IMEITest.class));
                        break;
                    case R.string.test_usb:
                        startActivity(new Intent(ManualTest.this, UsbTest.class));
                        break;
                    case R.string.test_audio:
                        startActivity(new Intent(ManualTest.this, AudioLoopback.class));
                        break;
                    case R.string.test_headset:
                        startActivity(new Intent(ManualTest.this, HeadsetLoopback.class));
                        break;
                    case R.string.test_fm:
                        startActivity(new Intent(ManualTest.this, FmMainActivity.class));
                        break;
                    case R.string.test_loud:
                        startActivity(new Intent(ManualTest.this, LoudSpeaker.class));
                        break;
                    case R.string.test_ctp:
                        startActivity(new Intent(ManualTest.this, MainActivity.class));
                        break;
                    case R.string.test_sensor:
                        startActivity(new Intent(ManualTest.this, SensorTest.class));
                        break;
                    case R.string.test_lcd:
                        startActivity(new Intent(ManualTest.this, TestLCD.class));
                        break;
                    case R.string.test_camera:
                        Intent intent10 = new Intent(ManualTest.this, CameraTest.class);
                        startActivity(intent10);
                        break;
                    case R.string.test_rec:
                        startActivity(new Intent(ManualTest.this, RecTest.class));
                        break;
                    case R.string.test_gps:
                        startActivity(new Intent(ManualTest.this, AutoTest.class));
                        break;
                    case R.string.test_key:
                        startActivity(new Intent(ManualTest.this, KeyTestActivity.class));
                        break;
                    case R.string.test_tp:
                        startActivity(new Intent(ManualTest.this, MainActivity.class));
                        break;
                    case R.string.test_refaudio:
                        startActivity(new Intent(ManualTest.this, RefAudioLoopback.class));
                        break;
                    case R.string.test_compass:
                        startActivity(new Intent(ManualTest.this, CompassTest.class));
                        break;
                    case R.string.test_call:
                        startActivity(new Intent(ManualTest.this, CallTest.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.string.test_fingerprint:
                        startActivity(new Intent(ManualTest.this, FingerprintTestActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private class ItemListViewAdapter extends BaseAdapter {

        private ArrayList<TestItem> mItemList;
        private LayoutInflater mInflater;
        private Context mContext;

        public ItemListViewAdapter(Context c, ArrayList<TestItem> mItemsListView) {
            mItemList = mItemsListView;
            mContext = c;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mItemList != null) {
                return mItemList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            TestItem item = mItemList.get(position);
            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_btn, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.listitem_text);
                viewHolder.textView1 = (TextView) view.findViewById(R.id.textView1);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            String signbol = Integer.toString(position + 1);
            viewHolder.textView1.setText(signbol);
            viewHolder.textView.setText(item.getTestTitle());

            if (item.getResult() == TestItem.SUCCESS) {
                viewHolder.textView.setTextColor(Color.GREEN);
            } else if (item.getResult() == TestItem.FAIL) {
                viewHolder.textView.setTextColor(Color.RED);
            } else {
                viewHolder.textView.setTextColor(Color.WHITE);
            }
            return view;
        }
    }

    class ViewHolder {
        TextView textView;
        TextView textView1;
    }

    @Override
    protected void onResume() {
        MySharedPreferences();
        MyUpdateLanguage.updateLanguage(this,Root.getInstance().isChinese);
        updateview();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mItemListViewAdapter.notifyDataSetChanged();
        resultnumber.setText(Integer.toString(number) +"/"+count);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ManualTest.this, TestMainActivity.class);
            startActivity(intent);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
