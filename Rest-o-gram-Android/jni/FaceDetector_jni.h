#include <jni.h>

#ifndef _Included_rest_o_gram_FaceDetector
#define _Included_rest_o_gram_FaceDetector
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_rest_o_gram_openCV_OpenCVFaceDetector_nativeLoadClassifier
    (JNIEnv * jenv, jclass, jstring jFileName, jint faceSize);

JNIEXPORT void JNICALL Java_rest_o_gram_openCV_OpenCVFaceDetector_nativeDetectFaces
    (JNIEnv * jenv, jclass, jlong imageGray, jlong faces, jlong minSize, jlong maxSize);

#ifdef __cplusplus
}
#endif
#endif
