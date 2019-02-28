package com.dyc.factorymode;

import android.util.Log;

import com.dyc.factorymode.R;


public class TestItem {
    private int indexInAll;
    private int result;
    // add status for test item
    public static final int FAIL = 2;
    public static final int SUCCESS = 1;
    public static final int DEFAULT = 0;
    //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 begin
    public static final int[] ALL_TEST_ITEM_STRID1 = {
            R.string.test_usb,
            R.string.test_imei,
            R.string.test_ctp,
            R.string.test_sensor,
            R.string.test_compass,
            R.string.test_audio,
            R.string.test_refaudio,
            R.string.test_headset,
            R.string.test_fm,
            R.string.test_loud,
            R.string.test_lcd,
            R.string.test_camera,
            R.string.test_rec,
            R.string.test_gps,
            R.string.test_key,
            R.string.test_fingerprint,
            R.string.other_test

    };

    public static final int[] ALL_TEST_ITEM_STRID2 = {
            R.string.test_usb,
            R.string.test_imei,
            R.string.test_ctp,
            R.string.test_sensor,
            R.string.test_audio,
            R.string.test_refaudio,
            R.string.test_headset,
            R.string.test_fm,
            R.string.test_loud,
            R.string.test_lcd,
            R.string.test_camera,
            R.string.test_rec,
            R.string.test_gps,
            R.string.test_key,
            R.string.test_fingerprint,
            R.string.other_test

    };
    public static final int[] ALL_TEST_ITEM_STRID3 = {
            R.string.test_usb,
            R.string.test_imei,
            R.string.test_ctp,
            R.string.test_sensor,
            R.string.test_compass,
            R.string.test_audio,
            R.string.test_headset,
            R.string.test_fm,
            R.string.test_loud,
            R.string.test_lcd,
            R.string.test_camera,
            R.string.test_rec,
            R.string.test_gps,
            R.string.test_key,
            R.string.test_fingerprint,
            R.string.other_test
    };

    public static final int[] ALL_TEST_ITEM_STRID4 = {
            R.string.test_usb,
            R.string.test_imei,
            R.string.test_ctp,
            R.string.test_sensor,
            R.string.test_audio,
            R.string.test_headset,
            R.string.test_fm,
            R.string.test_loud,
            R.string.test_lcd,
            R.string.test_camera,
            R.string.test_rec,
            R.string.test_gps,
            R.string.test_key,
            R.string.test_fingerprint,
            R.string.other_test

    };
    //Redmine165183  chenghao add for factorytest fingerprinttest 2019-02-23 end
    public TestItem(int indexInAll) {
        this.indexInAll = indexInAll;
    }

    public int getTestTitle() {
        if (!FeatureOption.MTK_DUAL_MIC_SUPPORT && !FeatureOption.SAGEREAL_FACTORYTEST_COMPASS) {
            return ALL_TEST_ITEM_STRID4[indexInAll];
        } else if(!FeatureOption.MTK_DUAL_MIC_SUPPORT) {
            return ALL_TEST_ITEM_STRID3[indexInAll];
        }else if(!FeatureOption.SAGEREAL_FACTORYTEST_COMPASS){
            return ALL_TEST_ITEM_STRID2[indexInAll];
        }else{
            return ALL_TEST_ITEM_STRID1[indexInAll];
        }
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
