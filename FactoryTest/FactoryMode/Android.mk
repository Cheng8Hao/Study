LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := mediatek-framework telephony-common mediatek-common
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := FactoryMode
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_SRC_FILES := $(call all-java-files-under, src)\
		src/com/mediatek/fmradio/FMInterface.aidl     #jiangcunbin add for FM in *906# 2015.05.15
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_FLAG_FILES := proguard-project.txt
#LOCAL_AAPT_FLAGS := --auto-add-overlay
#LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat:android.support.v7.widget:android.support.design:android.support.constraint

LOCAL_STATIC_ANDROID_LIBRARIES := \
                android-support-v4 \
                android-support-v7-appcompat \

LOCAL_JNI_SHARED_LIBRARIES := libfmxjni_sagereal

LOCAL_STATIC_JAVA_LIBRARIES := zxing \
		android-support-v7-appcompat \
		android-support-design \
		vendor.mediatek.hardware.nvram-V1.0-java \
		
#如果要预置进去可卸载,需要添加以下这行 
#LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := zxing:libs/zxing.jar
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))


