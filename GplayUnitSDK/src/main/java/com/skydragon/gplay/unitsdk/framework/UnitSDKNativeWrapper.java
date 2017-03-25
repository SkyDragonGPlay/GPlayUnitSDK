package com.skydragon.gplay.unitsdk.framework;

import android.util.Log;

import com.skydragon.gplay.unitsdk.bridge.IUnitSDKBridgeProxy;

import java.util.HashMap;
import java.util.Map;

public final class UnitSDKNativeWrapper {
    private static IUnitSDKBridgeProxy mUnitSDKBridgeProxy;
    private static final String TAG = "UnitSDKNativeWrapper";

    public static void setUnitSDKBridgeProxy(IUnitSDKBridgeProxy bridgeProxy) {
        mUnitSDKBridgeProxy = bridgeProxy;
    }

    public static void nativeAsyncActionResult(int ret, String msg, String callbackId) {
        Log.d(TAG, "nativeAsyncActionResult: called");
        if(null != mUnitSDKBridgeProxy) {
            Map<String,Object> params = new HashMap<>();
            params.put("ret", ret);
            params.put("msg", msg);
            params.put("callbackid", callbackId);
            mUnitSDKBridgeProxy.invokeMethodSync("onAsyncActionResult", params);
        }
    }

    public static void nativeOutputLog(int type, String tag, String msg) {
        if(null != mUnitSDKBridgeProxy) {
            Map<String,Object> params = new HashMap<>();
            params.put("type", type);
            params.put("tag", tag);
            params.put("msg", msg);
            mUnitSDKBridgeProxy.invokeMethodSync("outputLog", params);
        }
    }
}
