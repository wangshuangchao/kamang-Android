package com.mugua.enterprise.util.httppost;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public class BaseModel {
	private String url;
	private IRequestCallBack iRequestCallBack;

	public BaseModel(String url) {
		this.url = url;
	}

	public void send(String str,IRequestCallBack iRequestCallBack) {
		this.iRequestCallBack = iRequestCallBack;
		new ReadJSONFeedTask(str).execute(new String(this.url));
	}

	private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
		private String str;

		private ReadJSONFeedTask(String str) {
			this.str = str;
		}

		protected String doInBackground(String... urls) {
			return this.readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result) {
			try {
				if (!result.equals("") && BaseModel.this.iRequestCallBack != null)
					BaseModel.this.iRequestCallBack.onRequestBack(result);
			} catch (Exception var3) {
			}
		}

		public String readJSONFeed(String URL) {
			//{"bid":"1","status":"ok"}、、字符串形式
			StringBuffer stringBuffer = new StringBuffer();
			HttpPost httpPost = new HttpPost(URL);
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);// 设置请求超时10秒
			HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000); // 设置等待数据超时10秒
			HttpConnectionParams.getSocketBufferSize(httpParams);
			try {

//				httpPost.addHeader("Content-Type", "application/json");
//				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

				httpPost.setEntity(new StringEntity(str, "UTF-8"));
				HttpResponse response = new DefaultHttpClient()
						.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int cas = statusLine.getStatusCode();
				if (cas == 200) {
					//Entity形式
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new
							InputStreamReader(content));
//					// String形式
//					String entity = EntityUtils.toString(response.getEntity());
//					String content = entity.toString();
//					BufferedReader reader = new BufferedReader(
//							new StringReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						stringBuffer.append(line);
					}
				} else {

				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return stringBuffer.toString();
		}
	}
}
