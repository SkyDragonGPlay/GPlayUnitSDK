package com.skydragon.gplay.unitsdk.framework.util;

/**
 * 
 * @ClassName: SdkHttpListener
 * @Description: the interface of callback in http
 * @author
 * @date 2014-5-5 上午11:35:41
 * 
 */
public interface OnHttpListener {

	public void onResponse(String response);

	public void onError();

}
