package com.skydragon.gplay.unitsdk.bridge;

import android.content.Context;

import com.skydragon.gplay.thirdsdk.IChannelSDKBridge;
import com.skydragon.gplay.thirdsdk.IChannelSDKServicePlugin;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zhangjunfei on 16/1/26.
 */
public interface IUnitSDKBridge {

    void init(Context ctx, String channelId, JSONObject jsonGameInfo, IChannelSDKBridge bridge, IChannelSDKServicePlugin channelSDKServicePlugin);

    /**
     * 设置接口访问host地址
     * @param host
     */
    void setServerHostUrl(String host);

    // 同步方法调用
    Object invokeMethodSync(String method, Map<String, Object> args);

    // 设置Bridge代理类，用于Client层访问Host层的方法
    void setBridgeProxy(IUnitSDKBridgeProxy proxy);
}
