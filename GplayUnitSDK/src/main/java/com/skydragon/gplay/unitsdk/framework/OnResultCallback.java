package com.skydragon.gplay.unitsdk.framework;


/**
 *
 */
public interface OnResultCallback {
	
	public void onSuccessed(int code, String ext);
	
	public void onFailed(int code, String msg);

}
