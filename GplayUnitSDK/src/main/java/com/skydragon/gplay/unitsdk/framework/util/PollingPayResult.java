package com.skydragon.gplay.unitsdk.framework.util;

import android.content.Context;
import android.util.Log;

import com.skydragon.gplay.thirdsdk.IChannelSDKServicePlugin;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * package : com.skydragon.gplay.unitsdk.framework.util
 *
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/5/18 18:56:20
 */
public final class PollingPayResult extends TimerTask {

    private static final String TAG = "PollingPayResult";
    private final int REPEAT_TIMES = 10;
    private int mPollingTimes;
    private Context mContext;
    private String mPollingPayResultUrl;
    private List<NameValuePair> mPostParams = new ArrayList<NameValuePair>();
    private PollingPayResultBackListener mPayResultBackListener;

    public static final int STATUS_PAY_WAITTING = 0;
    public static final int STATUS_PAY_ALREADY = 1;
    public static final int STATUS_PAY_FAILURE = 2;
    private static final int SCHEDULE_TIME = 3000;

    private PollingPayResult(Context context, String clientId, String clientSecret, String orderSn, String pollingUrl, final PollingPayResultBackListener listener){
        mPayResultBackListener = listener;
        mContext = context;
        mPollingPayResultUrl = pollingUrl;
        mPostParams.add(new BasicNameValuePair("client_id", clientId));
        mPostParams.add(new BasicNameValuePair("client_secret", clientSecret));
        mPostParams.add(new BasicNameValuePair("order_sn", orderSn));
    }

    public static void startPollingPayResult(Context context, String clientId, String clientSecret, String orderSn, String pollingUrl, final PollingPayResultBackListener listener){
        if(context == null || clientId == null
                || clientSecret == null || orderSn == null || listener == null) {
            Log.e(TAG, "PollingPayResult startPollingPayResult params error!");
            return;
        }

        new Timer().schedule(new PollingPayResult(context, clientId, clientSecret,
                orderSn, pollingUrl, listener), 100, SCHEDULE_TIME);
    }

    /*{ "result": {
            "status": "ok", "error": "","error_no": ""
        },"data": {
            "status": 1,//订单支付状态 0 待支付 1 已经支付 2 支付失败
            "client_status": 0, //客户端处理状态 0 未处理 1 已经处理 2 处理失败
            "user_id": "4dd3c0bce9eb78506911ba5155b182fc", //用户唯一标识（GUID）
            "product_amount": "100", "product_price": "50", "product_count": 2, "product_name": "至尊卡",
            "game_user_id": "22","server_id": "1","private_data": "1111"}
    }* */
    @Override
    public void run() {
        if (++ mPollingTimes > REPEAT_TIMES){
            cancel();
            return;
        }

        HttpWrapper httpTask = new HttpWrapper(mContext);
        httpTask.doPost(new OnHttpListener() {

            @Override
            public void onResponse(String response) {
                if(Util.isEmpty(response))
                    return;
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject jsonResult = json.optJSONObject("result");
                    JSONObject jsonData = json.getJSONObject("data");
                    String statusResult = jsonResult.optString("status");

                    if(statusResult.equals("ok")){
                        if(jsonData.optInt("status") == STATUS_PAY_ALREADY){
                            onResultBack(IChannelSDKServicePlugin.PAY_RESULT_SUCCESS, jsonData);
                        } else if(jsonData.optInt("status") == STATUS_PAY_FAILURE){
                            onResultBack(IChannelSDKServicePlugin.PAY_RESULT_FAIL, jsonData);
                        }else if(jsonData.optInt("status") == STATUS_PAY_WAITTING
                                && mPollingTimes == REPEAT_TIMES){
                            onResultBack(IChannelSDKServicePlugin.PAY_RESULT_FAIL, jsonData);
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "onError ! Polling PayResult parse result error!");
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "onError ! Polling PayResult something error happened!");
            }

        }, mPostParams, mPollingPayResultUrl, 60000);
    }

    // 结果回调
    private synchronized void onResultBack(int resultCode, JSONObject jsonObject) {
        // stop polling
        mPollingTimes = REPEAT_TIMES;
        cancel();
        // result callback
        if(mPayResultBackListener != null){
            mPayResultBackListener.onPayResultBack(resultCode, jsonObject);
            mPayResultBackListener = null;
        }
    }

    public interface PollingPayResultBackListener {
        public void onPayResultBack(int resultCode, JSONObject jsonObject);
    }
}
