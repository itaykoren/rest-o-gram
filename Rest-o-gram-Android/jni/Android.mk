LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=SHARED
include ../../OpenCV-2.4.6-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE     := face_detector
LOCAL_SRC_FILES  := FaceDetector_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

include $(BUILD_SHARED_LIBRARY)