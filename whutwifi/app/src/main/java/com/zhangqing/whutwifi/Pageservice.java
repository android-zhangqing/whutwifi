package com.zhangqing.whutwifi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Pageservice {

	

	/**
	 * @author sky Email vipa1888@163.com QQ:840950105 获取当前的网络状态 -1：没有网络
	 *         1：WIFI网络2：wap网络3：net网络
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			Log.e("networkInfo.getExtraInfo()",
					"networkInfo.getExtraInfo() is "
							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
		}
		return netType;
	}

	/**
	 * 取网页源码
	 * 
	 * @param urlString
	 * @return
	 * @throws MalformedURLException
	 */

	public static String getHtml(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = StreamTool.read(inputStream);
			String html = new String(data, "UTF-8");
			return html;

		}
		// TODO Auto-generated method stub
		return null;
	}

}

class StreamTool {
	/**
	 * 流数据到字节集
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);

		}
		inputStream.close();
		return outputStream.toByteArray();
		// TODO Auto-generated method stub

	}

}
