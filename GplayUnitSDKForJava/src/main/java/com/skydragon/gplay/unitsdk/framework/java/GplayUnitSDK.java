package com.skydragon.gplay.unitsdk.framework.java;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class GplayUnitSDK {

    private static final String TAG = "GplayUnitSDK";

    private static final int TYPE_USER = 1;
    private static final int TYPE_IAP = 2;
    private static final int TYPE_SHARE = 4;
    private static final int TYPE_SHORTCUT = 8;

    private static Activity mActivity;
    private static GplayUnitSDK _instance;

    private static Map<String, UnitSDKListener> _listenerMap = new HashMap<String, UnitSDKListener>();

    private GplayUnitSDK() {
        System.loadLibrary("UnitSDK");
    }

    public static GplayUnitSDK getInstance() {
        if (null == _instance) {
            _instance = new GplayUnitSDK();
        }
        return _instance;
    }

    /**
     * 初始化 unitSDK, 设置渠道配置信息
     * 
     * @param activity  安卓层接入 SDK 需要传入游戏 Activity. 如 cocos2dActivity
     * @param appKey    统一SDK分配
     * @param appSecret 统一SDK分配
     * @param privateKey 统一SDK分配
     */
    public void init(Activity activity,
                     String appKey,
                     String appSecret,
                     String privateKey)
    {
        mActivity = activity;
        nativeInit(appKey, appSecret, privateKey);
        _listenerMap.clear();
    }

    /**
     * APIs
     * 通过 JNI 间接调用到 UnitSDK 的响应功能
     * 同步方法直接返回结果
     * 异步方法需要传入一个 UnitSDKListene 对象, 在 listener.onCallBack(); 中处理回调返回的结果
     */

    public void release() { }

    public void login(UnitSDKListener listener) {
        String callbackId = addCallbackToMapAndGetCallbackId(listener);
        nativeLogin(callbackId);
    }

    public void payForProduct( Map<String, String> orderInfo, UnitSDKListener listener) {
        String callbackId = addCallbackToMapAndGetCallbackId(listener);
        nativePayForProduct(callbackId, orderInfo);
    }

    public void share(Map<String, String> shareInfo, UnitSDKListener listener) {
        String callbackId = addCallbackToMapAndGetCallbackId(listener);
        nativeShare(callbackId, shareInfo);
    }

    public void createShortcut(Map<String, String> shortcutInfo, UnitSDKListener listener) {
        String callbackId = addCallbackToMapAndGetCallbackId(listener);
        nativeCreateShortcut(callbackId, shortcutInfo);
    }

    public boolean isFunctionSupported(String functionName) {
        return nativeIsFunctionSupported(functionName);
    }

    public boolean isLogined() {
        return nativeIsLogined();
    }

    public String getChannelId() {
        return nativeGetChannelId();
    }

    public String getUserId() {
        return nativeGetUserId();
    }

    public String getOrderId() {
        return nativeGetOrderId();
    }

    // reset the state of Pay
    public void resetPayState() {
        nativeResetPayState();
    }


    /**
     * 扩展接口, 可以使用通用的方式尝试调用渠道提供的方法
     * 同步方式立即返回, 异步方式需要 new 一个 UnitSDKListener 传入来处理异步结果
     * 使用扩展接口的参数由于不确定类型, 需要将每个参数构建成一个 UnitSDKParam 对象传入
     * UnitSDKParam 重载了多个构造函数, 支持  bool int string float
     *
     * @param functionName  函数名称是必须的
     * @param params        参数依具体情况可选
     */

    public void callSyncFunction(String functionName, UnitSDKParam ...params) {
        if (params.length == 0) {
            nativeCallSyncFunction(functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            nativeCallSyncFunction(functionName, paramsList);
        }
    }

    public boolean callBoolFunction(String functionName, UnitSDKParam ...params) {
        if (params.length == 0) {
            return nativeCallSyncBoolFunction(functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            return nativeCallSyncBoolFunction(functionName, paramsList);
        }
    }

    public int  callIntFunction(String functionName, UnitSDKParam ...params) {
        if (params.length == 0) {
            return nativeCallSyncIntFunction(functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            return nativeCallSyncIntFunction(functionName, paramsList);
        }
    }

    public float callFloatFunction(String functionName, UnitSDKParam ...params) {
        if (params.length == 0) {
            return nativeCallSyncFloatFunction(functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            return nativeCallSyncFloatFunction(functionName, paramsList);
        }
    }

    public String callStringFunction(String functionName, UnitSDKParam ...params) {
        if (params.length == 0) {
            return nativeCallSyncStringFunction(functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            return nativeCallSyncStringFunction(functionName, paramsList);
        }
    }


    public void callAsyncFunction(String functionName, UnitSDKListener listener, UnitSDKParam ...params ) {
        String callbackId = addCallbackToMapAndGetCallbackId(listener);
        if (params.length == 0) {
            nativeCallAsyncFunction(callbackId, functionName, null);
        } else {
            ArrayList<UnitSDKParam> paramsList = new ArrayList<>();
            for (UnitSDKParam param : params) {
                paramsList.add(param);
            }
            nativeCallAsyncFunction(callbackId, functionName, paramsList);
        }
    }

    /**
     * 保存 listener, 生成一个随机 AlphaNum 字符串 callbackId 作为 key 将 listener 保存在 map 中
     * 返回 callbackId 用于之后取出 listener 使用
     */
    private static String addCallbackToMapAndGetCallbackId(UnitSDKListener listener) {
        String callbackId = Long.toHexString(Double.doubleToLongBits(Math.random()));
        _listenerMap.put(callbackId, listener);
        return callbackId;
    }

    /**
     * 根据 callbackId 从 map 中取出 listener
     * 执行 listener 的 onCallback 回调
     * 从 map 中去除 listener
     */
    private static void onAsyncFuncResult(int code, String msg, String callbackId) {
        UnitSDKListener listener = _listenerMap.get(callbackId);
        listener.onCallBack(code, msg);

        _listenerMap.remove(callbackId);
    }


    // JNI native method
    private static native void nativeInit(String appKey, String appSecret, String privateKey);
    private static native void nativeLogin(String callbackId);
    private static native void nativePayForProduct(String callbackId, Map<String, String> orderInfo);
    private static native void nativeShare(String callbackId, Map<String, String> shareInfo);
    private static native void nativeCreateShortcut(String callbackId, Map<String, String> shortcutInfo);
    private static native void nativeResetPayState();
    private static native void nativeSetDebugMode(boolean bDebug);

    private static native boolean nativeIsFunctionSupported(String functionName);
    private static native boolean nativeIsLogined();
    private static native String nativeGetChannelId();
    private static native String nativeGetUserId();
    private static native String nativeGetOrderId();

    private static native void nativeCallSyncFunction(String functionName, ArrayList<UnitSDKParam> params);
    private static native boolean nativeCallSyncBoolFunction(String functionName, ArrayList<UnitSDKParam> params);
    private static native int nativeCallSyncIntFunction(String functionName, ArrayList<UnitSDKParam> params);
    private static native float nativeCallSyncFloatFunction(String functionName, ArrayList<UnitSDKParam> params);
    private static native String nativeCallSyncStringFunction(String functionName, ArrayList<UnitSDKParam> params);

    private static native void nativeCallAsyncFunction(String callbackId, String functionName, ArrayList<UnitSDKParam> params);
}
