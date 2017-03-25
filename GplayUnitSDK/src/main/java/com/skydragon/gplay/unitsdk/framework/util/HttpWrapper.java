package com.skydragon.gplay.unitsdk.framework.util;

import android.app.ProgressDialog;
import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/***
 * 通过http访问应用服务器，获取http返回结果
 */
public final class HttpWrapper {

	private static final String TAG = "HttpWrapper";

	private OnHttpListener mListener;

	private List<NameValuePair> mKeyValueArray;

	private boolean mIsHttpPost;

	private Context mContext;

	private int mTimeOut;
	
	private boolean mIsRetry = false;

	private static ProgressDialog mProgress = null;

	public HttpWrapper(Context context) {
		mContext = context;
	}

	public void doPost(OnHttpListener listener,
			List<NameValuePair> keyValueArray, String url, int timeOut) {
		this.mListener = listener;
		this.mIsHttpPost = true;
		this.mKeyValueArray = keyValueArray;
		this.mTimeOut = timeOut;
		this.mIsRetry = false;
		executeHttp(mContext, url);

	}

	public void doGet(OnHttpListener listener, String url, int timeOut) {
		this.mListener = listener;
		this.mIsHttpPost = false;
		this.mTimeOut = timeOut;
		this.mIsRetry = false;
		executeHttp(mContext, url);
	}

	private void executeHttp(Context context, String uri) {

		final String curUri = uri;
		final Context curContext = context;
	
		new Thread() {
			@Override
			public void run() {
				try {
					HttpResponse response = null;
					if (mIsHttpPost) {
						response = HttpUtils.post(curContext, curUri,
								mKeyValueArray, mTimeOut);
					} else {
						response = HttpUtils.get(curContext, curUri, mTimeOut);
					}
					
					if (response != null) {
						int st = response.getStatusLine().getStatusCode();
						if (st != 200) {
                            Util.LogD(TAG,
                                    " http status" + String.valueOf(st));
							if (!mIsRetry) {
								mIsRetry = true;
								executeHttp(curContext, curUri);
								return;
							}
							mListener.onError();
							return;
						}
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							InputStream content = entity.getContent();
							if (content != null) {
								String result = convertStreamToString(content);
								Util.LogD(TAG, "response:"
										+ result);
								mListener.onResponse(result);
							}
						}
					} else {
						mListener.onError();
					}
				} catch (Exception e) {
					mListener.onError();
                    Util.LogE(TAG,"", e);
				}
			}
		}.start();
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
            Util.LogE(TAG,"", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
                Util.LogE(TAG,"", e);
			}
		}
		return sb.toString();
	}

}
