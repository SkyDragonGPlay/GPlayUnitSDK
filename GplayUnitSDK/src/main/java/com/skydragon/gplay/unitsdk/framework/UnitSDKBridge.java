package com.skydragon.gplay.unitsdk.framework;

import android.content.Context;

import com.skydragon.gplay.runtimeparams.ParamsOfOnActivityResult;
import com.skydragon.gplay.runtimeparams.ParamsOfOnNewIntent;
import com.skydragon.gplay.thirdsdk.IChannelSDKBridge;
import com.skydragon.gplay.thirdsdk.IChannelSDKServicePlugin;
import com.skydragon.gplay.unitsdk.bridge.IUnitSDKBridge;
import com.skydragon.gplay.unitsdk.bridge.IUnitSDKBridgeProxy;

import org.json.JSONObject;

import java.util.Map;

public final class UnitSDKBridge implements IUnitSDKBridge {

    @Override
    public void init(Context ctx, String channelId, JSONObject jsonGameInfo, IChannelSDKBridge bridge, IChannelSDKServicePlugin channelSDKServicePlugin) {
        UnitSDK.init(ctx, channelId, jsonGameInfo, bridge, channelSDKServicePlugin);
    }

    @Override
    public void setServerHostUrl(String host) {
        UnitSDK.setUnitSDKServerUrl(host);
    }

    @Override
    public Object invokeMethodSync(String method, Map<String, Object> args) {
        switch (method) {
            case "onPause": {
                UnitSDK.onPause();
                break;
            }
            case "onResume": {
                UnitSDK.onResume();
                break;
            }
            case "onStop": {
                UnitSDK.onStop();
                break;
            }
            case "onDestroy": {
                UnitSDK.onDestroy();
                break;
            }
            case "onNewIntent": {
                ParamsOfOnNewIntent params = new ParamsOfOnNewIntent();
                ParamsOfOnNewIntent.from(args, params);
                UnitSDK.onNewIntent(params.intent);
                break;
            }
            case "onActivityResult": {
                ParamsOfOnActivityResult params = new ParamsOfOnActivityResult();
                ParamsOfOnActivityResult.from(args, params);
                UnitSDK.onActivityResult(params.requestCode, params.resultCode, params.data);
                break;
            }
        }

        return null;
    }

    @Override
    public void setBridgeProxy(IUnitSDKBridgeProxy proxy) {
        UnitSDK.setUnitSDKBridgeProxy(proxy);
    }
}
