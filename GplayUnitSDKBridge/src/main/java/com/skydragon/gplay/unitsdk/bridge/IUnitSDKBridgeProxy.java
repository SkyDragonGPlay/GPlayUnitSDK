package com.skydragon.gplay.unitsdk.bridge;

import java.util.Map;

public interface IUnitSDKBridgeProxy {
    // 同步方法调用
    Object invokeMethodSync(String method, Map<String, Object> args);

    public void runOnGLThread(Runnable r);

}