package com.skydragon.unitsdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skydragon.gplay.channel.plugin.h5.GplayChannelH5PluginProxy;
import com.skydragon.gplay.thirdsdk.IChannelSDKCallback;
import com.skydragon.gplay.unitsdk.framework.UnitSDKImpl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * package : com.skydragon.testandroid
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/5/6 17:29.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private UnitSDKImpl mUnitSDKImpl = new UnitSDKImpl();
    private GplayChannelH5PluginProxy h5PluginProxy = new GplayChannelH5PluginProxy();
    String client_id = "573d630ec12a6";
    String client_secret = "Z6__Fpvm6bpfbTSAtQjQhnK7tDZPeUkr";
    String privateKey = "gM13JLLoiceTxoSEyed2ouR060oyOMaP";
    String uid = "gM13JLLoiceTxoSEyed2ouR060oyOMaP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.unit_sdk_main_activity);

/*
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_price", "11.1");
        params.put("product_count", "1");
        mUnitSDKImpl.payOnline(params, new IChannelSDKCallback() {
            @Override
            public void onCallback(int resultCode, String resultJsonMsg) {
                Log.v("", "IIkkII  resultCode = " + resultCode + "   ,  resultJsonMsg : " + resultJsonMsg);
            }
        });
*/
    }

    public void onTest1(View view){
        JSONObject jsonGameConfig = new JSONObject();
        try {
            jsonGameConfig.put("client_id", client_id);
            jsonGameConfig.put("client_secret", client_secret);
            jsonGameConfig.put("orientation", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUnitSDKImpl.init(this, "666666", jsonGameConfig , h5PluginProxy);
        final JSONObject jsonObject = new JSONObject();
    }

    public void onTest2(View view){
    }


    public void onTest3(View view){
        mUnitSDKImpl.checkUnSuccessOrders(new IChannelSDKCallback() {
            @Override
            public void onCallback(int resultCode, String resultJsonMsg) {
                Log.v("", "IIkkII  resultCode = " + resultCode + "   ,  resultJsonMsg : " + resultJsonMsg);
            }
        });
    }
}
