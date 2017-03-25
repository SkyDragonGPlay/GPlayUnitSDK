#include "UnitSDKObject.h"
#include "utils/JniHelper.h"
#include "utils/Utils.h"

#include <android/log.h>

#define  LOG_TAG    "UnitSDKObject"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace std;
namespace gplay {namespace framework {
    
extern "C" {

    JNIEXPORT void JNICALL Java_com_skydragon_gplay_unitsdk_nativewrapper_NativeWrapper_nativeAsyncActionResult(JNIEnv* env, jobject thiz, jint ret, jstring msg, jstring callbackId) {
        string strMsg = JniHelper::jstring2string(msg);
        string strCallbackId = JniHelper::jstring2string(callbackId);

        UnitSDK* pUnitSDK = UnitSDK::getInstance();
        if (pUnitSDK != NULL) {
            UnitSDKObject* pObject = static_cast<UnitSDKObject*>(pUnitSDK);

            if (pObject != NULL) {
                ActionResultListener* listener = pObject->getAsyncActionResultListener();
                if (NULL != listener) {
                    map<string, string> infoMap;
                    infoMap["callbackId"] = strCallbackId;

                    // paying 回调的时候设为 false
                    if (UnitSDKObject::_payingCallbacks[strCallbackId] == "1") {
                        UnitSDK::_paying = false; 
                        UnitSDKObject::_payingCallbacks.clear();
                    }

                    listener->onActionResult(pObject, (ActionResultCode) ret, strMsg.c_str(), infoMap);
                } else {
                    LOGE("asyncActionResultListener of UnitSDK not set correctly");
                }
            }
        } else {
            LOGE("UnitSDKObject is null");
        }
    }

    JNIEXPORT void JNICALL Java_com_skydragon_gplay_unitsdk_nativewrapper_NativeWrapper_nativeOutputLog(JNIEnv*  env, jobject thiz, jint type, jstring tag, jstring msg) {
        string stag = JniHelper::jstring2string(tag);
        string smsg = JniHelper::jstring2string(msg);
        Utils::outputLog((int)type, stag.c_str() ,smsg.c_str());
    }
}

bool UnitSDK::_paying = false;
UnitSDK* UnitSDK::pUnitSDK = NULL;

map<string, string> UnitSDKObject::_curInfo;
map<string, string> UnitSDKObject::_payingCallbacks;

jobject UnitSDKObject::jGlobalProxyObj = NULL;
const string UnitSDKObject::jProxyClass("com/skydragon/gplay/unitsdk/framework/UnitSDK");

UnitSDK* UnitSDK::getInstance() {
    if( NULL != pUnitSDK ) {
        return pUnitSDK;
    }
    else {
        pUnitSDK = static_cast<UnitSDK*>(new UnitSDKObject());
        return pUnitSDK;
    }
}

UnitSDKObject::UnitSDKObject()
:pAsyncActionResultListener(NULL)
{
}
    
UnitSDKObject::~UnitSDKObject() {
    JNIEnv* env = JniHelper::getEnv();
    env->DeleteLocalRef(jGlobalProxyObj);
}

void UnitSDKObject::init(string appKey,string appSecret,string privateKey) {
    LOGD("UnitSDKObject::init");
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "initUnitSDKParams"
            , "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring strAppKey = JniHelper::newStringUTF(t.env, appKey);
        jstring strAppSecret = JniHelper::newStringUTF(t.env, appSecret);
        jstring strPrivateKey = JniHelper::newStringUTF(t.env, privateKey);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, strAppKey, strAppSecret, strPrivateKey);

        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(strAppSecret);
        t.env->DeleteLocalRef(strAppKey);
        t.env->DeleteLocalRef(strPrivateKey);
    }
}

string UnitSDKObject::getChannelId() {
    Utils::outputLog(ANDROID_LOG_DEBUG,"UnitSDKObject", "Try to get channelid");
    JniMethodInfo t;
    string ret;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "getChannelId"
            , "()Ljava/lang/String;"))
    {
        jstring jstrRet = (jstring)t.env->CallStaticObjectMethod(t.classID,t.methodID);
        string ret = JniHelper::jstring2string(jstrRet);
        t.env->DeleteLocalRef(t.classID);
        return ret;
    }
    return ret;
}

bool UnitSDKObject::isFunctionSupported(string functionName) {
    return callSyncStringFunc("isFunctionSupported", functionName) == "true";
}

void UnitSDKObject::login(string callbackId) {
    LOGD("UnitSDKObject::login()");
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "login"
            , "(Ljava/lang/String;)V"))
    {
        jstring strCallbackId = JniHelper::newStringUTF(t.env, callbackId);
        
        t.env->CallStaticVoidMethod(t.classID,t.methodID, strCallbackId);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(strCallbackId);
    }
}

bool UnitSDKObject::isLogined() {
    JniMethodInfo t;
    bool ret = false;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "isLogin"
            , "()Z"))
    {
        ret = t.env->CallStaticBooleanMethod(t.classID,t.methodID);
        t.env->DeleteLocalRef(t.classID);
    }
    return ret;
}

string UnitSDKObject::getUserID() {
    JniMethodInfo t;
    string ret;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "getUserID"
            , "()Ljava/lang/String;"))
    {
        jstring jstrRet = (jstring)t.env->CallStaticObjectMethod(t.classID,t.methodID);
        string ret = JniHelper::jstring2string(jstrRet);
        t.env->DeleteLocalRef(t.classID);
        return ret;
    }
    return ret;
}

void UnitSDKObject::payForProduct(string callbackId, string info) {
    if (UnitSDK::_paying) {
        Utils::outputLog(ANDROID_LOG_DEBUG, "UnitSDK", "Now is paying");
        onAsyncActionResult(callbackId, PAY_RESULT_NOW_PAYING, "Now is paying");
        return;
    }

    if (info.empty()) {
        onAsyncActionResult(callbackId, PAY_RESULT_FAIL, "Product info error");;
        Utils::outputLog(ANDROID_LOG_ERROR, "UnitSDK", "The product info is empty!");
        return;
    }
    else {
        UnitSDKObject::_payingCallbacks[callbackId] = "1";
        UnitSDK::_paying = true;
        _curInfo["pay_info"] = info;

        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t
                , jProxyClass.c_str()
                , "pay"
                , "(Ljava/lang/String;Ljava/lang/String;)V"))
        {
            jstring strCallbackId = JniHelper::newStringUTF(t.env, callbackId);
            jstring strProductInfo = JniHelper::newStringUTF(t.env, info);

            // invoke java method
            t.env->CallStaticVoidMethod(t.classID, t.methodID, strCallbackId, strProductInfo);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(strCallbackId);
            t.env->DeleteLocalRef(strProductInfo);
        }
    }
}

string UnitSDKObject::getOrderId() {
    JniMethodInfo t;
    string ret;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "getOrderId"
            , "()Ljava/lang/String;"))
    {
        jstring jstrRet = (jstring)t.env->CallStaticObjectMethod(t.classID,t.methodID);
        string ret = JniHelper::jstring2string(jstrRet);
        t.env->DeleteLocalRef(t.classID);
        return ret;
    }
    return ret;
}

void UnitSDKObject::share(string callbackId, string info) {
    if (info.empty()) {
        onAsyncActionResult(callbackId, SHARE_RESULT_FAIL, "Share info error");
        
        Utils::outputLog(ANDROID_LOG_DEBUG,"UnitSDK", "The Share info is empty!");
        return;
    }
    else {
        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t
                , jProxyClass.c_str()
                , "share"
                , "(Ljava/lang/String;Ljava/lang/String;)V"))
        {
            
            jstring strCallbackId = JniHelper::newStringUTF(t.env, callbackId);
            jstring strShareInfo = JniHelper::newStringUTF(t.env, info);

            // invoke java method
            t.env->CallStaticVoidMethod(t.classID, t.methodID, strCallbackId, strShareInfo);
            
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(strCallbackId);
            t.env->DeleteLocalRef(strShareInfo);
        }
    }
}

void UnitSDKObject::createShortCut(string callbackId) {
    LOGD("UnitSDKObject::createShortCut()");
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t
            , jProxyClass.c_str()
            , "createShortcut"
            , "(Ljava/lang/String;)V"))
    {
        jstring strCallbackId = JniHelper::newStringUTF(t.env, callbackId);

        // invoke java method
        t.env->CallStaticVoidMethod(t.classID, t.methodID, strCallbackId);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(strCallbackId);
    }
}

    
// ======= handle listener ===========
    
void UnitSDKObject::setAsyncActionResultListener(ActionResultListener* listener) {
    LOGD("UnitSDKObject::setAsyncActionResultListener");
    if (NULL==listener)
        LOGE("input ActionResultListener is null");
    pAsyncActionResultListener = listener;
}
    
ActionResultListener* UnitSDKObject::getAsyncActionResultListener() {
    if (NULL==pAsyncActionResultListener)
        LOGE("pAsyncActionResultListener is null");
    return pAsyncActionResultListener;
}
    
    
// ============ unitSdk 通用扩展接口 ==============
    
string UnitSDKObject::callSyncStringFunc(string funcName, string jsonParams) {
    return callJavaSyncStringFunc(funcName, jsonParams);
}

void UnitSDKObject::callAsyncFunc(string callbackId, string funcName, string jsonParams) {
    callJavaAsyncFunc(callbackId, funcName, jsonParams);
}
    
    
// ========== 调用 java 通用扩展接口 ==========
string UnitSDKObject::callJavaSyncStringFunc(string funcName, string jsonParams) {
    if(funcName.empty()) return "";

    JNIEnv* env = JniHelper::getEnv();
    jstring jfunc = JniHelper::newStringUTF(env, funcName.c_str());
    jstring jparams = JniHelper::newStringUTF(env, jsonParams.c_str());

    string jfuncName = "callSyncFunction";
    string paramsCode = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    string ret = "";

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, jProxyClass.c_str(), jfuncName.c_str(), paramsCode.c_str())) {
        jstring jstrRet = (jstring)env->CallStaticObjectMethod(t.classID, t.methodID, jfunc, jparams);
        ret = JniHelper::jstring2string(jstrRet);
        t.env->DeleteLocalRef(t.classID);
    }
    env->DeleteLocalRef(jfunc);
    env->DeleteLocalRef(jparams);
    return ret;
}
    
void UnitSDKObject::callJavaAsyncFunc(string callbackId, string funcName, string jsonParams) {
    if(funcName.empty()) return;

    JNIEnv* env = JniHelper::getEnv();
    jstring jfunc = JniHelper::newStringUTF(env, funcName.c_str());
    jstring jparams = JniHelper::newStringUTF(env, jsonParams.c_str());
    jstring jcallbackid = JniHelper::newStringUTF(env, callbackId.c_str());

    string jfuncName = "callAsynFunctionFromNative";
    string paramsCode = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, jProxyClass.c_str(), jfuncName.c_str(), paramsCode.c_str())) {
        env->CallStaticVoidMethod(t.classID, t.methodID, jfunc, jparams, jcallbackid);
        t.env->DeleteLocalRef(t.classID);
    }
    env->DeleteLocalRef(jfunc);
    env->DeleteLocalRef(jparams);
}
//=====================================

void UnitSDKObject::onAsyncActionResult(string callbackId, ActionResultCode ret, const char* msg) {
    LOGD("UnitSDKObject::onAsyncActionResult");

    if (pAsyncActionResultListener) {
        _curInfo["callbackId"] = callbackId;
        pAsyncActionResultListener->onActionResult(this, ret, msg, _curInfo);
        _curInfo.clear();
    }
    
    LOGD("result is : %d(%s)", (int) ret, msg);
}

jmethodID UnitSDKObject::getJProxyCallSyncFunctionMethodID() {
    string funcName = "callSyncFunction";
    string paramsCode = "(Ljava/lang/String;Ljava/util/List;)Ljava/lang/Object;";
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, jProxyClass.c_str(), funcName.c_str(), paramsCode.c_str())) {
        t.env->DeleteLocalRef(t.classID);
        return t.methodID;
    }
    return NULL;
}

jmethodID UnitSDKObject::getJProxyCallAsynFunctionMethodID() {
    string funcName = "callAsynFunction";
    string paramsCode = "(Ljava/lang/String;Ljava/util/List;)Ljava/lang/Object;";
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, jProxyClass.c_str(), funcName.c_str(), paramsCode.c_str())) {
        t.env->DeleteLocalRef(t.classID);
        return t.methodID;
    }
    return NULL;
}
    
}} // namespace gplay::framework::