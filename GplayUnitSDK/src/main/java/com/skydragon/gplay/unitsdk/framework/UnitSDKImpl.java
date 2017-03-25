package com.skydragon.gplay.unitsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.ValueCallback;

import com.skydragon.gplay.runtimeparams.ParamsOfInitUnitSDKParams;
import com.skydragon.gplay.runtimeparams.ParamsOfOutputLog;
import com.skydragon.gplay.runtimeparams.ParamsOfSetUnitSDKServerUrl;
import com.skydragon.gplay.thirdsdk.IChannelSDKBridge;
import com.skydragon.gplay.thirdsdk.IChannelSDKCallback;
import com.skydragon.gplay.thirdsdk.IChannelSDKServicePlugin;
import com.skydragon.gplay.unitsdk.framework.util.HttpWrapper;
import com.skydragon.gplay.unitsdk.framework.util.OnHttpListener;
import com.skydragon.gplay.unitsdk.framework.util.PollingPayResult;
import com.skydragon.gplay.unitsdk.framework.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class UnitSDKImpl implements IChannelSDKBridge {
    private static final String TAG = "UnitSDK";
    private static final String RESULT = "result";
    private static final String STATUS = "status";
    private static final String STATUS_OK = "ok";
    private static final String EXT = "ext";
    private static final String ERROR = "error";
    private static final int RESULT_OK = 0;

    private static final String CHANNEL_CODE = "channel_code";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";

    private String server_url = "http://api.skydragon-inc.cn";

    //登陆通知地址
    private static final String auth_url = "/user/UnitLoginOauth";

    //创建订单
    private static final String URL_GET_PAY_RESULT = "/order/payresult";

    private  static final String URL_GET_CHAREGE_CLIENT_STATUS = "/order/changeClientStatus";

    private  static final String URL_GET_UNSUCCESS_ORDER_LIST = "/order/unsuccessList";

    //创建订单
    private static final String URL_GET_ORDER_ID = "/pay/createOrder";

    //创建订单在渠道上的交易信息
    private  static final String URL_GET_CHAREGE = "/pay/createCharge";

    private IChannelSDKServicePlugin mChannelSDKServicePlugin;

    private Context mContext;

    private JSONObject mJsonGameInfo;

    private String mPrivateKey;
    private String mUApiKey;
    private String mUApiSecret;
    private String mChannelID;
    private String mLoginServerUrl;

    private String mUid;
    private String mMiniGameServerUrl;
    private boolean mIsLogined;

    private String sOrderId;

    Handler handler = new Handler( Looper.getMainLooper() );

    public UnitSDKImpl() {}

    @Override
    public void init(Context context, String channelId, JSONObject jsonGameInfo, IChannelSDKServicePlugin channelSDKServicePlugin) {
        mContext = context;
        mChannelID = channelId;
        mJsonGameInfo = jsonGameInfo;
        mChannelSDKServicePlugin = channelSDKServicePlugin;
        mLoginServerUrl = Util.ensurePathEndsWithSlash(server_url) + auth_url;
        mMiniGameServerUrl = jsonGameInfo.optString("mini_game_server", null);

        JSONObject jsonChannelConfig = jsonGameInfo.optJSONObject("channel_config");
        if(null == jsonChannelConfig)
            jsonChannelConfig = new JSONObject();
        mChannelSDKServicePlugin.init((Activity)context, jsonChannelConfig);
        mChannelSDKServicePlugin.setChannelSDKBridge(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityResultWrapper(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityPause();
    }

    @Override
    public void onResume() {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityNewIntent(intent);
    }

    @Override
    public void onStop() {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityStop();
    }

    @Override
    public void onDestroy() {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityDestroy();
    }

    @Override
    public void onRestart() {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        mChannelSDKServicePlugin.onActivityRestart();
    }


    @Override
    public void invokeMethodAsync(String method, String data, IChannelSDKCallback callback)
    {
        switch (method)
        {
            case "login":
                login(callback);
                break;
            case "pay":
                pay(data, callback);
                break;
            case "share":
                share(data, callback);
                break;
            case "createShortcut":
                createShortcut(callback);
                break;
            case "requestChargeInfo":
                requestChargeInfo(Util.toJsonObject(data), callback);
                break;
            case "checkUnSuccessOrders":
                checkUnSuccessOrders(callback);
                break;
            default:
                callAsynFunction(method, data, callback);
                break;
        }
    }

    @Override
    public String invokeMethodSync(String method, String jsonStr) {
        switch (method)
        {
            case "initUnitSDKParams": {
                HashMap args = Util.json2Map(jsonStr);
                ParamsOfInitUnitSDKParams params = new ParamsOfInitUnitSDKParams();
                ParamsOfInitUnitSDKParams.from(args, params);
                initUnitSDKParams(params.uApiKey, params.apiSecret, params.privateKey);
                break;
            }
            case "getChannelId": {
                return getChannelId();
            }
            case "isLogin": {
                return Boolean.toString(isLogin());
            }
            case "getUserID": {
                return getUserID();
            }
            case "getOrderId": {
                return getOrderId();
            }
            case "isFunctionSupported": {
                return isFunctionSupported(jsonStr);
            }
            case "outputLog": {
                HashMap args = Util.json2Map(jsonStr);
                ParamsOfOutputLog params = new ParamsOfOutputLog();
                ParamsOfOutputLog.from(args, params);
                outputLog(params.type, params.tag, params.msg);
                break;
            }
            case "setUnitSDKServerUrl": {
                HashMap args = Util.json2Map(jsonStr);
                ParamsOfSetUnitSDKServerUrl params = new ParamsOfSetUnitSDKServerUrl();
                ParamsOfSetUnitSDKServerUrl.from(args, params);
                setUnitSDKServerUrl(params.serverUrl);
                break;
            }
            default:
                return callSyncFunction(method,jsonStr);
        }

        return null;
    }

    private void initUnitSDKParams(String uApiKey, String apiSecret, String privateKey) {
        Log.d(TAG, "initUnitSDKParams: with key " + uApiKey + ", secret " + apiSecret + ", privateKey " + privateKey);
        mUApiKey = uApiKey;
        mPrivateKey = privateKey;
        mUApiSecret = apiSecret;
    }

    private String getChannelId() {
        if( null == mChannelID || mChannelID.trim().equals( "" ) ) {
            return "";
        }
        return mChannelID;
    }

    /***
     * 登陆
     ***/
    private void login(final IChannelSDKCallback callback) {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channel service plugin first!");
            return;
        }
        runOnMainThread(new Runnable() {
            public void run() {
                userLogin(new OnResultCallback() {
                    @Override
                    public void onSuccessed(final int code, final String ext) {
                        Log.d(TAG, "UnitSDK login success. code: " + code + ",ext: " + ext);
                        callback.onCallback(code, ext);
                    }

                    @Override
                    public void onFailed(final int code, final String msg) {
                        Log.d(TAG, "UnitSDK login failed.");
                        callback.onCallback(code, msg);
                    }
                });
            }
        });
    }

    /**
     * 确定是否已经登录
     * @return
     */
    private boolean isLogin() {
        return mIsLogined;
    }

    private String getUserID() {
        return mUid;
    }

    /**
     * 需要在UI线程中运行
     * 支付
     * @param strPayInfo 产品信息
     * @param callback
     */
    private void pay(String strPayInfo, final IChannelSDKCallback callback) {
        Log.d(TAG, "pay ");
        if (null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channel service plugin first!");
            return;
        }

        final HashMap<String, String> productInfo = Util.json2StringMap(strPayInfo);
        runOnMainThread(new Runnable() {
            public void run() {
                if (!Util.networkReachable(mContext)) {
                    callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_NETWORK_ERROR, "Network not available!");
                    return;
                }

                if (mIsLogined) {
                    getPayOrderId(productInfo, callback);
                } else {
                    userLogin(new OnResultCallback() {
                        @Override
                        public void onSuccessed(int code, String msg) {
                            getPayOrderId(productInfo, callback);
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "Login failed");
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取订单ID
     * @return
     */
    private String getOrderId() {
        return sOrderId;
    }

    /***
     * 分享
     * @param strShareInfo 分享信息
     * @param callback
     */
    private void share(final String strShareInfo, final IChannelSDKCallback callback) {
        Log.d(TAG, "share ");
        if (null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }

        try {
            final JSONObject jsonShareInfo = new JSONObject(strShareInfo);
            runOnMainThread(new Runnable() {
                public void run() {
                    if (!Util.networkReachable(mContext)) {
                        callback.onCallback(IChannelSDKServicePlugin.SHARE_RESULT_NETWORK_ERROR, "Network not available!");
                        return;
                    }

                    if (mIsLogined) {
                        doShare(jsonShareInfo, callback);
                    } else {
                        Log.d(TAG, "not logined, try to log in and share");
                        userLogin(new OnResultCallback() {
                            @Override
                            public void onSuccessed(int code, String msg) {
                                doShare(jsonShareInfo, callback);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                callback.onCallback(IChannelSDKServicePlugin.SHARE_RESULT_FAIL, "fail");
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 创建游戏的桌面快捷方式
     */
    private void createShortcut(final IChannelSDKCallback callback) {
        Log.d(TAG, "createShortcut: onEnter");
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        runOnMainThread(new Runnable() {
            public void run() {
                JSONObject jsonShortcut = null;
                try {
                    jsonShortcut = new JSONObject();
                    jsonShortcut.put("engine_type", mJsonGameInfo.optString("engine"));
                    jsonShortcut.put("engine_version", mJsonGameInfo.optString("engine_version"));
                    jsonShortcut.put("client_id", mJsonGameInfo.optString("client_id"));
                    jsonShortcut.put("orientation", mJsonGameInfo.optString("orientation"));
                    jsonShortcut.put("game_name", mJsonGameInfo.optString("name"));

                    String downloadUrl = mJsonGameInfo.optString("download_url");
                    String iconUrl = downloadUrl + "/icon/icon_small.png";
                    int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
                    if (densityDpi <= 240) {
                        //
                    } else if (densityDpi >= 480) {
                        iconUrl = downloadUrl + "/icon/icon_large.png";
                    } else {
                        iconUrl = downloadUrl + "/icon/icon_middle.png";
                    }
                    jsonShortcut.put("icon_url", iconUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mChannelSDKServicePlugin.createShortcut(jsonShortcut, new ValueCallback<JSONObject>() {
                    @Override
                    public void onReceiveValue(JSONObject value) {
                        int result = value.optInt(RESULT);
                        String msg = value.optString("msg");

                        if (result == IChannelSDKServicePlugin.SHORTCUT_RESULT_SUCCESS) {
                            callback.onCallback(IChannelSDKServicePlugin.SHORTCUT_RESULT_SUCCESS, msg);
                        } else {
                            callback.onCallback(IChannelSDKServicePlugin.SHORTCUT_RESULT_FAILED, "fail");
                        }
                    }
                });
            }
        });
    }

    /**
     * 是否支持某个函数
     * @param method 函数名称
     * @return true 表示支持这个函数 false  表示不支持这个函数
     */
    private String isFunctionSupported(String method) {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return "false";
        }
        boolean b= mChannelSDKServicePlugin.isFunctionSupported(method);
        return b?"true":"false";
    }

    /**
     * 调用同步函数
     * @param method 要调用的方法名称
     * @param jsonStr 参数
     * @return
     */
    private String callSyncFunction(String method, String jsonStr ) {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return null;
        }
        return mChannelSDKServicePlugin.invokeSyncExtraMethods(method, Util.toJsonObject(jsonStr));
    }

    /**
     * 调用异步函数
     * @param method 要调用方法的名称
     * @param jsonStr 参数
     */
    private void callAsynFunction(String method, final String jsonStr, final IChannelSDKCallback callback )
    {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            if(null != callback) {
                callback.onCallback(-1,"ChannelSDKProxy is null!");
            }
            return;
        }
        mChannelSDKServicePlugin.invokeAsynExtraMethods(method, Util.toJsonObject(jsonStr), new IChannelSDKCallback() {
            @Override
            public void onCallback(int resultCode, String resultJsonMsg) {
                callback.onCallback(resultCode, resultJsonMsg);
            }
        });
    }

    private void outputLog(int type, String tag, String msg) {
        UnitSDKNativeWrapper.nativeOutputLog(type, tag, msg);
    }

    private void getAccessToken(JSONObject jsonChannelData, OnHttpListener listener) {
        try {
            jsonChannelData.put(CHANNEL_CODE, mChannelID);
            jsonChannelData.put(CLIENT_ID, mUApiKey);
            jsonChannelData.put(CLIENT_SECRET, mUApiSecret);

            List<NameValuePair> params = new ArrayList<>();
            Iterator<String> it = jsonChannelData.keys();
            while (it.hasNext()) {
                String key = it.next();
                params.add(new BasicNameValuePair(key, jsonChannelData.optString(key)));
            }
            HttpWrapper task = new HttpWrapper(mContext);
            final OnHttpListener curListener = listener;

            task.doPost(new OnHttpListener() {

                @Override
                public void onResponse(String response) {
                    curListener.onResponse(response);
                }

                @Override
                public void onError() {
                    curListener.onError();
                }

            }, params, mLoginServerUrl, 60000);

        } catch (Exception e) {
            listener.onError();
        }

    }

    private void userLogin(final OnResultCallback callback ) {
        Log.d(TAG, "userLogin");
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        mChannelSDKServicePlugin.login(jsonObject, new ValueCallback<JSONObject>() {
            @Override
            public void onReceiveValue(JSONObject value) {
                int result = value.optInt(RESULT);
                String msg = value.optString("msg");
                if (result == IChannelSDKServicePlugin.USER_LOGIN_RESULT_SUCCESS) {
                    final JSONObject jsonChannelData = value.optJSONObject("data");
                    getAccessToken(jsonChannelData, new OnHttpListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, "userLogin#mChannelSDKServicePlugin#onResponse  " + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                JSONObject jsonResult = jsonObj.optJSONObject(RESULT);
                                String status = jsonResult.getString(STATUS);
                                if (status.equals(STATUS_OK)) {
                                    mIsLogined = true;
                                    JSONObject jsonData = jsonObj.optJSONObject("data");
                                    JSONObject jsonUserData = jsonData.optJSONObject("user_data");
                                    jsonUserData.put("gtoken", jsonData.optString("gtoken"));
                                    jsonUserData.put("ext", jsonData.optJSONObject("ext"));

                                    String guid = jsonUserData.optString("guid");
                                    Log.d(TAG, "onResponse: Guid is " + guid);
                                    mUid = guid;

                                    callback.onSuccessed(IChannelSDKServicePlugin.USER_LOGIN_RESULT_SUCCESS, jsonUserData.toString());
                                } else {
                                    mIsLogined = false;
                                    String msg = jsonResult.optString(ERROR);
                                    callback.onFailed(IChannelSDKServicePlugin.USER_LOGIN_RESULT_FAIL, msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onFailed(IChannelSDKServicePlugin.USER_LOGIN_RESULT_FAIL, ERROR);
                            }
                        }

                        @Override
                        public void onError() {
                            callback.onFailed(IChannelSDKServicePlugin.USER_LOGIN_RESULT_FAIL, ERROR);
                        }
                    });
                } else {
                    callback.onFailed(result, msg);
                }
            }
        });
    }

    private void getPayOrderId(final HashMap<String, String> info, final IChannelSDKCallback callback) {
        try {
            HashMap<String, String> orderInfo = getOrderInfo(info);
            if ( null == orderInfo ) {
                Log.w(TAG, "getPayOrderId: orderInfo get from info is null");
                callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_INVALID, "something is null");
            }

            getPayOrderId(mContext, orderInfo, new OnHttpListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d(TAG, "getPayOrderId response: " + response);
                        JSONObject json = new JSONObject(response);
                        JSONObject jsonResult = json.optJSONObject(RESULT);
                        String status = jsonResult.optString(STATUS);
                        if (status.equals("ok")) {
                            JSONObject jsonData = json.getJSONObject("data");
                            sOrderId = jsonData.getString("order_sn");
                            info.put(IChannelSDKServicePlugin.ORDER_ID, sOrderId);
                            runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    payOnline(info, callback);
                                }
                            });
                        } else {
                            callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "getPayOrderId faild");
                        }
                    } catch (JSONException e) {
                        callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "getPayOrderId onResponse error");
                    }
                }

                @Override
                public void onError() {
                    callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "getPayOrderId->onError");
                }
            });
        }
        catch(Exception e) {
            callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "Error during getPayOrderId");
        }
    }

    private void payOnline(HashMap<String, String> payOnlineInfo, final IChannelSDKCallback callback) {
        if(null == mChannelSDKServicePlugin) {
            Log.e(TAG, "Must invoke setChannelSDKServicePlugin to set channelSDKProxy first!");
            return;
        }
        String productPrice = payOnlineInfo.get("product_price");
        String productCount = payOnlineInfo.get("product_count");
        final String orderSn = payOnlineInfo.get(IChannelSDKServicePlugin.ORDER_ID);

        float price = Float.parseFloat(productPrice);
        int count = Integer.parseInt(productCount);
        Float money = price*count;
        int amount = money.intValue();
        if (amount<1) {
            amount = 1;
        }
        String amountStr = amount+"";
        payOnlineInfo.put("product_amount", amountStr);
        JSONObject jsonObject = new JSONObject(payOnlineInfo);
        mChannelSDKServicePlugin.pay(jsonObject, new ValueCallback<JSONObject>() {
            @Override
            public void onReceiveValue(JSONObject value) {
                int result = value.optInt(RESULT);
                String msg = value.optString("msg");
                if (result == RESULT_OK) {

                    // 此 mMiniGameServerUrl 可以用于标记是否单机充值，代表我们提供的回调服务器地址
                    if(mMiniGameServerUrl != null){
                        // single player model
                        pollingPayResult(orderSn, callback);
                    } else {
                        callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_SUCCESS, "");
                    }

                } else {
                    callback.onCallback(result, msg);
                }
            }
        });
    }

    // 提供给cp的单机充值未完成订单查询接口
    public void checkUnSuccessOrders(final IChannelSDKCallback callback){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", mUApiKey));
        params.add(new BasicNameValuePair("client_secret", mUApiSecret));
        params.add(new BasicNameValuePair("user_id", mUid));
        HttpWrapper httpTask = new HttpWrapper(mContext);
        httpTask.doPost(new OnHttpListener() {
            @Override
            public void onResponse(String response) {
                if(Util.isEmpty(response)) {
                    Log.e(TAG, "searchUnSuccessOrders response is empty!");
                }
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject jsonResult = json.optJSONObject(RESULT);
                    String statusResult = jsonResult.optString(STATUS);
                    if(statusResult.equals(STATUS_OK)){
                        JSONArray jsonDataArray = json.getJSONArray("data");
                        String orderSns = null;
                        for(int i = 0; i < jsonDataArray.length(); i ++){
                            JSONObject orderData = jsonDataArray.getJSONObject(i);
                            if(orderSns == null){
                                orderSns = orderData.getString("order_sn");
                            } else {
                                orderSns += "," + orderData.getString("order_sn");
                            }
                        }
                        changeClientStatus(orderSns, callback, jsonDataArray.toString());
                    } else {
                        callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_FAIL, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError() {}
        }, params, getUnsuccessListUrl(), 60000);
    }

    // 单机游戏充值结束时对服务端进行游戏结果轮询
    private void pollingPayResult(final String orderSn, final IChannelSDKCallback callback) {
        PollingPayResult.startPollingPayResult(mContext, mUApiKey, mUApiSecret, orderSn,
                getPayResultUrl(), new PollingPayResult.PollingPayResultBackListener() {
                    @Override
                    public void onPayResultBack(int resultCode, JSONObject jsonObject) {
                        if(jsonObject == null){
                            Log.e(TAG, "onPayResultBack ! Polling PayResult error!");
                            return;
                        }
                        JSONArray resultArray = new JSONArray();
                        try {
                            jsonObject.put("order_sn", orderSn);
                            resultArray.put(jsonObject);
                        } catch (JSONException e) {
                            Log.e(TAG, "onError ! onPayResultBack add order_sn error!");
                        }

                        if(resultCode == IChannelSDKServicePlugin.PAY_RESULT_SUCCESS){
                            changeClientStatus(orderSn, callback, resultArray.toString());
                        } else {
                            callback.onCallback(resultCode, orderSn);
                        }
                    }
                });
    }

    // 更改订单客户端状态
    // orderSns : "order1, order2, order3, ..."
    /* 支付结果内容
    [{ "order_sn":"201602261020193" //订单号
    "product_amount": "100",
    "product_price": "50",
    "product_count": 2,
    "product_name": "至尊卡",
    "server_id": "1", //服务器ID
    "private_data": "111"//开发者透传的私有数据  }, ... ]* */
    private void changeClientStatus(final String orderSns, final IChannelSDKCallback callback, final String orderResultArray){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", mUApiKey));
        params.add(new BasicNameValuePair("client_secret", mUApiSecret));
        params.add(new BasicNameValuePair("order_sn", orderSns));
        params.add(new BasicNameValuePair("client_status", String.valueOf(PollingPayResult.STATUS_PAY_ALREADY)));
        HttpWrapper httpTask = new HttpWrapper(mContext);
        httpTask.doPost(new OnHttpListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject jsonResult = json.optJSONObject(RESULT);
                    String statusResult = jsonResult.optString(STATUS);
                    if(statusResult.equals(STATUS_OK)){
                        callback.onCallback(IChannelSDKServicePlugin.PAY_RESULT_SUCCESS, orderResultArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {}
        }, params, getChargeClientStatusUrl(), 60000);
    }

    private void getPayOrderId(Context context,HashMap<String, String> info, OnHttpListener listener) {
        try {
            String sign = cryptProductInfo(info);

            Log.d(TAG, "getPayOrderId sign: " + sign );

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("sign", sign));
            Set<String> keys = info.keySet();
            for(String key : keys) {
                params.add(new BasicNameValuePair(key, info.get(key)));
            }
            HttpWrapper httpTask = new HttpWrapper(context);
            final OnHttpListener curListener = listener;
            String url = getOrderIdUrl();
            httpTask.doPost(new OnHttpListener() {

                @Override
                public void onResponse(String response) {
                    curListener.onResponse(response);
                }

                @Override
                public void onError() {
                    curListener.onError();
                }

            }, params, url, 60000);
        } catch (Exception e) {
            listener.onError();
        }
    }


    private HashMap<String, String> getOrderInfo(HashMap<String, String> productInfo) {
        HashMap<String, String> orderInfo = null;
        do {
            try {
                String id = productInfo.get(IChannelSDKServicePlugin.PRODUCT_ID);
                String strName = productInfo.get(IChannelSDKServicePlugin.PRODUCT_NAME);
                String strPrice = productInfo.get(IChannelSDKServicePlugin.PRODUCT_PRICE);
                String strCount = productInfo.get(IChannelSDKServicePlugin.PRODUCT_COUNT);
                String strRoleId = productInfo.get(IChannelSDKServicePlugin.GAME_ROLE_ID);
                String strRoleName = productInfo.get(IChannelSDKServicePlugin.GAME_ROLE_NAME);
                String strServerId = productInfo.get(IChannelSDKServicePlugin.SERVER_ID);
                String strServerName = productInfo.get(IChannelSDKServicePlugin.SERVER_NAME);

                if ( id == null ||
                        strName == null ||
                        strPrice == null ||
                        strCount == null ||
                        strRoleId == null ||
                        strRoleName == null ||
                        strServerId == null ||
                        strServerName == null )
                {
                    break;
                }

                int count = Integer.parseInt(strCount) < 1 ? 1 : Integer.parseInt(strCount);
                int money = Integer.parseInt(strPrice) * count;

                orderInfo = new HashMap<>();
                orderInfo.put(IChannelSDKServicePlugin.APPKEY, mUApiKey);
                orderInfo.put(IChannelSDKServicePlugin.PRODUCT_AMOUNT, String.valueOf(money));

                productInfo.put(IChannelSDKServicePlugin.PRODUCT_AMOUNT, String.valueOf(money));

                orderInfo.put(IChannelSDKServicePlugin.PRODUCT_ID, id);
                orderInfo.put(IChannelSDKServicePlugin.PRODUCT_NAME, strName);
                orderInfo.put(IChannelSDKServicePlugin.PRODUCT_PRICE, strPrice);
                orderInfo.put(IChannelSDKServicePlugin.PRODUCT_COUNT, strCount);
                orderInfo.put(IChannelSDKServicePlugin.GAME_ROLE_ID, strRoleId);
                orderInfo.put(IChannelSDKServicePlugin.GAME_ROLE_NAME, strRoleName);
                orderInfo.put(IChannelSDKServicePlugin.SERVER_ID, strServerId);
                orderInfo.put(IChannelSDKServicePlugin.SERVER_NAME, strServerName);

                orderInfo.put(IChannelSDKServicePlugin.CHANNEL_USER_ID, mUid);
                orderInfo.put(IChannelSDKServicePlugin.CHANNEL_CODE, mChannelID);

                String sPrivateData = productInfo.get(EXT);

                Log.d(TAG, "getOrderInfo: " + sPrivateData);
                if (sPrivateData != null && !sPrivateData.trim().equals("")) {
                    String sDecodeData = Base64.encodeToString(sPrivateData.getBytes(), Base64.DEFAULT).trim();
                    orderInfo.put(IChannelSDKServicePlugin.EXT, sDecodeData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        } while (false);

        return orderInfo;
    }


    private void setUnitSDKServerUrl(String serverUrl) {
        server_url = serverUrl;
    }

    private void doShare(JSONObject jsonObject, final IChannelSDKCallback callback) {
        mChannelSDKServicePlugin.share(jsonObject, new ValueCallback<JSONObject>() {
            @Override
            public void onReceiveValue(JSONObject value) {
                int result = value.optInt(RESULT);
                String msg = value.optString("msg");
                if (result == IChannelSDKServicePlugin.SHARE_RESULT_SUCCESS) {
                    callback.onCallback(IChannelSDKServicePlugin.SHARE_RESULT_SUCCESS, msg);
                } else {
                    callback.onCallback(IChannelSDKServicePlugin.SHARE_RESULT_FAIL, "fail");
                }
            }
        });
    }

    private String cryptProductInfo(HashMap<String, String> products) {
        Set<String> setKeys = products.keySet();
        ArrayList<String> listKeys = new ArrayList<>(setKeys);
        Collections.sort(listKeys, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        StringBuilder sb = new StringBuilder();
        for(String key : listKeys) {
            Log.d(TAG, "cryptProductInfo key: " + key);
            sb.append(products.get(key));
        }

        String md51 = Util.getMd5(sb.toString());
        String str2 = md51 + mPrivateKey;
        return Util.getMd5(str2);
    }

    public void requestChargeInfo(JSONObject jsonData, final IChannelSDKCallback callback) {
        List<NameValuePair> params = new ArrayList<>();
        Iterator<String> iter = jsonData.keys();
        while(iter.hasNext()) {
            String key = iter.next();
            params.add(new BasicNameValuePair(key, jsonData.optString(key)));
        }

        HttpWrapper httpWrapper = new HttpWrapper(mContext);
        httpWrapper.doPost(new OnHttpListener() {
            @Override
            public void onError() {
                if (callback != null)
                    callback.onCallback(IChannelSDKServicePlugin.PAY_REQUEST_CHARGE_FAILED, "unknown error!!!");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonResult = jsonObject.optJSONObject(RESULT);
                    if (jsonResult != null) {
                        String status = jsonResult.optString(STATUS);
                        if (!TextUtils.isEmpty(status) && status.contentEquals(STATUS_OK)) {
                            JSONObject jsonData = jsonObject.optJSONObject("data");
                            if (jsonData != null) {
                                String strBase64Charge = jsonData.optString("channel_charge");
                                byte[] strCharge = Base64.decode(strBase64Charge, Base64.DEFAULT);
                                if (callback != null)
                                    callback.onCallback(IChannelSDKServicePlugin.PAY_REQUEST_CHARGE_SUCCESS, new String(strCharge));
                            }
                        } else {
                            if (callback != null)
                                callback.onCallback(IChannelSDKServicePlugin.PAY_REQUEST_CHARGE_FAILED, jsonResult.optString("error"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, getChargeInfoUrl(), 5000);
    }

    private void runOnMainThread( Runnable r ) {
        handler.post(r);
    }

    public String getPayResultUrl(){
        return server_url + URL_GET_PAY_RESULT;
    }

    private String getOrderIdUrl() {
        return server_url + URL_GET_ORDER_ID;
    }

    private String getUnsuccessListUrl() {
        return server_url + URL_GET_UNSUCCESS_ORDER_LIST;
    }

    private String getChargeClientStatusUrl() {
        return server_url + URL_GET_CHAREGE_CLIENT_STATUS;
    }

    private String getChargeInfoUrl() {
        return server_url + URL_GET_CHAREGE;
    }
}
