package com.dyc.factorymode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;

public class FeatureOption {

	public final static boolean MTK_DUAL_MIC_SUPPORT =SystemProperties.get("ro.mtk_dual_mic_support").equals("1");
	public final static boolean MTK_HALL_SUPPORT = SystemProperties.get("ro.mtk_hall_support").equals("1");
	public final static boolean MTK_GPS_SUPPORT = SystemProperties.get("ro.vendor.mtk_gps_support").equals("1");//ro.mtk_gps_support
	public final static boolean MTK_GEMINI_SUPPORT = SystemProperties.get("ro.mtk_gemini_support").equals("1");
	public final static boolean MTK_2SDCARD_SWAP = SystemProperties.get("ro.mtk_2sdcard_swap").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_VERSION = SystemProperties.get("ro.sr_ftest_version").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_LED = SystemProperties.get("ro.sr_ftest_led").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_GSENSOR = SystemProperties.get("ro.sr_ftest_gsensor").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_PROXSENSOR = SystemProperties.get("ro.sr_ftest_psensor").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_FRONTCAMERA = SystemProperties.get("ro.sr_ftest_fcamera").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_SMARTPA=SystemProperties.get("ro.sr_ftest_smartpa").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_COMPASS = SystemProperties.get("ro.sr_ftest_compass").equals("1");
	public final static boolean SAGEREAL_COMPASS_SHOW_ANIMATION = false;
	public final static boolean SAGEREAL_FACTORYTEST_GYROSCOPE = SystemProperties.get("ro.sr_ftest_gyroscope").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_CAMERA_FRONT_LED = SystemProperties.get("ro.sr_ftest_camera_front_led").equals("1");
	public final static boolean MTK_EMMC_SUPPORT_OTP = SystemProperties.get("ro.sr_ftest_otp_support").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_NFC= SystemProperties.get("ro.sr_ftest_nfc").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_LIGHT_SENSOR= SystemProperties.get("ro.sr_ftest_lightsensor").equals("1");


/*
	public final static boolean MTK_DUAL_MIC_SUPPORT = true;
	public final static boolean MTK_HALL_SUPPORT = false;
	public final static boolean MTK_GPS_SUPPORT = true;
	public final static boolean MTK_GEMINI_SUPPORT = true;
	public final static boolean MTK_2SDCARD_SWAP = true;
	public final static boolean SAGEREAL_FACTORYTEST_VERSION = false;
	public final static boolean SAGEREAL_FACTORYTEST_LED = true;
	public final static boolean SAGEREAL_FACTORYTEST_GSENSOR = SystemProperties.get("ro.sr_ftest_gsensor").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_PROXSENSOR = SystemProperties.get("ro.sr_ftest_psensor").equals("1");
	public final static boolean SAGEREAL_FACTORYTEST_FRONTCAMERA = true;
	public final static boolean SAGEREAL_FACTORYTEST_GYROSCOPE = true;
	public final static boolean SAGEREAL_FACTORYTEST_COMPASS = true;
	public final static boolean SAGEREAL_COMPASS_SHOW_ANIMATION = false;
	public final static boolean SAGEREAL_FACTORYTEST_CAMERA_FRONT_LED = true;
	public final static boolean MTK_EMMC_SUPPORT_OTP = true;
	public final static boolean SAGEREAL_FACTORYTEST_NFC= false;
*/

}
