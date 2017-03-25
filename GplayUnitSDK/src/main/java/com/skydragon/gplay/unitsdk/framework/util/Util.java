package com.skydragon.gplay.unitsdk.framework.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.skydragon.gplay.unitsdk.framework.UnitSDK;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Util {

	private final static String TAG = "Util";
	private static HttpWrapper sSdkHttpTask = null;

	private static Hashtable<String, String> mInfo = null;

	public static void pluginHttp(Context context,
			Hashtable<String, String> info, OnHttpListener listener) {
		try {
			String server = info.get("server_url");
			info.remove("server_url");
			// info = addData(context,info);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Iterator<String> it = info.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				params.add(new BasicNameValuePair(key, info.get(key)));

			}
			sSdkHttpTask = new HttpWrapper(context);
			final OnHttpListener curListener = listener;
			LogD(TAG, "send:" + params.toString());
			sSdkHttpTask.doPost(new OnHttpListener() {

				@Override
				public void onResponse(String response) {
					curListener.onResponse(response);
					sSdkHttpTask = null;
				}

				@Override
				public void onError() {
					curListener.onError();
					sSdkHttpTask = null;
				}

			}, params, server, 60000);

		} catch (Exception e) {
			LogE(TAG, "", e);
			listener.onError();
		}

	}

	public static String getMd5(String str) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] bytes = str.getBytes("UTF-8");
			byte[] digest2 = digest.digest(bytes);
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < digest2.length; i++) {
				int val = ((int) digest2[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (NoSuchAlgorithmException e) {
			LogE(TAG,"", e);
		} catch (UnsupportedEncodingException e) {
			LogE(TAG, "", e);
		}
		return str;
	}

    /**
     *
     * @Title: networkReachable
     * @Description: get status of network
     * @param @param ctx context
     * @param @return the status
     * @return boolean
     * @throws
     */
    public static boolean networkReachable(Context ctx) {
        boolean bRet = false;
        try {
            ConnectivityManager conn = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conn.getActiveNetworkInfo();
            bRet = (null == netInfo) ? false : netInfo.isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bRet;
    }

    private static void outputLog(int type, String tag, String msg) {
        UnitSDK.outputLog(type, tag, msg);
    }

    public static String logLevel() {
        return "verbose";
    }

    /**
     *
     * @Title: LogV
     * @Description: output the Verbose
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    @Deprecated
    public static void LogV(String tag, String msg) {
        outputLog(Log.VERBOSE, tag, msg);
    }

    /**
     *
     * @Title: logV
     * @Description: output the Verbose
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    public static void logV(String tag, String msg) {
        outputLog(Log.VERBOSE, tag, msg);
    }

    /**
     *
     * @Title: LogE
     * @Description: output the Exception
     * @param @param tag
     * @param @param msg information
     * @param @param e
     * @return void
     * @throws
     */
    @Deprecated
    public static void LogE(String tag, String msg) {
        outputLog(Log.ERROR, tag, msg);
    }

    /**
     *
     * @Title: logE
     * @Description: output the Exception
     * @param @param tag
     * @param @param msg information
     * @param @param e
     * @return void
     * @throws
     */
    public static void logE(String tag, String msg) {
        outputLog(Log.ERROR, tag, msg);
    }

    /**
     *
     * @Title: LogE
     * @Description: output the Exception
     * @param @param tag
     * @param @param msg information
     * @param @param e
     * @return void
     * @throws
     */
    @Deprecated
    public static void LogE(String tag, String msg, Exception e) {
        outputLog(Log.ERROR, tag, msg + "\n" + e.toString());
        e.printStackTrace();
    }

    /**
     *
     * @Title: logE
     * @Description: output the Exception
     * @param @param tag
     * @param @param msg information
     * @param @param e
     * @return void
     * @throws
     */
    public static void logE(String tag, String msg, Exception e) {
        outputLog(Log.ERROR, tag, msg + "\n" + e.toString());
        e.printStackTrace();
    }

    /**
     *
     * @Title: LogD
     * @Description: output the info in debug mode
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    @Deprecated
    public static void LogD(String tag, String msg) {
        outputLog(Log.DEBUG, tag, msg);
    }

    /**
     *
     * @Title: logD
     * @Description: output the info in debug mode
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    public static void logD(String tag, String msg) {
        outputLog(Log.DEBUG, tag, msg);
    }

    /**
     *
     * @Title: LogI
     * @Description: output the tips
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    @Deprecated
    public static void LogI(String tag, String msg) {
        outputLog(Log.INFO, tag, msg);
    }

    /**
     *
     * @Title: logI
     * @Description: output the tips
     * @param @param tag
     * @param @param msg information
     * @return void
     * @throws
     */
    public static void logI(String tag, String msg) {
        outputLog(Log.INFO, tag, msg);
    }

    /**
     *
     * @Title: getApplicationName
     * @Description: get AndroidMainfest.xml app_name
     * @return applicationName
     * @throws
     */
    public static String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = UnitSDK.getContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(UnitSDK.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager
                .getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     *
     * @Title: getApplicationVersion
     * @Description: get AndroidMainfest.xml versionName
     * @return versionName
     * @throws
     */
    public static String getApplicationVersion() {
        try {
            PackageManager packageManager = UnitSDK.getContext().getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(UnitSDK.getContext().getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            // : handle exception
        }
        return "";

    }

    /**
     *
     * @Title: getFileContentWithName
     * @Description: According to the file name for the file content
     * @param @param str
     * @param @return
     * @return String
     * @throws
     */
    public static String getFileContentWithName(String fileName) {
        String Result = "";
        Context sContext = UnitSDK.getContext();
        try {
            InputStreamReader inputReader = new InputStreamReader(sContext
                    .getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                Result += line;
        } catch (Exception e) {
            // PluginHelper.LogE(TAG, e.toString());
        }
        return Result;
    }

    /**
     *
     * @Title: getApkPath
     * @Description: get
     * @param @param str
     * @param @return
     * @return String
     * @throws
     */
    public static String getApkPath() {
        Context sContext = UnitSDK.getContext();
        return sContext.getApplicationInfo().sourceDir;
    }


    public static HashMap<String,String> json2StringMap(String jsonStr) {
        HashMap<String, String> map = new HashMap<String,String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return json2StringMap(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<String,String> json2StringMap(JSONObject jsonObject) {
        HashMap<String, String> map = new HashMap<String,String>();
        Iterator<String> iter = jsonObject.keys();
        while(iter.hasNext()) {
            String key = iter.next();
            map.put(key, jsonObject.optString(key));
        }
        return map;
    }

    public static HashMap<String,Object> json2Map(String jsonStr) {
        HashMap<String, Object> map = new HashMap<String,Object>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return json2Map(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<String,Object> json2Map(JSONObject jsonObject) {
        HashMap<String, Object> map = new HashMap<String,Object>();
        Iterator<String> iter = jsonObject.keys();
        while(iter.hasNext()) {
            String key = iter.next();
            map.put(key, jsonObject.opt(key));
        }
        return map;
    }

    public static String map2JsonStr(Map<String,Object> data) {
        JSONObject jsonObject = new JSONObject();
        Iterator<String> keys = data.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = data.get(key);
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static boolean isEmpty(String s) {
        return null == s || s.trim().equals("");
    }

    public static JSONObject toJsonObject(String jsonStr) {
        if(null == jsonStr || jsonStr.trim().equals("")) {
            jsonStr = "{}";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String ensurePathEndsWithSlash(String path) {
        if (TextUtils.isEmpty(path))
            return "";

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path;
    }
}
