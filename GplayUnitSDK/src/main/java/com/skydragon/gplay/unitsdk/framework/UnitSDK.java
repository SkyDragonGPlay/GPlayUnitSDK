package com.skydragon.gplay.unitsdk.framework;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.skydragon.gplay.runtimeparams.ParamsOfInitUnitSDKParams;
import com.skydragon.gplay.runtimeparams.ParamsOfOutputLog;
import com.skydragon.gplay.runtimeparams.ParamsOfSetUnitSDKServerUrl;
import com.skydragon.gplay.thirdsdk.IChannelSDKBridge;
import com.skydragon.gplay.thirdsdk.IChannelSDKCallback;
import com.skydragon.gplay.thirdsdk.IChannelSDKServicePlugin;
import com.skydragon.gplay.unitsdk.bridge.IUnitSDKBridgeProxy;

import org.json.JSONObject;

public final class UnitSDK {

    public static void init(Context ctx, String channelId, JSONObject jsonGameInfo, IChannelSDKBridge bridge, IChannelSDKServicePlugin channelSDKServicePlugin) {
        sContext = ctx;
        sChannelSDKBridge = bridge;
        if(null == sChannelSDKBridge) {
            sChannelSDKBridge = new UnitSDKImpl();
        }
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.init(ctx, channelId, jsonGameInfo, channelSDKServicePlugin);
        }
    }

    private static final String TAG = "UnitSDK";

    public static final String JSON_PARAMS = "json_params";

    private static IUnitSDKBridgeProxy sUnitSDKBridgeProxy;

    private static IChannelSDKBridge sChannelSDKBridge = null;

    private static Context sContext;

    public static void initUnitSDKParams(String uApiKey, String apiSecret, String privateKey) {
        if(checkChannelSDKBridge()) {
            ParamsOfInitUnitSDKParams params = new ParamsOfInitUnitSDKParams();
            params.uApiKey = uApiKey;
            params.apiSecret = apiSecret;
            params.privateKey = privateKey;
            sChannelSDKBridge.invokeMethodSync("initUnitSDKParams", params.toJsonStr());
        }
    }

    public static void setUnitSDKBridgeProxy(IUnitSDKBridgeProxy bridgeProxy) {
        sUnitSDKBridgeProxy = bridgeProxy;
        UnitSDKNativeWrapper.setUnitSDKBridgeProxy(bridgeProxy);
    }

    public static void setUnitSDKServerUrl(String serverUrl) {
        if(checkChannelSDKBridge()) {
            ParamsOfSetUnitSDKServerUrl params = new ParamsOfSetUnitSDKServerUrl();
            params.serverUrl = serverUrl;
            sChannelSDKBridge.invokeMethodSync("setUnitSDKServerUrl", params.toJsonStr());
        }
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getChannelId() {
        if(checkChannelSDKBridge()) {
            return sChannelSDKBridge.invokeMethodSync("getChannelId", "{}");
        }
        return null;
    }

    /***
     * 登陆
     ***/
    public static void login(final String callbackId) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync("login", "{}", new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                    }
                }
            });
        }
    }

    /**
     * 确定是否已经登录
     * @return
     */
    public static boolean isLogin() {
        if(checkChannelSDKBridge()) {
            String isLogin =  sChannelSDKBridge.invokeMethodSync("isLogin", "{}");
            return isLogin.toLowerCase().equals( "true");
        }
        return false;
    }

    public static String getUserID() {
        if(checkChannelSDKBridge()) {
            return (String) sChannelSDKBridge.invokeMethodSync("getUserID", "{}");
        }
        return null;
    }

    /**
     * 需要在UI线程中运行
     * 支付
     * @param strProductInfo 产品信息
     */
    public static void pay(final String callbackId, final String strProductInfo) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync("pay", strProductInfo, new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                    }
                }
            });
        }
    }

    /***
     * 登陆
     ***/
    public static void checkUnSuccessOrders(final String callbackId) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync("checkUnSuccessOrders", "{}", new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                    }
                }
            });
        }
    }

    /**
     * 获取订单ID
     * @return
     */
    public static String getOrderId() {
        if (checkChannelSDKBridge()) {
            return sChannelSDKBridge.invokeMethodSync("getOrderId", "{}");
        } else {
            return null;
        }
    }

    /***
     * 分享
     * @param strShareInfo 分享信息
     */
    public static void share(final String callbackId, final String strShareInfo) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync("share", strShareInfo, new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                    }
                }
            });
        }
    }

    /***
     * 创建游戏的桌面快捷方式
     */
    public static void createShortcut(final String callbackId) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync("createShortcut", "{}", new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackId);
                    }
                }
            });
        }
    }

    /**
     * 是否支持某个函数
     * @param method 函数名称
     * @return "true" 表示支持这个函数 "false" 表示不支持这个函数
     */
    public static String isFunctionSupported(String method) {
        if(checkChannelSDKBridge()) {
            return (String) sChannelSDKBridge.invokeMethodSync("isFunctionSupported", method);
        }
        return "false";

    }

    /**
     * 调用同步函数
     * @param method 要调用的方法名称
     * @param jsonStr 参数 方法用到的参数，按参数的顺序存放
     * @return
     */
    public static String callSyncFunction(final String method, final String jsonStr) {
        if (checkChannelSDKBridge()) {
            String strRet = sChannelSDKBridge.invokeMethodSync(method, jsonStr);
            if (null == strRet) {
                Log.w(TAG, "The method " + method + " return null!!!");
                strRet = "";
            }
            return strRet;
        }
        return null;
    }

    public static void callAsynFunctionFromNative(final String method, String jsonStr, final String callbackid) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync(method, jsonStr, new IChannelSDKCallback() {
                @Override
                public void onCallback(final int resultCode, final String resultJsonMsg) {
                    if (null != sUnitSDKBridgeProxy) {
                        sUnitSDKBridgeProxy.runOnGLThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackid);
                                    }
                                });
                    } else {
                        UnitSDKNativeWrapper.nativeAsyncActionResult(resultCode, resultJsonMsg, callbackid);
                    }
                }
            });
        }
    }

    /**
     * 调用异步函数
     * @param method 要调用方法的名称
     * @param callback 回调接口
     */
    public static void callAsynFunction(String method, String jsonStr, final IChannelSDKCallback callback )
    {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.invokeMethodAsync(method, jsonStr, callback);
        }
    }

    public static void outputLog(int type, String tag, String msg) {
        if(checkChannelSDKBridge()) {
            ParamsOfOutputLog params = new ParamsOfOutputLog();
            params.type = type;
            params.tag = tag;
            params.msg = msg;
            sChannelSDKBridge.invokeMethodSync("outputLog", params.toJsonStr());
        }
    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void onPause() {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onPause();
        }
    }

    public static void onResume() {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onResume();
        }
    }

    public static void onNewIntent(Intent intent) {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onNewIntent(intent);
        }
    }

    public static void onStop() {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onStop();
        }
    }

    public static void onDestroy() {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onDestroy();
        }
    }

    public static void onRestart() {
        if(checkChannelSDKBridge()) {
            sChannelSDKBridge.onRestart();
        }
    }

    private static boolean checkChannelSDKBridge() {
        if(null == sChannelSDKBridge) {
            Log.e(TAG, "Please invoke init method first!");
            return false;
        }
        return true;
    }

}
