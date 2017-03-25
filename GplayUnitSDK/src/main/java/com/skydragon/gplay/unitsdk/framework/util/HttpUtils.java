package com.skydragon.gplay.unitsdk.framework.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.util.List;

public final class HttpUtils {
	private static int mTimeOut;
	private static final String TAG="HttpUtils";

	public static HttpResponse post(Context paramContext, String paramString,
			List<NameValuePair> paramArrayList, int timeOut) throws Exception {
		mTimeOut = timeOut;

		if (null != paramString)
            paramString = paramString.replaceAll(" ", "%20");
		else
			Log.e(TAG,"post: paramString is null");

		DefaultHttpClient httpClient = createHttpClient();
		HttpHost host = getProxyHttpHost(paramContext);
		if (host != null) {
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					host);
		} else {
			httpClient.getParams().removeParameter(
					ConnRoutePNames.DEFAULT_PROXY);
		}
		HttpPost httpPost = new HttpPost(paramString);
		// httpPost.addHeader("Content-Type","text/html; application/x-www-form-urlencoded; charset=UTF-8");

		if (!paramArrayList.isEmpty()) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					paramArrayList, HTTP.UTF_8);
			httpPost.setEntity(entity);
		}
		return httpClient.execute(httpPost);
	}

	public static HttpResponse get(Context paramContext, String paramString,
			int timeOut) throws Exception {
		mTimeOut = timeOut;
		paramString = paramString.replaceAll(" ", "%20");
		DefaultHttpClient httpClient = createHttpClient();
		HttpHost host = getProxyHttpHost(paramContext);
		if (host != null) {
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					host);
		} else {
			httpClient.getParams().removeParameter(
					ConnRoutePNames.DEFAULT_PROXY);
		}
		HttpRequest httpRequest = new HttpGet(paramString);
		httpRequest.addHeader("Content-Type",
				"text/html; application/x-www-form-urlencoded; charset=UTF-8");
		return httpClient.execute((HttpUriRequest) httpRequest);
	}

	public static HttpHost getProxyHttpHost(Context context) {
		if (context == null) {
			return null;
		}
		ConnectivityManager ConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = ConnMgr.getActiveNetworkInfo();
		String proxyHost = null;
		int proxyPort = 0;
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			} else {
				// GPRS: APN http proxy
				proxyHost = android.net.Proxy.getDefaultHost();
				proxyPort = android.net.Proxy.getDefaultPort();
			}
		}
		if (proxyHost != null) {
			return new HttpHost(proxyHost, proxyPort);
		} else {
			return null;
		}
	}

	private static DefaultHttpClient createHttpClient() {
		final SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		final HttpParams httpParams = createHttpParams();
		final ThreadSafeClientConnManager tccm = new ThreadSafeClientConnManager(
				httpParams, supportedSchemes);
		DefaultHttpClient client = new DefaultHttpClient(tccm, httpParams);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2,
				true));
		return client;
	}

	private static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, mTimeOut);
		HttpConnectionParams.setSoTimeout(params, mTimeOut);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, false);
		HttpClientParams.setRedirecting(params, false);
		ConnManagerParams.setMaxTotalConnections(params, 50);
		ConnManagerParams.setTimeout(params, mTimeOut);
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(20));
		return params;
	}
}
